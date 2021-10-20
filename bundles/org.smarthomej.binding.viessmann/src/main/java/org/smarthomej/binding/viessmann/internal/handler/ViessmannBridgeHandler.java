/**
 * Copyright (c) 2021 Contributors to the SmartHome/J project
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.jetty.client.HttpClient;
import org.openhab.core.config.core.Configuration;
import org.openhab.core.thing.Bridge;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingStatus;
import org.openhab.core.thing.ThingStatusDetail;
import org.openhab.core.thing.binding.BaseBridgeHandler;
import org.openhab.core.thing.binding.ThingHandlerService;
import org.openhab.core.types.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smarthomej.binding.viessmann.internal.ViessmannConfiguration;
import org.smarthomej.binding.viessmann.internal.ViessmannDiscoveryService;
import org.smarthomej.binding.viessmann.internal.api.ViessmannApi;
import org.smarthomej.binding.viessmann.internal.dto.device.DeviceDTO;
import org.smarthomej.binding.viessmann.internal.dto.device.DeviceData;
import org.smarthomej.binding.viessmann.internal.dto.features.FeatureDataDTO;
import org.smarthomej.binding.viessmann.internal.dto.features.FeaturesDTO;

import com.google.gson.JsonSyntaxException;

/**
 * The {@link ViessmannBridgeHandler} is responsible for handling the api connection.
 *
 * @author Ronny Grun - Initial contribution
 */
@NonNullByDefault
public class ViessmannBridgeHandler extends BaseBridgeHandler {
    private static final int DEFAULT_API_TIMEOUT_SECONDS = 20;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final HttpClient httpClient;

    private @NonNullByDefault({}) ViessmannApi api;
    private @NonNullByDefault({}) String apiKey;
    private @NonNullByDefault({}) String authToken;
    private @NonNullByDefault({}) String user;
    private @NonNullByDefault({}) String password;
    private @NonNullByDefault({}) String installationId;
    private @NonNullByDefault({}) String gatewaySerial;
    private @NonNullByDefault({}) int pollingInterval;

    protected @Nullable ViessmannDiscoveryService discoveryService;

    private int apiTimeout;

    private @Nullable static String newInstallationId;
    private @Nullable static String newGatewaySerial;

    private @Nullable ScheduledFuture<?> viessmannBridgePollingJob;

    public @Nullable List<DeviceData> devicesData;
    protected volatile List<String> devicesList = new ArrayList<>();
    protected volatile List<String> pollingDevicesList = new ArrayList<>();

    public static void setInstallationGatewayId(String newInstallation, String newGateway) {
        newInstallationId = newInstallation;
        newGatewaySerial = newGateway;
    }

    private @Nullable List<DeviceData> getDevicesData() {
        return devicesData;
    }

    /**
     * get the devices list (needed for discovery)
     *
     * @return a list of the all devices
     */
    public List<String> getDevicesList() {
        // return a copy of the list, so we don't run into concurrency problems
        return new ArrayList<>(devicesList);
    }

    public void setPollingDevice(String deviceId) {
        if (!pollingDevicesList.contains(deviceId)) {
            pollingDevicesList.add(deviceId);
        }
    }

    public void unsetPollingDevice(String deviceId) {
        if (pollingDevicesList.contains(deviceId)) {
            // Remove device from list
            pollingDevicesList.remove(deviceId);
        }
    }

    private void setConfigInstallationGatewayId() {
        Configuration conf = editConfiguration();
        conf.put("installationId", newInstallationId);
        conf.put("gatewaySerial", newGatewaySerial);
        updateConfiguration(conf);
    }

    public ViessmannBridgeHandler(Bridge bridge, HttpClient httpClient) {
        super(bridge);
        this.httpClient = httpClient;
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        // Nothing to handle here currently
    }

    @Override
    public void dispose() {
        stopViessmannBridgePolling();
    }

    @Override
    public Collection<Class<? extends ThingHandlerService>> getServices() {
        return Set.of(ViessmannDiscoveryService.class);
    }

