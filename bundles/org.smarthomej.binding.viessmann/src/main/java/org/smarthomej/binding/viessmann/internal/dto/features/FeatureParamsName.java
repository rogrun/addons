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
package org.smarthomej.binding.viessmann.internal.dto.features;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * The {@link FeatureParamsName} is responsible for
 *
 * @author Ronny Grun - Initial contribution
 */
public class FeatureParamsName {

    @SerializedName("required")
    @Expose
    public Boolean required;
    @SerializedName("type")
    @Expose
    public String type;
    @SerializedName("constraints")
    @Expose
    public FeatureConstraintsLength constraints;
}
