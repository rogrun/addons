/**
 * Copyright (c) 2021-2023 Contributors to the SmartHome/J project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.smarthomej.binding.viessmann.internal.handler;

import static org.smarthomej.binding.viessmann.internal.ViessmannBindingConstants.*;

import java.time.Instant;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.jetty.client.HttpClient;
import org.openhab.core.config.core.Configuration;
import org.openhab.core.library.types.DecimalType;
import org.openhab.core.library.types.OnOffType;
import org.openhab.core.library.types.StringType;
import org.openhab.core.storage.Storage;
import org.openhab.core.thing.*;
import org.openhab.core.thing.binding.BaseBridgeHandler;
import org.openhab.core.thing.binding.BridgeHandler;
import org.openhab.core.thing.binding.ThingHandler;
import org.openhab.core.thing.binding.ThingHandlerService;
import org.openhab.core.thing.util.ThingHandlerHelper;
import org.openhab.core.types.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smarthomej.binding.viessmann.internal.ViessmannGatewayDiscoveryService;
import org.smarthomej.binding.viessmann.internal.api.ViessmannApiGateway;
import org.smarthomej.binding.viessmann.internal.api.ViessmannCommunicationException;
import org.smarthomej.binding.viessmann.internal.config.GatewayConfiguration;
import org.smarthomej.binding.viessmann.internal.dto.device.DeviceDTO;
import org.smarthomej.binding.viessmann.internal.dto.device.DeviceData;
import org.smarthomej.binding.viessmann.internal.dto.events.EventsDTO;
import org.smarthomej.binding.viessmann.internal.dto.features.FeatureDataDTO;
import org.smarthomej.binding.viessmann.internal.dto.features.FeaturesDTO;
import org.smarthomej.binding.viessmann.internal.interfaces.BridgeInterface;

import com.google.gson.JsonSyntaxException;

/**
 * The {@link ViessmannGatewayHandler} is responsible for handling the api connection.
 *
 * @author Ronny Grun - Initial contribution
 */
@NonNullByDefault
public class ViessmannGatewayHandler extends BaseBridgeHandler implements BridgeInterface {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final Storage<String> stateStorage;

    private static final Set<String> ERROR_CHANNELS = Set.of("lastErrorMessage", "errorIsActive");
    private static final String STORED_API_CALLS = "apiCalls";

    private final HttpClient httpClient;
    private final @Nullable String callbackUrl;

    private @NonNullByDefault({}) ViessmannApiGateway api;

    protected @Nullable ViessmannGatewayDiscoveryService discoveryService;

    private int apiCalls;
    private boolean countReset = true;

    private @Nullable String newInstallationId;
    private @Nullable String newGatewaySerial;

    private String installationId = "";
    private String gatewaySerial = "";

    private @Nullable ScheduledFuture<?> viessmannBridgePollingJob;
    private @Nullable ScheduledFuture<?> viessmannErrorsPollingJob;
    private @Nullable ScheduledFuture<?> viessmannBridgeLimitJob;

    public @Nullable List<DeviceData> devicesData;
    protected final List<String> devicesList = new ArrayList<>();
    protected final List<DeviceData> discoveredDeviceList = new ArrayList<>();

    private GatewayConfiguration config = new GatewayConfiguration();

    public ViessmannGatewayHandler(Bridge bridge, Storage<String> stateStorage, HttpClient httpClient,
            @Nullable String callbackUrl) {
        super(bridge);
        this.stateStorage = stateStorage;
        this.httpClient = httpClient;
        this.callbackUrl = callbackUrl;
    }

    public void setInstallationGatewayId(String newInstallation, String newGateway) {
        newInstallationId = newInstallation;
        newGatewaySerial = newGateway;
    }

    /**
     * get the discovered devices list (needed for discovery)
     *
     * @return a list of the all devices
     */
    public List<DeviceData> getDiscoveredDeviceList() {
        // return a copy of the list, so we don't run into concurrency problems
        return new ArrayList<>(discoveredDeviceList);
    }

    private void setConfigInstallationGatewayId() {
        Configuration conf = editConfiguration();
        conf.put("installationId", newInstallationId);
        conf.put("gatewaySerial", newGatewaySerial);
        updateConfiguration(conf);
    }

