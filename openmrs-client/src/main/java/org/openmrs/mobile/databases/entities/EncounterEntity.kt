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

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import org.openmrs.mobile.models.Resource

@Entity(tableName = "encounters")
class EncounterEntity : Resource() {
    @ColumnInfo(name = "visit_id")
    var visitKeyId: String? = null

    @NonNull
    @ColumnInfo(name = "encounterDatetime")
    lateinit var encounterDateTime: String

    @ColumnInfo(name = "type")
    var encounterType: String? = null

    @ColumnInfo(name = "patient_uuid")
    var patientUuid: String? = null

    @ColumnInfo(name = "form_uuid")
    var formUuid: String? = null

    @ColumnInfo(name = "location_uuid")
    var locationUuid: String? = null

    @ColumnInfo(name = "encounter_provider_uuid")
    var encounterProviderUuid: String? = null

}