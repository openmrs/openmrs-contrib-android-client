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
 * The type StandaloneEncounter Observation.
 */
@Entity(tableName = "standaloneObservations")
class StandaloneObservationEntity: Resource {

    /**
     * Gets and sets encounter uuid.
     *
     * @return the encounter uuid
     */
    @ColumnInfo(name = "encounter_uuid")
    var encounterUuid: String? = null

    /**
     * Gets and sets patient uuid.
     *
     * @return the patient uuid
     */
    @ColumnInfo(name = "patient_uuid")
    var patientUuid: String? = null

    /**
     * Gets and sets location uuid.
     *
     * @return the location uuid
     */
    @ColumnInfo(name = "location_uuid")
    var locationUuid: String? = null

    /**
     * Gets and sets value of observation.
     *
     * @return the value of observation
     */
    @ColumnInfo(name = "value")
    var value: String? = null

    /**
     * Gets and sets status of observation
     *
     * @return the status of observation
     */
    @ColumnInfo(name = "status")
    var status: String? = null

    /**
     * Gets and sets observation date and time
     *
     * @return the obsDateTime
     */
    @ColumnInfo(name = "obsDatetime")
    var obsDateTime: String? = null

    /**
     * Gets and sets interpretation of observation
     *
     * @return the interpretation of observation
     */
    @ColumnInfo(name = "interpretation")
    var interpretation: String? = null

    /**
     * Gets and sets concept uuid
     *
     * @return the concept uuid
     */
    @ColumnInfo(name = "conceptUuid")
    var conceptuuid: String? = null

    /**
     * Gets and sets order of observation
     *
     * @return the order of observation
     */
    @ColumnInfo(name = "order")
    var order: String? = null

    /**
     * Gets and sets comment of observation
     *
     * @return the comment of observation
     */
    @ColumnInfo(name = "comment")
    var comment: String? = null

    constructor(
        uuid: String?,
        display: String?,
        encounterUuid: String?,
        patientUuid: String?,
        locationUuid: String?,
        value: String?,
        status: String?,
        obsDateTime: String?,
        interpretation: String?,
        conceptuuid: String?,
        order: String?,
        comment: String?
    ) {
        this.uuid =uuid
        this.display = display
        this.encounterUuid = encounterUuid
        this.patientUuid = patientUuid
        this.locationUuid = locationUuid
        this.value = value
        this.status = status
        this.obsDateTime = obsDateTime
        this.interpretation = interpretation
        this.conceptuuid = conceptuuid
        this.order = order
        this.comment = comment
    }
}