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
import java.util.*

/**
 *
 * PatientDTO represents patient data on create/update API.
 * This object is serialized as request/response body to send the patient related over the wire.
 *
 */
open class PatientDto {

    @SerializedName("uuid")
    @Expose
    var uuid: String? = null
        protected set

    @SerializedName("identifiers")
    @Expose
    private var identifiers: List<PatientIdentifier> = ArrayList()

    @SerializedName("person")
    @Expose
    var person: Person? = null


    /**
     *
     * @return
     * The patient object after transformation from patientDto Details
     */
    val patient: Patient
        get() {
            val person = person
            val patient = Patient(null, "", identifiers,
                    person!!.names, person.gender!!, person.birthdate!!, person.birthdateEstimated, person.addresses, person.attributes, person.photo, person.causeOfDeath, person.isDeceased)

            patient.uuid = uuid.toString()

            return patient
        }


    fun setIdentifiers(identifiers: List<PatientIdentifier>) {
        this.identifiers = identifiers
    }
}
