/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */

package org.openmrs.mobile.databases.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.openmrs.mobile.models.Resource

@Entity(tableName = "encounters")
class EncounterEntity : Resource() {
    @ColumnInfo(name = "visit_id")
    @SerializedName("visit_id")
    @Expose
    var visitKeyId: String? = null

    @ColumnInfo(name = "encounterDatetime")
    @SerializedName("encounterDatetime")
    @Expose
    lateinit var encounterDateTime: String

    @ColumnInfo(name = "type")
    @SerializedName("type")
    @Expose
    var encounterType: String? = null

    @ColumnInfo(name = "patient_uuid")
    @SerializedName("patient_uuid")
    @Expose
    var patientUuid: String? = null

    @ColumnInfo(name = "form_uuid")
    @SerializedName("form_uuid")
    @Expose
    var formUuid: String? = null

    @ColumnInfo(name = "location_uuid")
    @SerializedName("location_uuid")
    @Expose
    var locationUuid: String? = null

    @ColumnInfo(name = "encounter_provider_uuid")
    @SerializedName("encounter_provider_uuid")
    @Expose
    var encounterProviderUuid: String? = null

}