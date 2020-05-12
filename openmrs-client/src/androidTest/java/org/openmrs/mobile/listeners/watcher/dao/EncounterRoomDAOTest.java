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
import org.openmrs.mobile.databases.AppDatabase;
import org.openmrs.mobile.databases.entities.EncounterEntity;
import org.openmrs.mobile.databases.entities.ObservationEntity;
import org.openmrs.mobile.databases.entities.PatientEntity;
import org.openmrs.mobile.databases.entities.VisitEntity;
import org.openmrs.mobile.models.EncounterType;

import java.util.Objects;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class EncounterRoomDAOTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private EncounterEntity expectedEncounterEntity1 = newEncounterEntity(10L, "123", "Encounter 1", "20", "123-123", "40", "Vitals", "18 January");
    private EncounterEntity expectedEncounterEntity2 = newEncounterEntity(20L, "124", "Encounter 2", "30", "124-124", "50", "Visit Note", "19 January");
    private EncounterEntity expectedEncounterEntity3 = newEncounterEntity(30L, "125", "Encounter 3", null, "50", "60", "Visit 3", "20 January");

    private PatientEntity expectedPatientEntity1 = newPatientEntity(40L, "123-123", "M", "Beijing", "Shanghai", "34", "China", "who knows", "Missouri", "USA", "12/10/1903", expectedEncounterEntity1, "Tranquil", "male", "Jon", "101", "Johnson", "https://bit.ly/2W4Ofth", "2000000", "China", false);

    private VisitEntity expectedVisitEntity = createVisitEntity(1L, 40L, "startDate", "stopDate", "visitPlace", "visitType", "uuid");

    private ObservationEntity expectedObservationEntity = createObservationEntity(10L, "uuid", "diagnosisCertainty", "diagnosisList", "diagnosisNote", "diagnosisOrder", "displayValue");

    private EncounterType encounterType = new EncounterType(EncounterType.VITALS);

    private AppDatabase mDatabase;

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

        mDatabase.encounterRoomDAO().getEncounterTypeByFormName(expectedEncounterEntity1.getDisplay())
                .test()
                .assertValue(actualEncounterEntities -> Objects.equals(actualEncounterEntities.size(), 1)
                        && Objects.equals(renderEncounterEntityString(actualEncounterEntities.get(0)), renderEncounterEntityString(expectedEncounterEntity1)));
    }

    @Test
    public void getLastVitalsEncounterID_ShouldGetCorrectLastVitalsEncounterID() {
        mDatabase.encounterRoomDAO().saveEncounter(expectedEncounterEntity3);

        mDatabase.encounterRoomDAO().getLastVitalsEncounterID("50")
                .test()
                .assertValue(actualEncounterID -> Objects.equals(actualEncounterID, expectedEncounterEntity3.getId()));
    }

    @Test
    public void getLastVitalsEncounter_ShouldGetCorrectLastVitalsEncounter() {
        mDatabase.encounterRoomDAO().saveEncounter(expectedEncounterEntity1);
        mDatabase.encounterRoomDAO().saveEncounter(expectedEncounterEntity2);

        mDatabase.encounterRoomDAO().getLastVitalsEncounter(expectedEncounterEntity1.getPatientUuid(), expectedEncounterEntity1.getEncounterType())
                .test()
                .assertValue(actualEncounterEntities -> Objects.equals(actualEncounterEntities.size(), 1)
                        && Objects.equals(renderEncounterEntityString(actualEncounterEntities.get(0)), renderEncounterEntityString(expectedEncounterEntity1)));
    }

    @Test
    public void getEncounterByUUID_ShouldGetCorrectEncounterByUUID() {
        mDatabase.encounterRoomDAO().saveEncounter(expectedEncounterEntity1);
        mDatabase.encounterRoomDAO().saveEncounter(expectedEncounterEntity2);

        mDatabase.encounterRoomDAO().getEncounterByUUID(expectedEncounterEntity1.getUuid())
                .test()
                .assertValue(actualEncounterID -> Objects.equals(actualEncounterID, expectedEncounterEntity1.getId()));
    }

    @Test
    public void getEncountersByVisitID_ShouldFindCorrectEncountersByVisitID() {
        mDatabase.encounterRoomDAO().saveEncounter(expectedEncounterEntity1);
        mDatabase.encounterRoomDAO().saveEncounter(expectedEncounterEntity2);

        mDatabase.encounterRoomDAO().findEncountersByVisitID(expectedEncounterEntity2.getVisitKeyId())
                .test()
                .assertValue(actualEncounterEntities -> Objects.equals(actualEncounterEntities.size(), 1)
                        && Objects.equals(renderEncounterEntityString(actualEncounterEntities.get(0)), renderEncounterEntityString(expectedEncounterEntity2)));
    }

    @Test
    public void deleteEncounter_ShouldDeleteEncounter() {
        mDatabase.encounterRoomDAO().saveEncounter(expectedEncounterEntity1);
        mDatabase.encounterRoomDAO().saveEncounter(expectedEncounterEntity2);

        mDatabase.encounterRoomDAO().deleteEncounter(expectedEncounterEntity1.getUuid());

        mDatabase.encounterRoomDAO().getAllEncounters()
                .test()
                .assertValue(actualEncounterEntities -> Objects.equals(actualEncounterEntities.size(), 1)
                        && Objects.equals(renderEncounterEntityString(actualEncounterEntities.get(0)), renderEncounterEntityString(expectedEncounterEntity2)));
    }

    @Test
    public void getAllEncountersByType_ShouldGetCorrectEncounterByType() {
        expectedEncounterEntity1.setPatientUuid(expectedPatientEntity1.getUuid());
        expectedEncounterEntity1.setVisitKeyId(String.valueOf(expectedVisitEntity.getId()));

        mDatabase.encounterRoomDAO().saveEncounter(expectedEncounterEntity1);
        mDatabase.patientRoomDAO().savePatient(expectedPatientEntity1);
        mDatabase.visitRoomDAO().saveVisit(expectedVisitEntity);
        mDatabase.observationRoomDAO().saveObservation(expectedObservationEntity);

        mDatabase.encounterRoomDAO().getAllEncountersByType(expectedVisitEntity.getPatientKeyID(), encounterType.getDisplay())
                .test()
                .assertValue(actualEncounterEntities -> Objects.equals(actualEncounterEntities.size(), 1)
                        && Objects.equals(renderEncounterEntityString(actualEncounterEntities.get(0)), renderEncounterEntityString(expectedEncounterEntity1)));
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

    private VisitEntity createVisitEntity(long id, long patientID, String startDate, String stopDate, String visitPlace, String visitType, String uuid) {
        VisitEntity visitEntity = new VisitEntity();
        visitEntity.setId(id);
        visitEntity.setPatientKeyID(patientID);
        visitEntity.setStartDate(startDate);
        visitEntity.setStopDate(stopDate);
        visitEntity.setVisitPlace(visitPlace);
        visitEntity.setVisitType(visitType);
        visitEntity.setUuid(uuid);
        return visitEntity;
    }

    private ObservationEntity createObservationEntity(long id, String uuid, String diagnosisCertainty, String diagnosisList, String diagnosisNote, String diagnosisOrder, String displayValue) {
        ObservationEntity entity = new ObservationEntity();
        entity.setEncounterKeyID(id);
        entity.setConceptuuid(uuid);
        entity.setUuid("uuid");
        entity.setDiagnosisCertainty(diagnosisCertainty);
        entity.setDiagnosisList(diagnosisList);
        entity.setDiagnosisNote(diagnosisNote);
        entity.setDiagnosisOrder(diagnosisOrder);
        entity.setDisplayValue(displayValue);
        return entity;
    }

    private String renderEncounterEntityString(EncounterEntity encounterEntity) {
        return encounterEntity.getId() + encounterEntity.getUuid() + encounterEntity.getDisplay() + encounterEntity.getVisitKeyId()
                + encounterEntity.getPatientUuid() + encounterEntity.getFormUuid()
                + encounterEntity.getEncounterType() + encounterEntity.getEncounterDateTime();
    }
}