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

package com.openmrs.android_sdk.library.databases.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import com.openmrs.android_sdk.library.models.Resource

/**
 * The type StandaloneEncounter entity.
 */
@Entity(tableName = "standaloneEncounters")
class StandaloneEncounterEntity : Resource {

    /**
     * Gets and sets visit uuid.
     *
     * @return the visit uuid
     */
    @ColumnInfo(name = "visit_uuid")
    var visitUuid: String? = null

    /**
     * Gets and sets encounter date and time.
     *
     * @return the encounterDateTime
     */

    @ColumnInfo(name = "encounterDatetime")
    var encounterDateTime: String? = null

    /**
     * Gets and sets encounter type.
     *
     * @return the encounter type
     */
    @ColumnInfo(name = "type")
    var encounterType: String? = null
    /**
     * Gets and sets patient uuid.
     *
     * @return the patient uuid
     */
    @ColumnInfo(name = "patient_uuid")
    var patientUuid: String? = null
    /**
     * Gets and sets form uuid.
     *
     * @return the form uuid
     */
    @ColumnInfo(name = "form_uuid")

    var formUuid: String? = null
    /**
     * Gets and sets location uuid.
     *
     * @return the location uuid
     */
    @ColumnInfo(name = "location_uuid")
    var locationUuid: String? = null
    /**
     * Gets and sets encounter provider uuid.
     *
     * @return the encounter provider uuid
     */
    @ColumnInfo(name = "encounter_provider_uuid")
    var encounterProviderUuid: String? = null

    constructor(
        uuid: String?,
        display: String?,
        visitUuid: String?,
        encounterDateTime: String?,
        encounterType: String?,
        patientUuid: String?,
        formUuid: String?,
        locationUuid: String?,
        encounterProviderUuid: String?
    ) {
        this.uuid = uuid
        this.display = display
        this.visitUuid = visitUuid
        this.encounterDateTime = encounterDateTime
        this.encounterType = encounterType
        this.patientUuid = patientUuid
        this.formUuid = formUuid
        this.locationUuid = locationUuid
        this.encounterProviderUuid = encounterProviderUuid
    }
}