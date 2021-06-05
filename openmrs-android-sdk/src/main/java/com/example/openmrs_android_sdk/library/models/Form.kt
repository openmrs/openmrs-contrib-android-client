/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package com.example.openmrs_android_sdk.library.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class Form : Resource(), Serializable {


    @SerializedName("name")
    @Expose
    var name: String? = null


    @SerializedName("processor")
    @Expose
    var processor: String? = null


    @SerializedName("pages")
    @Expose
    var pages: List<Page> = ArrayList()

    @SerializedName("valueReference")
    @Expose
    var valueReference: String? = null

}
