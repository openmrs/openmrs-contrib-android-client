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
import com.openmrs.android_sdk.library.databases.entities.PatientEntity;
import com.openmrs.android_sdk.library.models.Patient;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import rx.Observable;

/**
 * The type Patient dao.
 */
public class PatientDAO {
    /**
     * The Patient room dao.
     */
    PatientRoomDAO patientRoomDAO = AppDatabase.getDatabase(OpenmrsAndroid.getInstance().getApplicationContext()).patientRoomDAO();

    /**
     * Save patient observable.
     *
     * @param patient the patient
     * @return the observable
     */
    public Observable<Long> savePatient(Patient patient) {
        PatientEntity entity = AppDatabaseHelper.convert(patient);
        return AppDatabaseHelper.createObservableIO(() -> patientRoomDAO.addPatient(entity));
    }

    /**
     * Update patient boolean.
     *
     * @param patientID the patient id
     * @param patient   the patient
     * @return the boolean
     */
    public boolean updatePatient(long patientID, Patient patient) {
        PatientEntity entity = AppDatabaseHelper.convert(patient);
        entity.setId(patientID);
        return patientRoomDAO.updatePatient(entity) > 0;
    }

    /**
     * Delete patient.
     *
     * @param id the id
     */
    public void deletePatient(long id) {
        patientRoomDAO.deletePatient(id);
    }

    /**
     * Gets all patients.
     *
     * @return the all patients
     */
    public Observable<List<Patient>> getAllPatients() {
        return AppDatabaseHelper.createObservableIO(() -> {
            List<Patient> patients = new ArrayList<>();
            List<PatientEntity> patientEntities = new ArrayList<>();
            try {
                patientEntities = patientRoomDAO.getAllPatients().blockingGet();
                for (PatientEntity entity : patientEntities) {
                    patients.add(AppDatabaseHelper.convert(entity));
                }
            } catch (Exception e) {
                return new ArrayList<>();
            }
            return patients;
        });
    }

    /**
     * Is user already saved boolean.
     *
     * @param uuid the uuid
     * @return the boolean
     */
    public boolean isUserAlreadySaved(String uuid) {
        try {
            PatientEntity patientEntity = patientRoomDAO.findPatientByUUID(uuid).blockingGet();
            return uuid.equalsIgnoreCase(patientEntity.getUuid());
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * User does not exist boolean.
     *
     * @param uuid the uuid
     * @return the boolean
     */
    public boolean userDoesNotExist(String uuid) {
        return !isUserAlreadySaved(uuid);
    }

    /**
     * Find patient by uuid patient.
     *
     * @param uuid the uuid
     * @return the patient
     */
    public Patient findPatientByUUID(String uuid) {
        try {
            PatientEntity patient = patientRoomDAO.findPatientByUUID(uuid).blockingGet();
            return AppDatabaseHelper.convert(patient);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Gets un synced patients.
     *
     * @return the un synced patients
     */
    public List<Patient> getUnSyncedPatients() {
        List<Patient> patientList = new LinkedList<>();
        List<PatientEntity> unSyncedPatientList;
        try {
            unSyncedPatientList = patientRoomDAO.getUnsyncedPatients().blockingGet();
            for (PatientEntity entity : unSyncedPatientList) {
                patientList.add(AppDatabaseHelper.convert(entity));
            }
            return patientList;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    /**
     * Find patient by id patient.
     *
     * @param id the id
     * @return the patient
     */
    public Patient findPatientByID(String id) {
        try {
            PatientEntity patientEntity = patientRoomDAO.findPatientByID(id).blockingGet();
            return AppDatabaseHelper.convert(patientEntity);
        } catch (Exception e) {
            return null;
        }
    }
}
