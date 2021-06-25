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

package com.example.openmrs_android_sdk.library.dao;

import com.example.openmrs_android_sdk.library.OpenmrsAndroid;
import com.example.openmrs_android_sdk.library.databases.AppDatabase;
import com.example.openmrs_android_sdk.library.databases.AppDatabaseHelper;
import com.example.openmrs_android_sdk.library.databases.entities.PatientEntity;
import com.example.openmrs_android_sdk.library.models.Patient;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import rx.Observable;

public class PatientDAO {
    PatientRoomDAO patientRoomDAO = AppDatabase.getDatabase(OpenmrsAndroid.getInstance().getApplicationContext()).patientRoomDAO();

    public Observable<Long> savePatient(Patient patient) {
        PatientEntity entity = AppDatabaseHelper.convert(patient);
        return AppDatabaseHelper.createObservableIO(() -> patientRoomDAO.addPatient(entity));
    }

    public boolean updatePatient(long patientID, Patient patient) {
        PatientEntity entity = AppDatabaseHelper.convert(patient);
        entity.setId(patientID);
        return patientRoomDAO.updatePatient(entity) > 0;
    }

    public void deletePatient(long id) {
        patientRoomDAO.deletePatient(id);
    }

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

    public boolean isUserAlreadySaved(String uuid) {
        try {
            PatientEntity patientEntity = patientRoomDAO.findPatientByUUID(uuid).blockingGet();
            return uuid.equalsIgnoreCase(patientEntity.getUuid());
        } catch (Exception e) {
            return false;
        }
    }

    public boolean userDoesNotExist(String uuid) {
        return !isUserAlreadySaved(uuid);
    }

    public Patient findPatientByUUID(String uuid) {
        try {
            PatientEntity patient = patientRoomDAO.findPatientByUUID(uuid).blockingGet();
            return AppDatabaseHelper.convert(patient);
        } catch (Exception e) {
            return null;
        }
    }

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

    public Patient findPatientByID(String id) {
        try {
            PatientEntity patientEntity = patientRoomDAO.findPatientByID(id).blockingGet();
            return AppDatabaseHelper.convert(patientEntity);
        } catch (Exception e) {
            return null;
        }
    }
}
