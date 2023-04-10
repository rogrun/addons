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
package org.smarthomej.binding.aquatemp.internal;

import static org.smarthomej.binding.aquatemp.internal.AquatempBindingConstants.*;

import java.util.Set;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.jetty.client.HttpClient;
import org.openhab.core.io.net.http.HttpClientFactory;
import org.openhab.core.storage.Storage;
import org.openhab.core.storage.StorageService;
import org.openhab.core.thing.Bridge;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingTypeUID;
import org.openhab.core.thing.binding.BaseThingHandlerFactory;
import org.openhab.core.thing.binding.ThingHandler;
import org.openhab.core.thing.binding.ThingHandlerFactory;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.http.HttpService;
import org.smarthomej.binding.aquatemp.internal.handler.AquatempBridgeHandler;
import org.smarthomej.binding.aquatemp.internal.handler.DeviceHandler;

/**
 * The {@link AquatempHandlerFactory} is responsible for creating things and thing
 * handlers.
 *
 * @author Ronny Grun - Initial contribution
 */
@NonNullByDefault
@Component(configurationPid = "binding.aquatemp", service = ThingHandlerFactory.class)
public class AquatempHandlerFactory extends BaseThingHandlerFactory {

    private final HttpClient httpClient;
    private final StorageService storageService;

    private static final Set<ThingTypeUID> SUPPORTED_THING_TYPES_UIDS = Set.of(THING_TYPE_BRIDGE, THING_TYPE_DEVICE);

    @Activate
    public AquatempHandlerFactory(@Reference HttpService httpService, @Reference HttpClientFactory httpClientFactory,
            @Reference StorageService storageService) {
        this.httpClient = httpClientFactory.getCommonHttpClient();
        this.storageService = storageService;
    }

    @Override
    public boolean supportsThingType(ThingTypeUID thingTypeUID) {
        return SUPPORTED_THING_TYPES_UIDS.contains(thingTypeUID);
    }

    @Override
    protected @Nullable ThingHandler createHandler(Thing thing) {
        ThingTypeUID thingTypeUID = thing.getThingTypeUID();

        if (THING_TYPE_BRIDGE.equals(thingTypeUID)) {
            Storage<String> storage = storageService.getStorage(thing.getUID().toString(),
                    String.class.getClassLoader());
            return new AquatempBridgeHandler((Bridge) thing, storage, httpClient);
        } else if (THING_TYPE_DEVICE.equals(thingTypeUID)) {
            return new DeviceHandler(thing);
        }
        return null;
    }
}
