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

package com.openmrs.android_sdk.library.dao;

import com.openmrs.android_sdk.library.OpenmrsAndroid;
import com.openmrs.android_sdk.library.databases.AppDatabase;
import com.openmrs.android_sdk.library.databases.AppDatabaseHelper;
import com.openmrs.android_sdk.library.databases.entities.ObservationEntity;
import com.openmrs.android_sdk.library.models.Observation;

import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

/**
 * The type Observation dao.
 */
public class ObservationDAO {
    /**
     * The Observation room dao.
     */
    ObservationRoomDAO observationRoomDAO = AppDatabase.getDatabase(OpenmrsAndroid.getInstance().getApplicationContext()).observationRoomDAO();

    @Inject
    public ObservationDAO() { }
    /**
     * Saves an observation entity to db
     *
     * @param obs the observation model
     * @param encounterID the encounterId
     *
     * @return id (primary key)
     */
    public Long saveObservation(Observation obs, long encounterID) {
        ObservationEntity observationEntity = AppDatabaseHelper.convert(obs, encounterID);
        Long id = observationRoomDAO.addObservation(observationEntity);
        return id;
    }

    /**
     * Update observation in db (ROOM will match the primary keys for updating)
     *
     * @param obs the observation
     * @param encounterId the observation (needed to verify that the observation already exists)
     *
     * @return count of updated values
     */
    public int updateObservation(Observation obs, long encounterId) {
        ObservationEntity observationEntity = AppDatabaseHelper.convert(obs, encounterId);
        observationEntity.setId(encounterId);
        int count = observationRoomDAO.updateObservation(observationEntity);
        return count;
    }

    /**
     * Find observation by encounter id list.
     *
     * @param encounterID the encounter id
     * @return the list
     */
    public List<Observation> findObservationByEncounterID(Long encounterID) {
        List<Observation> observationList;
        List<ObservationEntity> observationEntityList;
        try {
            observationEntityList = observationRoomDAO.findObservationByEncounterID(encounterID).blockingGet();
            observationList = AppDatabaseHelper.convert(observationEntityList);
            return observationList;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

}
