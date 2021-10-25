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
package org.smarthomej.binding.viessmann.internal;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

/**
 * The {@link ViessmannConfiguration} class contains fields mapping thing configuration parameters.
 *
 * @author Ronny Grun - Initial contribution
 */
@NonNullByDefault
public class ViessmannConfiguration {

    /**
     * Sample configuration parameters. Replace with your own.
     */
    public @Nullable String user;
    public @Nullable String password;
    public @Nullable String apiKey;
    public @Nullable String installationId;
    public @Nullable String gatewaySerial;
    public int pollingInterval = 20;
    public int apiTimeout = 120;
}