    public List<Thing> getChildren() {
        return getThing().getThings().stream().filter(Thing::isEnabled).collect(Collectors.toList());
    }

    private boolean errorChannelsLinked() {
        return getThing().getChannels().stream()
                .anyMatch(c -> isLinked(c.getUID()) && ERROR_CHANNELS.contains(c.getUID().getId()));
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        if (channelUID.getId().equals(CHANNEL_RUN_QUERY_ONCE) && OnOffType.ON.equals(command)) {
            logger.debug("Received command: CHANNEL_RUN_QUERY_ONCE");
            pollingFeatures();
            updateState(CHANNEL_RUN_QUERY_ONCE, OnOffType.OFF);
        }
        if (channelUID.getId().equals(CHANNEL_RUN_ERROR_QUERY_ONCE) && OnOffType.ON.equals(command)) {
            logger.debug("Received command: CHANNEL_RUN_ERROR_QUERY_ONCE");
            getDeviceError();
            updateState(CHANNEL_RUN_ERROR_QUERY_ONCE, OnOffType.OFF);
        }
    }

    @Override
    public void dispose() {
        stopViessmannBridgePolling();
        stopViessmannErrorsPolling();
        stopViessmannBridgeLimitReset();
    }

    @Override
    public Collection<Class<? extends ThingHandlerService>> getServices() {
        return Set.of(ViessmannGatewayDiscoveryService.class);
    }

    @Override
    public void initialize() {
        logger.debug("Initialize Viessmann Gateway");

        GatewayConfiguration config = getConfigAs(GatewayConfiguration.class);
        this.config = config;
        String storedApiCalls = this.stateStorage.get(STORED_API_CALLS);
        if (storedApiCalls != null) {
            apiCalls = Integer.parseInt(storedApiCalls);
        } else {
            apiCalls = 0;
        }
        newInstallationId = "";
        newGatewaySerial = "";

        this.gatewaySerial = config.gatewaySerial;
        this.installationId = config.installationId;

        //@formatter:off

        /*
        api = new ViessmannApiGateway(this, this.config.apiKey, httpClient, this.config.user, this.config.password,
                this.config.installationId, this.config.gatewaySerial, callbackUrl);
        */

        if (this.config.installationId.isEmpty() || this.config.gatewaySerial.isEmpty()) {
            // setConfigInstallationGatewayId();
        }

        if (!config.disablePolling && errorChannelsLinked()) {
            // startViessmannErrorsPolling(config.pollingIntervalErrors);
        }
        getAllDevices();
        if (!devicesList.isEmpty()) {
            updateBridgeStatus(ThingStatus.ONLINE);
            // startViessmannBridgePolling(getPollingInterval(), 1);
        }

        //@formatter:on
    }

    //@formatter:off
    private @Nullable BridgeHandler getBridgeHandler () {
        Bridge bridge = getBridge();
        if (bridge != null) {
            BridgeHandler handler = bridge.getHandler();
            if (handler != null) {
                if (handler instanceof ViessmannAccountHandler) {
                    return handler;
                }
            }
        }
        return null;
    }

    public void getAllDevices() {
        logger.trace("Loading Device List from Viessmann Gateway");
        try {
            DeviceDTO allDevices = new DeviceDTO();

            BridgeHandler handler = getBridgeHandler();
            if (handler != null) {
                allDevices = ((ViessmannAccountHandler) handler).getAllDevices(installationId,
                        gatewaySerial);
            }

            countApiCalls();
            if (allDevices != null) {
                devicesData = allDevices.data;
                if (devicesData == null) {
                    logger.warn("Device list is empty.");
                } else {
                    for (DeviceData deviceData : allDevices.data) {
                        deviceData.setInstallationId(installationId);
                        String deviceId = deviceData.id;
                        String deviceType = deviceData.deviceType;
                        if (!"gateway".equals(deviceId) && !devicesList.contains(deviceId)) {
                            discoveredDeviceList.add(deviceData);
                            devicesList.add(deviceId);
                        }
                        logger.trace("Device ID: {}, Type: {}", deviceId, deviceType);
                    }
                }
            }
        } catch (JsonSyntaxException | IllegalStateException e) {
            logger.warn("Parsing Viessmann response fails: {}", e.getMessage());
        }
    }
    //@formatter:on

