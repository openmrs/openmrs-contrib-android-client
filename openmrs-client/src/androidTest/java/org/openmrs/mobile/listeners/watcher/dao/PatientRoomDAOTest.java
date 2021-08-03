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

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.room.Room;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.openmrs.android_sdk.library.dao.PatientRoomDAO;
import com.openmrs.android_sdk.library.databases.AppDatabase;

import com.openmrs.android_sdk.library.databases.entities.PatientEntity;

import java.util.Objects;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class PatientRoomDAOTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();
    private PatientEntity expectedPatientEntity1 = newPatientEntity(10L, "123", "M", "Beijing", "Shanghai", "34", "China", "who knows", "Missouri", "USA", "12/10/1903", "expectedEncounterEntity1", "Tranquil", "male", "Jon", "101", "Johnson", "2000000", "China", false, "true");
    private PatientEntity expectedPatientEntity2 = newPatientEntity(20L, "124", "M", "Beijing", "Shanghai", "34", "China", "who knows", "Missouri", "USA", "12/10/1903", "expectedEncounterEntity2", "Tranquil", "male", "Jon", "101", "Johnson", "2000000", "China", false, "false");


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
        patientRoomDAO.addPatient(expectedPatientEntity1);
        patientRoomDAO.addPatient(expectedPatientEntity2);

        patientRoomDAO.getAllPatients()
                .test()
                .assertValue(actualPatientEntities -> Objects.equals(actualPatientEntities.size(), 2)
                        && Objects.equals(renderPatientEntityString(actualPatientEntities.get(0)), renderPatientEntityString(expectedPatientEntity1))
                        && Objects.equals(renderPatientEntityString(actualPatientEntities.get(1)), renderPatientEntityString(expectedPatientEntity2)));
    }

    @Test
    public void findPatientByUUID_ShouldFindCorrectPatientByUUID() {
        patientRoomDAO.addPatient(expectedPatientEntity1);
        patientRoomDAO.addPatient(expectedPatientEntity2);

        patientRoomDAO.findPatientByUUID(expectedPatientEntity2.getUuid())
                .test()
                .assertValue(actualPatientEntities -> Objects.equals(renderPatientEntityString(actualPatientEntities), renderPatientEntityString(expectedPatientEntity2)));
    }

    @Test
    public void getUnSyncedPatients_ShouldGetUnSyncedPatients() {
        patientRoomDAO.addPatient(expectedPatientEntity1);
        patientRoomDAO.addPatient(expectedPatientEntity2);

        patientRoomDAO.getUnsyncedPatients()
                .test()
                .assertValue(actualPatientEntities -> Objects.equals(actualPatientEntities.size(), 2)
                        && Objects.equals(renderPatientEntityString(actualPatientEntities.get(0)), renderPatientEntityString(expectedPatientEntity1))
                        && Objects.equals(renderPatientEntityString(actualPatientEntities.get(1)), renderPatientEntityString(expectedPatientEntity2)));
    }

    @Test
    public void findPatientByID_ShouldGetCorrectPatientByID() {
        patientRoomDAO.addPatient(expectedPatientEntity1);
        patientRoomDAO.addPatient(expectedPatientEntity2);

        patientRoomDAO.findPatientByID(expectedPatientEntity1.getId().toString())
                .test()
                .assertValue(actualPatientEntities -> Objects.equals(renderPatientEntityString(actualPatientEntities), renderPatientEntityString(expectedPatientEntity1)));
    }

    @Test
    public void deletePatientByID_ShouldDeleteCorrectPatientByID() {
        patientRoomDAO.addPatient(expectedPatientEntity1);
        patientRoomDAO.addPatient(expectedPatientEntity2);

        patientRoomDAO.deletePatient(20L);

        patientRoomDAO.getAllPatients()
                .test()
                .assertValue(actualPatientEntities -> Objects.equals(actualPatientEntities.size(), 1)
                        && Objects.equals(renderPatientEntityString(actualPatientEntities.get(0)), renderPatientEntityString(expectedPatientEntity1)));
    }

    @Test
    public void updatePatient_ShouldCorrectlyUpdatePatient() {
        patientRoomDAO.addPatient(expectedPatientEntity1);

        expectedPatientEntity1.setGivenName("Rishabh");
        expectedPatientEntity1.setFamilyName("Agarwal");

        patientRoomDAO.updatePatient(expectedPatientEntity1);

        patientRoomDAO.getAllPatients()
                .test()
                .assertValue(actualPatientEntities -> Objects.equals(actualPatientEntities.size(), 1)
                        && Objects.equals(actualPatientEntities.get(0).getGivenName(), "Rishabh")
                        && Objects.equals(actualPatientEntities.get(0).getFamilyName(), "Agarwal"));

    }

    private PatientEntity newPatientEntity(long id, String uuid, String display,
                                           String address1, String address2, String age,
                                           String birthDate, String causeOfDeath, String city,
                                           String country, String deathDate, String encounterEntity,
                                           String familyName, String gender, String givenName,
                                           String identifier, String middleName,
                                           String postalCode, String state, boolean synced, String dead) {
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
        entity.setPhoto(null);
        entity.setPostalCode(postalCode);
        entity.setState(state);
        entity.setSynced(synced);
        entity.setDeceased(dead);
        return entity;
    }

    private String renderPatientEntityString(PatientEntity patientEntity) {
        return patientEntity.getId() + patientEntity.getDisplay()
                + patientEntity.getAddress_1() + patientEntity.getAddress_2()
                + patientEntity.getAge() + patientEntity.getBirthDate() + patientEntity.getCauseOfDeath()
                + patientEntity.getCity() + patientEntity.getState() + patientEntity.getCountry()
                + patientEntity.getDeathDate() + patientEntity.getEncounters()
                + patientEntity.getFamilyName() + patientEntity.getGender()
                + patientEntity.getGivenName() + patientEntity.getIdentifier() + patientEntity.getMiddleName()
                + patientEntity.getPhoto() + patientEntity.getPostalCode();
    }
}
