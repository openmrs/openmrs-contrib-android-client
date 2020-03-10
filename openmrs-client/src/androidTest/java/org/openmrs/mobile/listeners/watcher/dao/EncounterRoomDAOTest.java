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
import org.openmrs.mobile.databases.AppDatabase;
import org.openmrs.mobile.databases.entities.EncounterEntity;
import org.openmrs.mobile.databases.entities.PatientEntity;
import org.openmrs.mobile.models.EncounterType;
import org.openmrs.mobile.utilities.ActiveAndroid.util.Log;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class EncounterRoomDAOTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private EncounterEntity expectedEncounterEntity1 = newEncounterEntity(10L, "123", "Encounter 1", "20", "123-123", "40", "Vitals", "18 January");
    private EncounterEntity expectedEncounterEntity2 = newEncounterEntity(20L, "124", "Encounter 2", "30", "124-124", "50", "Visit Note", "19 January");
    private EncounterEntity expectedEncounterEntity3 = newEncounterEntity(30L, "125", "Encounter 3", null, "50", "60", "Visit 3", "20 January");

    private PatientEntity expectedPatientEntity1 = newPatientEntity(40L, "123-123", "M", "Beijing", "Shanghai", "34", "China", "who knows", "Missouri", "USA", "12/10/1903", expectedEncounterEntity1, "Tranquil", "male", "Jon", "101", "Johnson", "https://bit.ly/2W4Ofth", "2000000", "China", false);
    private PatientEntity expectedPatientEntity2 = newPatientEntity(50L, "124-124", "M", "Beijing", "Shanghai", "34", "China", "who knows", "Missouri", "USA", "12/10/1903", expectedEncounterEntity2, "Tranquil", "male", "Jon", "101", "Johnson", "https://bit.ly/2W4Ofth", "2000000", "China", false);

    private EncounterType encounterType = new EncounterType(EncounterType.VITALS);

    private AppDatabase mDatabase;
    private String TAG = "EncounterRoomDAOTest";

    @Before
    public void initDb() {
        mDatabase = Room.inMemoryDatabaseBuilder(
                InstrumentationRegistry.getInstrumentation().getTargetContext(),
                AppDatabase.class)
                .allowMainThreadQueries()
                .build();
    }

    @After
    public void closeDb() {
        mDatabase.close();
    }

    @Test
    public void findEncounterByFormName_ShouldFindCorrectEncounterByFormName() {
        mDatabase.encounterRoomDAO().saveEncounter(expectedEncounterEntity1);

        mDatabase.encounterRoomDAO().getEncounterTypeByFormName("Encounter 1").subscribe(new SingleObserver<List<EncounterEntity>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onSuccess(List<EncounterEntity> encounterEntities) {
                Assert.assertEquals(encounterEntities.get(0), expectedEncounterEntity1);
            }

            @Override
            public void onError(Throwable e) {
                Log.i(TAG, e.getMessage());
            }
        });
    }

    @Test
    public void getLastVitalsEncounterID_ShouldGetCorrectLastVitalsEncounterID() {
        mDatabase.encounterRoomDAO().saveEncounter(expectedEncounterEntity3);

        mDatabase.encounterRoomDAO().getLastVitalsEncounterID("50").subscribe(new SingleObserver<Long>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onSuccess(Long actualEncounterID) {
                Assert.assertEquals(expectedEncounterEntity3.getId(), actualEncounterID);
            }

            @Override
            public void onError(Throwable e) {
                Log.i(TAG, e.getMessage());
            }
        });
    }

    @Test
    public void getLastVitalsEncounter_ShouldGetCorrectLastVitalsEncounter() {
        mDatabase.encounterRoomDAO().saveEncounter(expectedEncounterEntity1);
        mDatabase.encounterRoomDAO().saveEncounter(expectedEncounterEntity2);

        mDatabase.encounterRoomDAO().getLastVitalsEncounter(expectedEncounterEntity1.getPatientUuid(), expectedEncounterEntity1.getEncounterType()).subscribe(new SingleObserver<List<EncounterEntity>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onSuccess(List<EncounterEntity> encounterEntities) {
                Assert.assertEquals(encounterEntities.get(0), expectedEncounterEntity1);
            }

            @Override
            public void onError(Throwable e) {
                Log.i(TAG, e.getMessage());
            }
        });
    }

    @Test
    public void getEncounterByUUID_ShouldGetCorrectEncounterByUUID() {
        mDatabase.encounterRoomDAO().saveEncounter(expectedEncounterEntity1);
        mDatabase.encounterRoomDAO().saveEncounter(expectedEncounterEntity2);

        mDatabase.encounterRoomDAO().getEncounterByUUID(expectedEncounterEntity1.getUuid()).subscribe(new SingleObserver<Long>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onSuccess(Long actualEncounterID) {
                Assert.assertEquals(actualEncounterID, expectedEncounterEntity1.getId());
            }

            @Override
            public void onError(Throwable e) {
                Log.i(TAG, e.getMessage());
            }
        });
    }

    @Test
    public void getEncountersByVisitID_ShouldFindCorrectEncountersByVisitID() {
        mDatabase.encounterRoomDAO().saveEncounter(expectedEncounterEntity1);
        mDatabase.encounterRoomDAO().saveEncounter(expectedEncounterEntity2);

        mDatabase.encounterRoomDAO().findEncountersByVisitID(expectedEncounterEntity2.getVisitKeyId()).subscribe(new SingleObserver<List<EncounterEntity>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onSuccess(List<EncounterEntity> encounterEntities) {
                Assert.assertEquals(encounterEntities.get(0), expectedEncounterEntity2);
            }

            @Override
            public void onError(Throwable e) {
                Log.i(TAG, e.getMessage());
            }
        });
    }

    @Test
    public void deleteEncounter_ShouldDeleteEncounter() {
        mDatabase.encounterRoomDAO().saveEncounter(expectedEncounterEntity1);
        mDatabase.encounterRoomDAO().saveEncounter(expectedEncounterEntity2);

        mDatabase.encounterRoomDAO().deleteEncounter(expectedEncounterEntity1.getUuid());

        mDatabase.encounterRoomDAO().getAllEncounters().subscribe(new SingleObserver<List<EncounterEntity>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onSuccess(List<EncounterEntity> encounterEntities) {
                Assert.assertEquals(encounterEntities.size(), 1);
                Assert.assertEquals(encounterEntities.get(0), expectedEncounterEntity2);
            }

            @Override
            public void onError(Throwable e) {
                Log.i(TAG, e.getMessage());
            }
        });
    }

    @Test
    public void getAllEncountersByType_ShouldGetCorrectEncounterByType() {
        mDatabase.patientRoomDAO().savePatient(expectedPatientEntity1);
        mDatabase.patientRoomDAO().savePatient(expectedPatientEntity2);

        mDatabase.encounterRoomDAO().getAllEncountersByType(expectedPatientEntity1.getId(), encounterType.getDisplay()).subscribe(new SingleObserver<List<EncounterEntity>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onSuccess(List<EncounterEntity> encounterEntities) {
                Assert.assertEquals(encounterEntities.get(0), expectedEncounterEntity1);
            }

            @Override
            public void onError(Throwable e) {
                Log.i(TAG, e.getMessage());
            }
        });
    }

    private EncounterEntity newEncounterEntity(long id, String uuid, String display, String visitKeyID, String patientUUID, String formUUID, String encounterType, String encounterDayTime) {
        EncounterEntity encounterEntity = new EncounterEntity();

        encounterEntity.setId(id);
        encounterEntity.setUuid(uuid);
        encounterEntity.setDisplay(display);
        encounterEntity.setVisitKeyId(visitKeyID);
        encounterEntity.setPatientUuid(patientUUID);
        encounterEntity.setFormUuid(formUUID);
        encounterEntity.setEncounterType(encounterType);
        encounterEntity.setEncounterDateTime(encounterDayTime);

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