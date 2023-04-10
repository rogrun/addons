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
package org.smarthomej.binding.aquatemp.internal.dto.device;

import com.google.gson.annotations.SerializedName;

/**
 * The {@link CommandDTO} provides the commands from device
 *
 * @author Ronny Grun - Initial contribution
 */
public class CommandDTO {

    @SerializedName("protocol_code")
    private String protocolCode;
    @SerializedName("value")
    private String value;

    public String getProtocolCode() {
        return protocolCode;
    }

    public void setProtocolCode(String protocolCode) {
        this.protocolCode = protocolCode;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
