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

import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.databases.AppDatabase;
import org.openmrs.mobile.databases.AppDatabaseHelper;
import org.openmrs.mobile.databases.entities.PatientEntity;
import org.openmrs.mobile.models.Patient;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import rx.Observable;

import static org.openmrs.mobile.databases.DBOpenHelper.createObservableIO;

public class PatientDAO {
    AppDatabaseHelper appDatabaseHelper = new AppDatabaseHelper();
    PatientRoomDAO patientRoomDAO = AppDatabase.getDatabase(OpenMRS.getInstance().getApplicationContext()).patientRoomDAO();

    public Observable<Long> savePatient(Patient patient) {
        PatientEntity entity = appDatabaseHelper.patientToPatientEntity(patient);
        return createObservableIO(() -> patientRoomDAO.addPatient(entity));
    }

    public boolean updatePatient(long patientID, Patient patient) {
        PatientEntity entity = appDatabaseHelper.patientToPatientEntity(patient);
        entity.setId(patientID);
        return patientRoomDAO.updatePatient(entity) > 0;
    }

    public void deletePatient(long id) {
        patientRoomDAO.deletePatient(id);
    }

    public Observable<List<Patient>> getAllPatients() {
        return createObservableIO(() -> {
            List<Patient> patients = new ArrayList<>();
            patientRoomDAO.getAllPatients()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new DisposableSingleObserver<List<PatientEntity>>() {
                        @Override
                        public void onSuccess(List<PatientEntity> patientEntities) {
                            for (PatientEntity entity : patientEntities) {
                                patients.add(appDatabaseHelper.patientEntityToPatient(entity));
                            }
                        }

                        @Override
                        public void onError(Throwable e) {

                        }
                    });
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
        PatientEntity patient = patientRoomDAO.findPatientByUUID(uuid).blockingGet();
        return appDatabaseHelper.patientEntityToPatient(patient);
    }

    public List<Patient> getUnSyncedPatients() {
        List<Patient> patientList = new LinkedList<>();
        List<PatientEntity> unSyncedPatientList = new ArrayList<>();
        unSyncedPatientList = patientRoomDAO.getUnsyncedPatients().blockingGet();
        for (PatientEntity entity : unSyncedPatientList) {
            patientList.add(appDatabaseHelper.patientEntityToPatient(entity));
        }
        return patientList;
    }

    public Patient findPatientByID(String id) {
        try {
            PatientEntity patientEntity = patientRoomDAO.findPatientByID(id).blockingGet();
            return appDatabaseHelper.patientEntityToPatient(patientEntity);
        } catch (Exception e) {
            return null;
        }
    }
}