    public void getDeviceError() {
        logger.trace("Loading error-list from Viessmann Bridge");
        //@formatter:off
        try {
            EventsDTO errors = new EventsDTO();
            String eventType = "device-error";
            BridgeHandler handler = getBridgeHandler();
            if (handler != null) {
                errors = ((ViessmannAccountHandler) handler).getSelectedEvents(eventType, installationId,
                        gatewaySerial);
            }
            countApiCalls();
            logger.trace("Errors:{}", errors);
            if (errors != null && !errors.data.isEmpty()) {
                String state = errors.data.get(0).body.errorDescription;
                Boolean active = errors.data.get(0).body.active;
                updateState("lastErrorMessage", StringType.valueOf(state));
                updateState("errorIsActive", OnOffType.from(active));
            }
        /*
        } catch (ViessmannCommunicationException e) {
            updateBridgeStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR,
                    "Installation not reachable");
         */
        } catch (JsonSyntaxException | IllegalStateException e) {
            logger.warn("Parsing Viessmann response fails: {}", e.getMessage());
        }
        //@formatter:on
    }

    public boolean setData(@Nullable String url, @Nullable String json) throws ViessmannCommunicationException {
        if (url != null && json != null) {
            BridgeHandler handler = getBridgeHandler();
            if (handler != null) {
                return ((ViessmannAccountHandler) handler).setData(url, json);
            }
        }
        return false;
    }

    private Integer getPollingInterval() {
        if (this.config.pollingInterval > 0) {
            return this.config.pollingInterval;
        } else {
            int errorApiCalls = 0;
            if (errorChannelsLinked()) {
                errorApiCalls = 1440 / this.config.pollingIntervalErrors;
            }
            return (86400 / (this.config.apiCallLimit - this.config.bufferApiCommands - errorApiCalls)
                    * devicesList.size()) + 1;
        }
    }

    private void countApiCalls() {
        apiCalls++;
        String apiCallsAsString = String.valueOf(apiCalls);
        stateStorage.put(STORED_API_CALLS, apiCallsAsString);
        updateState(COUNT_API_CALLS, DecimalType.valueOf(apiCallsAsString));
    }

    private void checkResetApiCalls() {
        LocalTime time = LocalTime.now();
        if (time.isAfter(LocalTime.of(0, 0, 1)) && (time.isBefore(LocalTime.of(1, 0, 0)))) {
            if (countReset) {
                logger.debug("Resetting API call counts");
                apiCalls = 0;
                countReset = false;
            }
        } else {
            countReset = true;
        }
    }

    private void pollingFeatures() {
        List<Thing> children = getThing().getThings().stream().filter(Thing::isEnabled).collect(Collectors.toList());
        for (Thing child : children) {
            ThingHandler childHandler = child.getHandler();
            if (childHandler instanceof DeviceHandler && ThingHandlerHelper.isHandlerInitialized(childHandler)) {
                updateFeaturesOfDevice((DeviceHandler) childHandler);
            }
        }
    }

