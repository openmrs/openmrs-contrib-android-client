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

package org.openmrs.mobile.dao;

import com.example.openmrs_android_sdk.library.databases.entities.ObservationEntity;
import com.example.openmrs_android_sdk.library.models.Observation;

import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.databases.AppDatabase;
import org.openmrs.mobile.databases.AppDatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class ObservationDAO {
    ObservationRoomDAO observationRoomDAO = AppDatabase.getDatabase(OpenMRS.getInstance().getApplicationContext()).observationRoomDAO();

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
