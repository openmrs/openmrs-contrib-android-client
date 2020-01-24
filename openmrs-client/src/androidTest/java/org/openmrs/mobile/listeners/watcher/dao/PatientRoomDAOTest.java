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

package org.openmrs.mobile.listeners.watcher.dao;

import java.util.List;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.room.Room;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;
import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openmrs.mobile.dao.PatientRoomDAO;
import org.openmrs.mobile.databases.AppDatabase;
import org.openmrs.mobile.databases.entities.EncounterEntity;
import org.openmrs.mobile.databases.entities.PatientEntity;
import org.openmrs.mobile.utilities.ActiveAndroid.util.Log;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class PatientRoomDAOTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();
    private EncounterEntity expectedEncounterEntity1 = newEncounterEntity(40L, "123-123", "Encounter", "50", "60", "70", "Visit", "22nd of May");
    private EncounterEntity expectedEncounterEntity2 = newEncounterEntity(50L, "124-124", "Encounter", "50", "60", "70", "Visit", "23rd of May");
    private PatientEntity expectedPatientEntity1 = newPatientEntity(10L, "123", "M", "Beijing", "Shanghai", "34", "China", "who knows", "Missouri", "USA", "12/10/1903", expectedEncounterEntity1, "Tranquil", "male", "Jon", "101", "Johnson", "https://bit.ly/2W4Ofth", "2000000", "China", false);
    private PatientEntity expectedPatientEntity2 = newPatientEntity(20L, "124", "M", "Beijing", "Shanghai", "34", "China", "who knows", "Missouri", "USA", "12/10/1903", expectedEncounterEntity2, "Tranquil", "male", "Jon", "101", "Johnson", "https://bit.ly/2W4Ofth", "2000000", "China", false);


    private AppDatabase mDatabase;
    private PatientRoomDAO patientRoomDAO;
    private String TAG = "PatientRoomDAOTest";

    @Before
    public void initDb() {
        mDatabase = Room.inMemoryDatabaseBuilder(
                InstrumentationRegistry.getInstrumentation().getTargetContext(),
                AppDatabase.class)
                // allowing main thread queries, just for testing
                .allowMainThreadQueries()
                .build();
        patientRoomDAO = mDatabase.patientRoomDAO();
    }

    @After
    public void closeDb() {
        mDatabase.close();
    }

    @Test
    public void getAllPatients_ShouldGetAllPatientsCorrectly() {
        patientRoomDAO.savePatient(expectedPatientEntity1);
        patientRoomDAO.savePatient(expectedPatientEntity2);

        patientRoomDAO.getAllPatients().subscribe(new SingleObserver<List<PatientEntity>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onSuccess(List<PatientEntity> patientEntities) {
                Assert.assertEquals(patientEntities.get(0), expectedPatientEntity1);
                Assert.assertEquals(patientEntities.get(1), expectedPatientEntity2);
            }

            @Override
            public void onError(Throwable e) {
                Log.i(TAG, e.getMessage());
            }
        });
    }

    @Test
    public void findPatientByUUID_ShouldFindCorrectPatientByUUID() {
        patientRoomDAO.savePatient(expectedPatientEntity1);
        patientRoomDAO.savePatient(expectedPatientEntity2);

        patientRoomDAO.findPatientByUUID(expectedPatientEntity2.getUuid()).subscribe(new SingleObserver<List<PatientEntity>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onSuccess(List<PatientEntity> patientEntities) {
                Assert.assertEquals(patientEntities.get(0), expectedPatientEntity2);
            }

            @Override
            public void onError(Throwable e) {
                Log.i(TAG, e.getMessage());
            }
        });
    }

    @Test
    public void getUnsyncedPatients_ShouldGetUnsyncedPatients() {
        patientRoomDAO.savePatient(expectedPatientEntity1);
        patientRoomDAO.savePatient(expectedPatientEntity2);

        patientRoomDAO.getUnsyncedPatients().subscribe(new SingleObserver<List<PatientEntity>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onSuccess(List<PatientEntity> patientEntities) {
                Assert.assertEquals(patientEntities.size(), 2);
                //since both the global patient entities are not synced
            }

            @Override
            public void onError(Throwable e) {
                Log.i(TAG, e.getMessage());
            }
        });
    }

    @Test
    public void findPatientByID_ShouldGetCorrectPatientByID() {
        patientRoomDAO.savePatient(expectedPatientEntity1);
        patientRoomDAO.savePatient(expectedPatientEntity2);

        patientRoomDAO.findPatientByID(expectedEncounterEntity1.getId()).subscribe(new SingleObserver<List<PatientEntity>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onSuccess(List<PatientEntity> patientEntities) {
                Assert.assertEquals(patientEntities.get(0), expectedPatientEntity1);
            }

            @Override
            public void onError(Throwable e) {
                Log.i(TAG, e.getMessage());
            }
        });
    }

    @Test
    public void deletePatientByID_ShouldDeleteCorrectPatientByID() {
        patientRoomDAO.savePatient(expectedPatientEntity1);
        patientRoomDAO.savePatient(expectedPatientEntity2);

        patientRoomDAO.deletePatient(expectedPatientEntity2.getId());

        patientRoomDAO.getAllPatients().subscribe(new SingleObserver<List<PatientEntity>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onSuccess(List<PatientEntity> patientEntities) {
                Assert.assertEquals(patientEntities.size(), 1);
                Assert.assertEquals(patientEntities.get(0), expectedPatientEntity1);
            }

            @Override
            public void onError(Throwable e) {
                Log.i(TAG, e.getMessage());
            }
        });
    }

    @Test
    public void updatePatient_ShouldCorrectlyUpdatePatient() {
        patientRoomDAO.savePatient(expectedPatientEntity1);

        expectedPatientEntity1.setGivenName("Rishabh");
        expectedPatientEntity1.setFamilyName("Agarwal");

        patientRoomDAO.updatePatient(expectedPatientEntity1);

        patientRoomDAO.getAllPatients().subscribe(new SingleObserver<List<PatientEntity>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onSuccess(List<PatientEntity> patientEntities) {
                Assert.assertEquals(patientEntities.size(), 1);

                Assert.assertEquals(patientEntities.get(0).getGivenName(), "Rishabh");
                Assert.assertEquals(patientEntities.get(0).getFamilyName(), "Agarwal");
            }

            @Override
            public void onError(Throwable e) {
                Log.i(TAG, e.getMessage());
            }
        });
    }

    private EncounterEntity newEncounterEntity(Long id, String uuid, String display, String visitKeyId,
                                               String patientUuid, String formUuid, String encounterType,
                                               String encounterDateTime) {
        EncounterEntity encounterEntity = new EncounterEntity();
        encounterEntity.setId(id);
        encounterEntity.setUuid(uuid);
        encounterEntity.setDisplay(display);
        encounterEntity.setVisitKeyId(visitKeyId);
        encounterEntity.setPatientUuid(patientUuid);
        encounterEntity.setFormUuid(formUuid);
        encounterEntity.setEncounterType(encounterType);
        encounterEntity.setEncounterDateTime(encounterDateTime);
        return encounterEntity;
    }

    private PatientEntity newPatientEntity(long id, String uuid, String display,
                                           String address1, String address2, String age,
                                           String birthDate, String causeOfDeath, String city,
                                           String country, String deathDate, EncounterEntity encounterEntity,
                                           String familyName, String gender, String givenName,
                                           String identifier, String middleName, String photo,
                                           String postalCode, String state, boolean synced) {
        PatientEntity entity = new PatientEntity();
        entity.setId(id);
        entity.setUuid(uuid);
        entity.setDisplay(display);
        entity.setAddress_1(address1);
        entity.setAddress_2(address2);
        entity.setAge(age);
        entity.setBirthDate(birthDate);
        entity.setCauseOfDeath(causeOfDeath);
        entity.setCity(city);
        entity.setCountry(country);
        entity.setDeathDate(deathDate);
        entity.setEncounters(encounterEntity);
        entity.setFamilyName(familyName);
        entity.setGender(gender);
        entity.setGivenName(givenName);
        entity.setIdentifier(identifier);
        entity.setMiddleName(middleName);
        entity.setPhoto(photo);
        entity.setPostalCode(postalCode);
        entity.setState(state);
        entity.setSynced(synced);
        return entity;
    }

}
