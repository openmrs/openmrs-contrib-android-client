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

import java.io.Serializable

class PersonName : Serializable {

    @SerializedName("givenName")
    @Expose
    var givenName: String? = null

    @SerializedName("middleName")
    @Expose
    var middleName: String? = null

    @SerializedName("familyName")
    @Expose
    var familyName: String? = null


    val nameString: String
        get() = if (middleName == null || middleName == "null")
            "$givenName $familyName"
        else
            "$givenName $middleName $familyName"
}
