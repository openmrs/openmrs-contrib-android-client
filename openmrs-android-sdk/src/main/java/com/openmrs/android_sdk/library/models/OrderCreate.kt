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
import java.io.Serializable

class OrderCreate: Serializable {

    @Expose
    lateinit var concept: String

    @Expose
    lateinit var encounter: String

    @Expose
    lateinit var type: String

    @Expose
    lateinit var patientUuid: String

    @Expose
    lateinit var orderer: String

    @Expose
    lateinit var careSetting: String

    @Expose
    val instructions: String = ""

    @Expose
    val frequency: String = ""

    @Expose
    val doseUnits: String = ""

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
}