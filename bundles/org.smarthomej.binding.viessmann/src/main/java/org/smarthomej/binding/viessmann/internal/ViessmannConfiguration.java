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

/**
 * The {@link ViessmannConfiguration} class contains fields mapping thing configuration parameters.
 *
 * @author Ronny Grun - Initial contribution
 */
public class ViessmannConfiguration {

    /**
     * Sample configuration parameters. Replace with your own.
     */
    public String user;
    public String password;
    public String apiKey;
    public String installationId;
    public String gatewaySerial;
    public int pollingInterval;
    public int apiTimeout;
}
