/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */


package com.openmrs.android_sdk.library.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Provider attribute
 *
 * <p> More about Provider Subresources https://rest.openmrs.org/#providers </p>
 * @constructor Create empty Provider attribute
 */
class ProviderAttribute : Serializable {

    @SerializedName("attributeType")
    @Expose
    var attributeType: ProviderAttributeType? = null

    @SerializedName("value")
    @Expose
    var value: String? = null

}