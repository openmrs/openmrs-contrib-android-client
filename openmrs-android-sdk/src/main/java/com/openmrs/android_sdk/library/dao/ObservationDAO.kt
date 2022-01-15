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
package com.openmrs.android_sdk.library.dao

import com.openmrs.android_sdk.library.OpenmrsAndroid
import com.openmrs.android_sdk.library.databases.AppDatabase
import com.openmrs.android_sdk.library.databases.AppDatabaseHelper
import com.openmrs.android_sdk.library.databases.entities.ObservationEntity
import com.openmrs.android_sdk.library.models.Observation
import java.util.*

/**
 * The type Observation dao.
 */
class ObservationDAO {
    /**
     * The Observation room dao.
     */
    private var observationRoomDAO: ObservationRoomDAO = AppDatabase.getDatabase(
        OpenmrsAndroid.getInstance()!!.applicationContext
    ).observationRoomDAO()

    /**
     * Find observation by encounter id list.
     *
     * @param encounterID the encounter id
     * @return the list
     */
    fun findObservationByEncounterID(encounterID: Long?): List<Observation> {
        val observationList: List<Observation>
        val observationEntityList: List<ObservationEntity>
        return try {
            observationEntityList = observationRoomDAO.findObservationByEncounterID(encounterID)
                .blockingGet()
            observationList = AppDatabaseHelper.convert(observationEntityList)
            observationList
        } catch (e: Exception) {
            ArrayList()
        }
    }
}