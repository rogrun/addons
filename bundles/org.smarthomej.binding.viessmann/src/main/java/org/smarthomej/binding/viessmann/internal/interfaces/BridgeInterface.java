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
package org.smarthomej.binding.viessmann.internal.interfaces;

import org.eclipse.jdt.annotation.Nullable;
import org.smarthomej.binding.viessmann.internal.api.ViessmannCommunicationException;
import org.smarthomej.binding.viessmann.internal.handler.DeviceHandler;

/**
 * The {@link BridgeInterface} is responsible for handling Bridges
 *
 *
 * @author Ronny Grun - Initial contribution
 */
public interface BridgeInterface {
    boolean setData(@Nullable String url, @Nullable String json) throws ViessmannCommunicationException;

    void updateFeaturesOfDevice(@Nullable DeviceHandler handler);
}
