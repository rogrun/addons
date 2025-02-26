/**
 * Copyright (c) 2010-2021 Contributors to the openHAB project
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
package org.smarthomej.binding.amazonechocontrol.internal.smarthome;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.core.library.types.DecimalType;
import org.openhab.core.library.types.HSBType;
import org.openhab.core.library.types.PercentType;
import org.openhab.core.library.types.StringType;
import org.openhab.core.thing.Channel;
import org.openhab.core.types.Command;
import org.openhab.core.types.CommandOption;
import org.openhab.core.types.StateDescription;
import org.openhab.core.types.StateDescriptionFragmentBuilder;
import org.openhab.core.types.UnDefType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smarthomej.binding.amazonechocontrol.internal.connection.Connection;
import org.smarthomej.binding.amazonechocontrol.internal.handler.SmartHomeDeviceHandler;
import org.smarthomej.binding.amazonechocontrol.internal.jsons.JsonSmartHomeCapability;
import org.smarthomej.binding.amazonechocontrol.internal.jsons.JsonSmartHomeDevice;

import com.google.gson.JsonObject;

/**
 * The {@link HandlerColorController} is responsible for the Alexa.ColorTemperatureController interface
 *
 * @author Lukas Knoeller - Initial contribution
 * @author Michael Geramb - Initial contribution
 */
@NonNullByDefault
public class HandlerColorController extends AbstractInterfaceHandler {
    public static final String INTERFACE = "Alexa.ColorController";
    public static final String INTERFACE_COLOR_PROPERTIES = "Alexa.ColorPropertiesController";

    private static final ChannelInfo COLOR = new ChannelInfo("color", "color", Constants.CHANNEL_TYPE_COLOR);
    private static final ChannelInfo COLOR_PROPERTIES = new ChannelInfo("colorProperties", "colorName",
            Constants.CHANNEL_TYPE_COLOR_NAME);

    private final Logger logger = LoggerFactory.getLogger(HandlerColorController.class);

    private @Nullable HSBType lastColor;
    private @Nullable String lastColorName;
    private boolean matchColors = false;

    public HandlerColorController(SmartHomeDeviceHandler smartHomeDeviceHandler) {
        super(smartHomeDeviceHandler, List.of(INTERFACE, INTERFACE_COLOR_PROPERTIES));
    }

    @Override
    protected Set<ChannelInfo> findChannelInfos(JsonSmartHomeCapability capability, @Nullable String property) {
        if (COLOR.propertyName.equals(property)) {
            return Set.of(COLOR, COLOR_PROPERTIES);
        }
        return Set.of();
    }

    @Override
    public void updateChannels(String interfaceName, List<JsonObject> stateList, UpdateChannelResult result) {
        if (INTERFACE.equals(interfaceName)) {
            HSBType colorValue = null;
            for (JsonObject state : stateList) {
                if (COLOR.propertyName.equals(state.get("name").getAsString())) {
                    JsonObject value = state.get("value").getAsJsonObject();
                    // For groups take the maximum
                    if (colorValue == null) {
                        colorValue = new HSBType(new DecimalType(value.get("hue").getAsDouble()),
                                new PercentType(BigDecimal.valueOf(value.get("saturation").getAsDouble() * 100.0)),
                                new PercentType(BigDecimal.valueOf(value.get("brightness").getAsDouble() * 100.0)));
                    }
                }
            }
            if (colorValue != null) {
                if (!colorValue.equals(lastColor)) {
                    result.needSingleUpdate = true;
                    lastColor = colorValue;
                }
            }
            smartHomeDeviceHandler.updateState(COLOR.channelId, colorValue == null ? UnDefType.UNDEF : colorValue);
        }
        if (INTERFACE_COLOR_PROPERTIES.equals(interfaceName)) {
            String colorNameValue = null;
            for (JsonObject state : stateList) {
                if (COLOR_PROPERTIES.propertyName.equals(state.get("name").getAsString())) {
                    if (colorNameValue == null) {
                        result.needSingleUpdate = false;
                        colorNameValue = state.get("value").getAsJsonObject().get("name").getAsString();
                    }
                }
            }
            if (colorNameValue == null && lastColorName != null) {
                colorNameValue = lastColorName;
            }
            lastColorName = colorNameValue;
            smartHomeDeviceHandler.updateState(COLOR_PROPERTIES.channelId,
                    lastColorName == null ? UnDefType.UNDEF : new StringType(lastColorName));
        }
    }

    @Override
    public boolean handleCommand(Connection connection, JsonSmartHomeDevice shd, String entityId,
            List<JsonSmartHomeCapability> capabilities, String channelId, Command command)
            throws IOException, InterruptedException {
        if (channelId.equals(COLOR.channelId)) {
            if (matchColors) {
                if (command instanceof HSBType) {
                    HSBType color = (HSBType) command;
                    String colorName = AlexaColor.getClosestColorName(color);
                    lastColorName = colorName;
                    connection.smartHomeCommand(entityId, "setColor", Map.of("colorName", colorName));
                    return true;
                }
            } else {
                logger.info("Discarding command to 'color' channel, read-only.");
            }
        }
        if (channelId.equals(COLOR_PROPERTIES.channelId)) {
            if (containsCapabilityProperty(capabilities, COLOR.propertyName)) {
                if (command instanceof StringType) {
                    String colorName = command.toFullString();
                    if (!colorName.isEmpty()) {
                        lastColorName = colorName;
                        connection.smartHomeCommand(entityId, "setColor", Map.of("colorName", colorName));
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public @Nullable List<CommandOption> getCommandDescription(Channel channel) {
        String channelId = channel.getUID().getId();
        if (COLOR_PROPERTIES.channelId.equals(channelId)) {
            return AlexaColor.ALEXA_COLORS.stream().map(color -> new CommandOption(color.colorName, color.colorName))
                    .sorted(Comparator.comparing(CommandOption::getCommand)).collect(Collectors.toList());
        }
        return null;
    }

    @Override
    public @Nullable StateDescription getStateDescription(Channel channel) {
        String channelId = channel.getUID().getId();
        if (COLOR.channelId.equals(channelId)) {
            matchColors = channel.getConfiguration().as(ColorChannelConfig.class).matchColors;
            return StateDescriptionFragmentBuilder.create().withReadOnly(!matchColors).build().toStateDescription();
        }
        return null;
    }
}
