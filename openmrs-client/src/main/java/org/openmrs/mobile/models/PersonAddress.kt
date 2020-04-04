/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.mobile.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import org.openmrs.mobile.utilities.StringUtils

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
