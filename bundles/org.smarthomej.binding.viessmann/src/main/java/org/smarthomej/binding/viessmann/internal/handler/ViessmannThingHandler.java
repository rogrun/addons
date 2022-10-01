/**
 * Copyright (c) 2021-2022 Contributors to the SmartHome/J project
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

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.core.thing.Bridge;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingStatus;
import org.openhab.core.thing.ThingStatusDetail;
import org.openhab.core.thing.ThingStatusInfo;
import org.openhab.core.types.CommandOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smarthomej.binding.viessmann.internal.ViessmannDynamicCommandDescriptionProvider;
import org.smarthomej.binding.viessmann.internal.dto.ViessmannMessage;
import org.smarthomej.binding.viessmann.internal.dto.features.FeatureDataDTO;
import org.smarthomej.commons.UpdatingBaseThingHandler;

/**
 * {@link ViessmannThingHandler} is the abstract base class for all Viessmann thing handlers.
 *
 * @author Ronny Grun - Initial contribution
 */
@NonNullByDefault
public abstract class ViessmannThingHandler extends UpdatingBaseThingHandler {

    private final Logger logger = LoggerFactory.getLogger(ViessmannThingHandler.class);
    protected final AtomicBoolean firstUpdateReceived = new AtomicBoolean(false);
    protected static AtomicBoolean readyToSendData = new AtomicBoolean(false);

    private final ViessmannDynamicCommandDescriptionProvider commandDescriptionProvider;

    public ViessmannThingHandler(Thing thing, ViessmannDynamicCommandDescriptionProvider commandDescriptionProvider) {
        super(thing);
        this.commandDescriptionProvider = commandDescriptionProvider;
    }

    /**
     * Initialize device state and set status for handler. Should be called at the end of initialize(). Also called by
     * bridgeStatusChanged() when bridge status changes from OFFLINE to ONLINE. Calls initChannelState() to initialize
     * channels if setting status to ONLINE.
     */
    protected void initDeviceState() {
        logger.trace("Initializing device state");
        Bridge bridge = getBridge();
        if (bridge == null) {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR, "No bridge configured");
        } else if (bridge.getStatus() == ThingStatus.ONLINE) {
            initChannelState();
            updateStatus(ThingStatus.ONLINE);
        } else {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.BRIDGE_OFFLINE);
        }
    }

    /**
     * Initialize channel states if necessary
     */
    public abstract void initChannelState();

    /**
     * Notify handler of a message from the Viessmann via the bridge
     *
     * @param msg The ViessmannMessage to handle
     */
    // public abstract void handleUpdate(ViessmannMessage msg);
    public abstract void handleUpdate(FeatureDataDTO feature);

    /**
     * Notify handler of a channel message from the Viessmann via the bridge
     *
     * @param msg The ViessmannMessage to handle
     */
    public abstract void handleUpdateChannel(ViessmannMessage msg);

    @Override
    public void bridgeStatusChanged(ThingStatusInfo bridgeStatusInfo) {
        ThingStatus bridgeStatus = bridgeStatusInfo.getStatus();
        logger.debug("Bridge status changed to {} for Viessmann handler", bridgeStatus);

        if (bridgeStatus == ThingStatus.ONLINE
                && getThing().getStatusInfo().getStatusDetail() == ThingStatusDetail.BRIDGE_OFFLINE) {
            initDeviceState();
        } else if (bridgeStatus == ThingStatus.OFFLINE) {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.BRIDGE_OFFLINE);
        }
    }

    public void setChannelCommand(ChannelUID channelUid, List<CommandOption> commandOptions) {
        commandDescriptionProvider.setCommandOptions(channelUid, commandOptions);
    }
}