    @Override
    public void initialize() {
        logger.debug("Initialize Viessmann Accountservice");

        ViessmannConfiguration config = getConfigAs(ViessmannConfiguration.class);
        user = config.user;
        password = config.password;
        apiKey = config.apiKey;
        installationId = config.installationId;
        gatewaySerial = config.gatewaySerial;
        pollingInterval = config.pollingInterval;
        newInstallationId = "";
        newGatewaySerial = "";
        Integer value;
        value = config.apiTimeout;
        apiTimeout = (value == null ? DEFAULT_API_TIMEOUT_SECONDS : value) * 1000;
        api = new ViessmannApi(this, apiKey, apiTimeout, httpClient, user, password, installationId, gatewaySerial);
        if (config.installationId == null || config.gatewaySerial == null) {
            setConfigInstallationGatewayId();
        }
        getAllDevices();
        startViessmannBridgePolling(pollingInterval);
    }

    public void getAllDevices() {
        logger.trace("Loading Device List from Viessmann Bridge");
        DeviceDTO allDevices = api.getAllDevices();
        if (allDevices != null) {
            devicesData = allDevices.data;
            if (devicesData == null) {
                logger.error("Device list is empty.");
            } else {
                for (DeviceData deviceData : allDevices.data) {
                    String deviceId = deviceData.id;
                    String deviceType = deviceData.deviceType;
                    if (!devicesList.contains(deviceId)) {
                        devicesList.add(deviceId);
                    }
                    logger.trace("Device ID: {}, Type: {}", deviceId, deviceType);
                }
            }
        }
    }

    public boolean setData(@Nullable String url, @Nullable String json) {
        if (url != null && json != null) {
            return api.setData(url, json);
        }
        return false;
    }

    private void pollingFeatures() {
        List<String> devices = pollingDevicesList;
        if (devices != null) {
            for (String deviceId : devices) {
                logger.debug("Loading Featueres from Device ID: {}", deviceId);
                getAllFeaturesByDeviceId(deviceId);
            }
        }
    }

    public void getAllFeaturesByDeviceId(String deviceId) {
        try {
            FeaturesDTO allFeatures = api.getAllFeatures(deviceId);
            if (allFeatures != null) {
                List<FeatureDataDTO> featuresData = allFeatures.data;
                for (FeatureDataDTO featureDataDTO : featuresData) {
                    notifyChildHandlers(featureDataDTO);
                }
            }
        } catch (JsonSyntaxException | IllegalStateException e) {
            logger.warn("Parsing Viessmann response fails: {}", e);
        }
    }

    private void startViessmannBridgePolling(Integer pollingIntervalS) {
        ScheduledFuture<?> currentPollingJob = viessmannBridgePollingJob;
        if (currentPollingJob == null) {
            viessmannBridgePollingJob = scheduler.scheduleWithFixedDelay(() -> {
                logger.debug("Refresh job scheduled to run every {} seconds for '{}'", pollingIntervalS,
                        getThing().getUID());
                api.checkExpiringToken();
                // getAllFeatures();
                pollingFeatures();
            }, 1, TimeUnit.SECONDS.toSeconds(pollingIntervalS), TimeUnit.SECONDS);
        }
    }

    private void stopViessmannBridgePolling() {
        ScheduledFuture<?> currentPollingJob = viessmannBridgePollingJob;
        if (currentPollingJob != null) {
            currentPollingJob.cancel(true);
            viessmannBridgePollingJob = null;
        }
    }

    /**
     * Notify appropriate child thing handlers of an Viessmann message by calling their handleUpdate() methods.
     *
     * @param msg message to forward to child handler(s)
     */
    private void notifyChildHandlers(FeatureDataDTO msg) {
        for (Thing thing : getThing().getThings()) {
            ViessmannThingHandler handler = (ViessmannThingHandler) thing.getHandler();
            //@formatter:off
            if (handler != null && (handler instanceof DeviceHandler && msg instanceof FeatureDataDTO)) {
                handler.handleUpdate(msg);
            }
            //@formatter:on
        }
    }

    @Override
    public void updateStatus(ThingStatus status) {
        super.updateStatus(status);
    }

    public void updateBridgeStatus(ThingStatus status) {
        updateStatus(status);
    }

    public void updateBridgeStatus(ThingStatus status, ThingStatusDetail statusDetail, String statusMessage) {
        updateStatus(status, statusDetail, statusMessage);
    }
}
