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

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * The {@link ErrorDTO} provides ErrorDTO
 *
 * @author Ronny Grun - Initial contribution
 */
public class ErrorDTO {

    @SerializedName("error_code")
    public String errorCode;
    @SerializedName("error_msg")
    public String errorMsg;
    @SerializedName("error_msg_code")
    public String errorMsgCode;
    @SerializedName("total_size")
    public Integer totalSize;
    @SerializedName("total_page")
    public Integer totalPage;
    @SerializedName("next_page")
    public Boolean nextPage;
    @SerializedName("object_result")
    public List<ErrorObjectResult> objectResult;
    @SerializedName("is_reuslt_suc")
    public Boolean isReusltSuc;
}
