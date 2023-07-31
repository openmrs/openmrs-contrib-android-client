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

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class OrderGet: Serializable {

    @Expose
    var uuid: String = ""

    @Expose
    var display: String = ""

    @Expose
    @SerializedName("encounter")
    var encounter: OrderResource = OrderResource()

    @Expose
    var type: String = ""

    @Expose
    var instructions: String = ""

    @Expose
    var frequency: String = ""

    @Expose
    var doseUnits: String = ""

    @Expose
    @SerializedName("careSetting")
    var careSettingName: String = ""

    @Expose
    var urgency: String = ""

    @Expose
    var dateStopped: String = ""

    @Expose
    var dateActivated: String = ""

    @Expose
    var quantity: String = ""

    @Expose
    var drug: String = ""

    @Expose
    var dosingInstructions: String = ""

    @Expose
    var duration: String = ""

    @Expose
    var dosingType: String = ""

    @Expose
    var numberOfRepeats: String = ""

    @Expose
    var orderNumber: String = ""

    @Expose
    var accessionNumber: String = ""

    @Expose
    @SerializedName("patient")
    var patient: OrderResource = OrderResource()

    @Expose
    @SerializedName("concept")
    var concept: OrderResource = OrderResource()

    @Expose
    var action: String = ""

    @Expose
    var scheduledDate: String = ""

    @Expose
    var autoExpireDate: String = ""

    @Expose
    var orderer: OrderResource = OrderResource()

    @Expose
    var orderReason: String = ""

    @Expose
    var orderType: OrderResource = OrderResource()

    @Expose
    var fulfillerStatus: String = ""

    @Expose
    var fulfillerComment: String = ""

    @Expose
    var specimenSource: String = ""
}