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

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Ignore
import androidx.room.TypeConverters
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.openmrs.mobile.models.typeConverters.ObservationListConverter
import java.io.Serializable

@Entity(tableName = "encountercreate")
class Encountercreate : Serializable {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    var id: Long? = null

    @ColumnInfo(name = "visit")
    @SerializedName("visit")
    @Expose
    var visit: String? = null

    @ColumnInfo(name = "patient")
    @SerializedName("patient")
    @Expose
    var patient: String? = null

    @ColumnInfo(name = "patientid")
    var patientId: Long? = null

    @ColumnInfo(name = "encounterType")
    @SerializedName("encounterType")
    @Expose
    var encounterType: String? = null

    @ColumnInfo(name = "formname")
    var formname: String? = null

    @ColumnInfo(name = "synced")
    var synced = false

    @TypeConverters(ObservationListConverter::class)
    @ColumnInfo(name = "obs")
    @SerializedName("obs")
    @Expose
    var observations: List<Obscreate> = ArrayList()

    @SerializedName("form")
    @Expose
    @Ignore
    var formUuid: String? = null

    @SerializedName("location")
    @Expose
    @Ignore
    var location: String? = null

    @SerializedName("encounterProviders")
    @Expose
    @Ignore
    var encounterProvider: List<EncounterProviderCreate> = ArrayList()
}
