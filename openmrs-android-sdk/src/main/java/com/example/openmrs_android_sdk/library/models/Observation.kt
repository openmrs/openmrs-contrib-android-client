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

import com.example.openmrs_android_sdk.library.databases.entities.ConceptEntity
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

import java.io.Serializable


class Observation : Resource(), Serializable {

    @SerializedName("concept")
    @Expose
    var concept: ConceptEntity? = null

    @SerializedName("person")
    @Expose
    var person: Person? = null

    @SerializedName("obsDatetime")
    @Expose
    var obsDatetime: String? = null

    @SerializedName("accessionNumber")
    @Expose
    var accessionNumber: Int = 0

    @SerializedName("obsGroup")
    @Expose
    var obsGroup: Observation? = null

    @SerializedName("valueCodedName")
    @Expose
    var valueCodedName: String? = null

    @SerializedName("comment")
    @Expose
    var comment: String? = null

    @SerializedName("location")
    @Expose
    var location: Resource? = null

    @SerializedName("encounter")
    @Expose
    var encounter: Encounter? = null

    @SerializedName("voided")
    @Expose
    var voided: Boolean? = null

    @SerializedName("formFieldPath")
    @Expose
    var formFieldPath: String? = null

    @SerializedName("formFieldNamespace")
    @Expose
    var formFieldNamespace: String? = null

    @SerializedName("resourceVersion")
    @Expose
    var resourceVersion: String? = null

    override var id: Long? = null
    var encounterID: Long? = null
    var displayValue: String? = null
        get() {
            if (field == null && display?.contains(":") == true) {
                displayValue = display!!.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]
            }
            return field
        }

    var diagnosisList: String? = null
    var diagnosisCertainty: String? = null
        private set
    var diagnosisOrder: String? = null

    var diagnosisNote: String? = null

    val shortDiagnosisCertainty: String
        get() = diagnosisCertainty!!.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]

    fun setDiagnosisCertanity(certanity: String?) {
        this.diagnosisCertainty = certanity
    }

}
