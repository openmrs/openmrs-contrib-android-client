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

import com.google.gson.GsonBuilder
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import org.openmrs.mobile.utilities.ActiveAndroid.Model
import org.openmrs.mobile.utilities.ActiveAndroid.annotation.Column
import org.openmrs.mobile.utilities.ActiveAndroid.annotation.Table
import java.io.Serializable

@Table(name = "encountercreate")
class Encountercreate : Model(), Serializable {

    private val gson = GsonBuilder().excludeFieldsWithoutExposeAnnotation().create()
    private val obscreatetype = object : TypeToken<List<Obscreate>>() {

    }.type

    @Column(name = "visit")
    @SerializedName("visit")
    @Expose
    var visit: String? = null

    @Column(name = "patient")
    @SerializedName("patient")
    @Expose
    var patient: String? = null

    @Column(name = "patientid")
    var patientId: Long? = null

    @Column(name = "encounterType")
    @SerializedName("encounterType")
    @Expose
    var encounterType: String? = null

    @SerializedName("form")
    @Expose
    var formUuid: String? = null

    @Column(name = "formname")
    var formname: String? = null

    @Column(name = "synced")
    private var synced = false

    @SerializedName("location")
    @Expose
    var location: String? = null

    @SerializedName("encounterProviders")
    @Expose
    var encounterProvider: List<EncounterProviderCreate> = ArrayList()

    @SerializedName("obs")
    @Expose
    var observations: List<Obscreate> = ArrayList()

    @Column(name = "obs")
    private var obslist: String? = null

    fun getSynced(): Boolean? {
        return synced
    }

    fun setSynced(synced: Boolean?) {
        this.synced = synced!!
    }


    fun setObslist() {
        this.obslist = gson.toJson(observations, obscreatetype)
    }

    fun pullObslist() {
        this.observations = gson.fromJson(this.obslist, obscreatetype)
    }


}
