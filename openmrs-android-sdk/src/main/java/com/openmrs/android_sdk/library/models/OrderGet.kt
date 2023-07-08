/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package com.openmrs.android_sdk.library.models

import androidx.room.FtsOptions.Order
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class OrderGet: Serializable {

    @Expose
    val uuid: String = ""

    @Expose
    val display: String = ""

    @Expose
    @SerializedName("encounter")
    val encounterUuid: String = ""

    @Expose
    val type: String = ""

    @Expose
    val instructions: String = ""

    @Expose
    val frequency: String = ""

    @Expose
    val doseUnits: String = ""

    @Expose
    @SerializedName("careSetting")
    val careSettingName: String = ""

    @Expose
    val urgency: String = ""

    @Expose
    val dateStopped: String = ""

    @Expose
    val dateActivated: String = ""

    @Expose
    val quantity: String = ""

    @Expose
    val drug: String = ""

    @Expose
    val dosingInstructions: String = ""

    @Expose
    val duration: String = ""

    @Expose
    val dosingType: String = ""

    @Expose
    val numberOfRepeats: String = ""

    @Expose
    val orderNumber: String = ""

    @Expose
    val accessionNumber: String = ""

    @Expose
    @SerializedName("patient")
    val patientUuid: String = ""

    @Expose
    @SerializedName("concept")
    val conceptUuid: String = ""

    @Expose
    val action: String = ""

    @Expose
    val scheduledDate: String = ""

    @Expose
    val autoExpireDate: String = ""

    @Expose
    val orderer: OrderResouce = OrderResouce()

    @Expose
    val orderReason: String = ""

    @Expose
    val orderType: OrderResouce = OrderResouce()

    @Expose
    val fulfillerStatus: String = ""

    @Expose
    val fulfillerComment: String = ""

    @Expose
    val specimenSource: String = ""
}