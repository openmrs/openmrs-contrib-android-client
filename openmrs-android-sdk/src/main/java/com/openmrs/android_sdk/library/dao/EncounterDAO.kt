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
import com.openmrs.android_sdk.library.databases.entities.EncounterEntity;
import com.openmrs.android_sdk.library.databases.entities.ObservationEntity;
import com.openmrs.android_sdk.library.models.Encounter;
import com.openmrs.android_sdk.library.models.EncounterType;
import com.openmrs.android_sdk.library.models.Observation;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;

/**
 * The type Encounter dao.
 */
public class EncounterDAO {
    EncounterRoomDAO encounterRoomDAO = AppDatabase.getDatabase(OpenmrsAndroid.getInstance().getApplicationContext()).encounterRoomDAO();
    ObservationRoomDAO observationRoomDAO = AppDatabase.getDatabase(OpenmrsAndroid.getInstance().getApplicationContext()).observationRoomDAO();
    EncounterTypeRoomDAO encounterTypeRoomDAO = AppDatabase.getDatabase(OpenmrsAndroid.getInstance().getApplicationContext()).encounterTypeRoomDAO();

    /**
     * Save encounter long.
     *
     * @param encounter the encounter
     * @param visitID   the visit id
     * @return the long
     */
    public long saveEncounter(Encounter encounter, Long visitID) {
        EncounterEntity encounterEntity = AppDatabaseHelper.convert(encounter, visitID);
        long id = encounterRoomDAO.addEncounter(encounterEntity);
        return id;
    }

    /**
     * Gets encounter type by form name.
     *
     * @param formname the formname
     * @return the encounter type by form name
     */
    public EncounterType getEncounterTypeByFormName(String formname) {
        return encounterTypeRoomDAO.getEncounterTypeByFormName(formname);
    }

    /**
     * Save last vitals encounter.
     *
     * @param encounter   the encounter
     * @param patientUUID the patient uuid
     */
    public void saveLastVitalsEncounter(Encounter encounter, String patientUUID) {
        if (null != encounter) {
            encounter.setPatientUUID(patientUUID);
            long oldLastVitalsEncounterID;
            try {
                oldLastVitalsEncounterID = encounterRoomDAO.getLastVitalsEncounterID(patientUUID).blockingGet();
            } catch (Exception e) {
                oldLastVitalsEncounterID = 0;
            }
            if (0 != oldLastVitalsEncounterID) {
                for (Observation obs : new ObservationDAO().findObservationByEncounterID(oldLastVitalsEncounterID)) {
                    ObservationEntity observationEntity = AppDatabaseHelper.convert(obs, 1L);
                    observationRoomDAO.deleteObservation(observationEntity);
                }
                encounterRoomDAO.deleteEncounterByID(oldLastVitalsEncounterID);
            }
            long encounterID = saveEncounter(encounter, null);
            for (Observation obs : encounter.getObservations()) {
                ObservationEntity observationEntity = AppDatabaseHelper.convert(obs, encounterID);
                observationRoomDAO.addObservation(observationEntity);
            }
        }
    }

    /**
     * Gets last vitals encounter.
     *
     * @param patientUUID the patient uuid
     * @return the last vitals encounter
     */
    public Observable<Encounter> getLastVitalsEncounter(String patientUUID) {
        return AppDatabaseHelper.createObservableIO(() -> {
            try {
                EncounterEntity encounterEntity = encounterRoomDAO.getLastVitalsEncounter(patientUUID, EncounterType.VITALS).blockingGet();
                return AppDatabaseHelper.convert(encounterEntity);
            } catch (Exception e) {
                return null;
            }
        });
    }

    /**
     * Update encounter int.
     *
     * @param encounterID the encounter id
     * @param encounter   the encounter
     * @param visitID     the visit id
     * @return the int
     */
    public int updateEncounter(long encounterID, Encounter encounter, long visitID) {
        EncounterEntity encounterEntity = AppDatabaseHelper.convert(encounter, visitID);
        encounterEntity.setId(encounterID);
        int id = encounterRoomDAO.updateEncounter(encounterEntity);
        return id;
    }

    /**
     * Find encounters by visit id list.
     *
     * @param visitID the visit id
     * @return the list
     */
    public List<Encounter> findEncountersByVisitID(Long visitID) {
        List<Encounter> encounters = new ArrayList<>();
        try {
            List<EncounterEntity> encounterEntities = encounterRoomDAO.findEncountersByVisitID(visitID.toString()).blockingGet();

            for (EncounterEntity entity : encounterEntities) {
                encounters.add(AppDatabaseHelper.convert(entity));
            }
            return encounters;
        } catch (Exception e) {
            return encounters;
        }
    }

    /**
     * Gets all encounters by type.
     *
     * @param patientID the patient id
     * @param type      the type
     * @return the all encounters by type
     */
    public Observable<List<Encounter>> getAllEncountersByType(Long patientID, EncounterType type) {
        return AppDatabaseHelper.createObservableIO(() -> {
            List<Encounter> encounters = new ArrayList<>();
            List<EncounterEntity> encounterEntities;
            try {
                encounterEntities = encounterRoomDAO.getAllEncountersByType(patientID, type.getDisplay()).blockingGet();
                for (EncounterEntity entity : encounterEntities) {
                    encounters.add(AppDatabaseHelper.convert(entity));
                }
                return encounters;
            } catch (Exception e) {
                return new ArrayList<>();
            }
        });
    }

    /**
     * Gets encounter by uuid.
     *
     * @param encounterUUID the encounter uuid
     * @return the encounter by uuid
     */
    public long getEncounterByUUID(final String encounterUUID) {
        try {
            return encounterRoomDAO.getEncounterByUUID(encounterUUID).blockingGet();
        } catch (Exception e) {
            return 0;
        }
    }
}
