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

package com.example.openmrs_android_sdk.library.models

import com.example.openmrs_android_sdk.utilities.StringUtils
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


import java.io.Serializable

class PersonAddress : Serializable {

    @SerializedName("preferred")
    @Expose
    var preferred: Boolean? = null

    @SerializedName("address1")
    @Expose
    var address1: String? = null

    @SerializedName("address2")
    @Expose
    var address2: String? = null

    @SerializedName("cityVillage")
    @Expose
    var cityVillage: String? = null

    @SerializedName("stateProvince")
    @Expose
    var stateProvince: String? = null

    @SerializedName("country")
    @Expose
    var country: String? = null

    @SerializedName("postalCode")
    @Expose
    var postalCode: String? = null

    val addressString: String
        get() {
            var addr = ""
            if (StringUtils.notNull(address1))
                addr += address1!! + "\n"
            if (StringUtils.notNull(address2))
                addr += address2
            return addr
        }

}