    @Override
    public void updateFeaturesOfDevice(@Nullable DeviceHandler handler) {
        String deviceId = "";
        if (handler != null) {
            deviceId = handler.getDeviceId();
            logger.debug("Loading features from Device ID: {}", deviceId);
            try {
                FeaturesDTO allFeatures = null;

                BridgeHandler bridgeHandler = getBridgeHandler();
                if (bridgeHandler != null) {
                    allFeatures = ((ViessmannAccountHandler) bridgeHandler).getAllFeatures(deviceId, installationId,
                            gatewaySerial);
                }
                if (allFeatures != null) {
                    List<FeatureDataDTO> featuresData = allFeatures.data;
                    if (featuresData != null && !featuresData.isEmpty()) {
                        for (FeatureDataDTO featureDataDTO : featuresData) {
                            handler.handleUpdate(featureDataDTO);
                        }
                    } else {
                        String statusMessage = String.format("Features of Device ID %s is empty.", deviceId);
                        logger.warn(statusMessage);
                        handler.updateThingStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR,
                                statusMessage);
                    }
                }
            } catch (ViessmannCommunicationException e) {
                handler.updateThingStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR,
                        "Device not reachable");
            } catch (JsonSyntaxException | IllegalStateException e) {
                logger.warn("Parsing Viessmann response fails: {}", e.getMessage());
            }
        }
    }

    private void startViessmannBridgePolling(Integer pollingIntervalS, Integer initialDelay) {
        ScheduledFuture<?> currentPollingJob = viessmannBridgePollingJob;
        if (currentPollingJob == null) {
            viessmannBridgePollingJob = scheduler.scheduleWithFixedDelay(() -> {
                api.checkExpiringToken();
                checkResetApiCalls();
                if (!config.disablePolling) {
                    logger.debug("Refresh job scheduled to run every {} seconds for '{}'", pollingIntervalS,
                            getThing().getUID());
                    pollingFeatures();
                }
            }, initialDelay, pollingIntervalS, TimeUnit.SECONDS);
        }
    }

    protected synchronized void manageErrorPolling() {
        ScheduledFuture<?> errorPollingJob = viessmannErrorsPollingJob;
        if (errorChannelsLinked() && errorPollingJob == null) {
            stopViessmannBridgePolling();
            startViessmannBridgePolling(getPollingInterval(), getPollingInterval());
            startViessmannErrorsPolling(config.pollingIntervalErrors);
        } else {
            if (!errorChannelsLinked() && errorPollingJob != null) {
                stopViessmannErrorsPolling();
                stopViessmannBridgePolling();
                startViessmannBridgePolling(getPollingInterval(), getPollingInterval());
            }
        }
    }

    private void startViessmannErrorsPolling(Integer pollingInterval) {
        ScheduledFuture<?> currentPollingJob = viessmannErrorsPollingJob;
        if (currentPollingJob == null) {
            viessmannErrorsPollingJob = scheduler.scheduleWithFixedDelay(() -> {
                logger.debug("Refresh job scheduled to run every {} minutes for polling errors", pollingInterval);
                getDeviceError();
            }, 0, pollingInterval, TimeUnit.MINUTES);
        }
    }

    private void startViessmannBridgeLimitReset(Long delay) {
        ScheduledFuture<?> currentPollingJob = viessmannBridgeLimitJob;
        if (currentPollingJob == null) {
            viessmannBridgeLimitJob = scheduler.scheduleWithFixedDelay(() -> {
                logger.debug("Resetting limit and reconnect for '{}'", getThing().getUID());
                api.checkExpiringToken();
                checkResetApiCalls();
                getAllDevices();
                if (!devicesList.isEmpty()) {
                    updateBridgeStatus(ThingStatus.ONLINE);
                    startViessmannBridgePolling(getPollingInterval(), 1);
                    stopViessmannBridgeLimitReset();
                }
            }, delay, 120, TimeUnit.SECONDS);
        }
    }

    public void stopViessmannBridgePolling() {
        ScheduledFuture<?> currentPollingJob = viessmannBridgePollingJob;
        if (currentPollingJob != null) {
            currentPollingJob.cancel(true);
            viessmannBridgePollingJob = null;
        }
    }

    public void stopViessmannErrorsPolling() {
        ScheduledFuture<?> currentPollingJob = viessmannErrorsPollingJob;
        if (currentPollingJob != null) {
            currentPollingJob.cancel(true);
            viessmannErrorsPollingJob = null;
        }
    }

    public void stopViessmannBridgeLimitReset() {
        ScheduledFuture<?> currentPollingJob = viessmannBridgeLimitJob;
        if (currentPollingJob != null) {
            currentPollingJob.cancel(true);
            viessmannBridgeLimitJob = null;
        }
    }

    public void waitForApiCallLimitReset(Long resetLimitMillis) {
        stopViessmannBridgePolling();
        stopViessmannErrorsPolling();
        Long delay = (resetLimitMillis - Instant.now().toEpochMilli()) / 1000;
        stopViessmannBridgeLimitReset();
        startViessmannBridgeLimitReset(delay);
    }

    public void updateBridgeStatus(ThingStatus status) {
        updateStatus(status);
    }

    public void updateBridgeStatus(ThingStatus status, ThingStatusDetail statusDetail, String statusMessage) {
        updateStatus(status, statusDetail, statusMessage);
    }

    @Override
    public void channelLinked(ChannelUID channelUID) {
        manageErrorPolling();
        super.channelLinked(channelUID);
    }

    @Override
    public void channelUnlinked(ChannelUID channelUID) {
        manageErrorPolling();
        super.channelUnlinked(channelUID);
    }
}
