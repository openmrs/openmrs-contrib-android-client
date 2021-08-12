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
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.openmrs.android_sdk.library.databases.AppDatabase;
import com.openmrs.android_sdk.library.databases.entities.ObservationEntity;

import java.util.Objects;

@RunWith(AndroidJUnit4.class)
public class ObservationRoomDAOTest {
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private AppDatabase mDatabase;
    private ObservationEntity expectedObservationEntity = createObservationEntity(10L, 100L, "uuid", "diagnosisCertainty", "diagnosisList", "diagnosisNote", "diagnosisOrder", "displayValue");
    private ObservationEntity updatedObservationEntity = createObservationEntity(10L, 100L, "updatedUuid", "updatedDiagnosisCertainty", "updatedDiagnosisList", "updatedDiagnosisNote", "updatedDiagnosisOrder", "updatedDisplayValue");

    @Before
    public void initDb() {
        mDatabase = Room.inMemoryDatabaseBuilder(
                InstrumentationRegistry.getInstrumentation().getTargetContext(),
                AppDatabase.class)
                // allowing main thread queries, just for testing
                .allowMainThreadQueries()
                .build();
    }

    @Test
    public void saveObservation_ShouldSaveCorrectObservation() {
        mDatabase.observationRoomDAO().addObservation(expectedObservationEntity);
        mDatabase.observationRoomDAO().getObservationByUUID("uuid")
                .test()
                .assertValue(actualObservationEntity -> Objects.equals(actualObservationEntity.getId(), 10L)
                        && Objects.equals(actualObservationEntity.getEncounterKeyID(), 100L)
                        && Objects.equals(actualObservationEntity.getDiagnosisCertainty(), "diagnosisCertainty")
                        && Objects.equals(actualObservationEntity.getDiagnosisList(), "diagnosisList")
                        && Objects.equals(actualObservationEntity.getDiagnosisNote(), "diagnosisNote")
                        && Objects.equals(actualObservationEntity.getDiagnosisOrder(), "diagnosisOrder")
                        && Objects.equals(actualObservationEntity.getDisplayValue(), "displayValue"));
    }

    @Test
    public void updateObservation_ShouldUpdateObservation() {
        mDatabase.observationRoomDAO().addObservation(expectedObservationEntity);
        mDatabase.observationRoomDAO().updateObservation(updatedObservationEntity);
        mDatabase.observationRoomDAO().getObservationByUUID("uuid")
                .test()
                .assertValue(actualObservationEntity -> Objects.equals(actualObservationEntity.getId(), 10L)
                        && Objects.equals(actualObservationEntity.getEncounterKeyID(), 100L)
                        && Objects.equals(actualObservationEntity.getDiagnosisCertainty(), "updatedDiagnosisCertainty")
                        && Objects.equals(actualObservationEntity.getDiagnosisList(), "updatedDiagnosisList")
                        && Objects.equals(actualObservationEntity.getDiagnosisNote(), "updatedDiagnosisNote")
                        && Objects.equals(actualObservationEntity.getDiagnosisOrder(), "updatedDiagnosisOrder")
                        && Objects.equals(actualObservationEntity.getDisplayValue(), "updatedDisplayValue"));
    }

    @Test
    public void deleteObservation_ShouldDeleteObservation() {
        mDatabase.observationRoomDAO().addObservation(expectedObservationEntity);
        mDatabase.observationRoomDAO().deleteObservation(expectedObservationEntity);
        mDatabase.observationRoomDAO().getAllObservations()
                .test()
                .assertValue(actualObservationEntities -> Objects.equals(actualObservationEntities.size(), 0));
    }

    @Test
    public void getObservationByEncounterId_ShouldGetRightObservation() {
        mDatabase.observationRoomDAO().addObservation(expectedObservationEntity);
        mDatabase.observationRoomDAO().findObservationByEncounterID(100L)
                .test()
                .assertValue(observationEntities -> {
                    ObservationEntity actualObservationEntity = observationEntities.get(0);
                    return Objects.equals(actualObservationEntity.getId(), 10L)
                            && Objects.equals(actualObservationEntity.getEncounterKeyID(), 100L)
                            && Objects.equals(actualObservationEntity.getDiagnosisCertainty(), "diagnosisCertainty")
                            && Objects.equals(actualObservationEntity.getDiagnosisList(), "diagnosisList")
                            && Objects.equals(actualObservationEntity.getDiagnosisNote(), "diagnosisNote")
                            && Objects.equals(actualObservationEntity.getDiagnosisOrder(), "diagnosisOrder")
                            && Objects.equals(actualObservationEntity.getDisplayValue(), "displayValue");
                });
    }

    @After
    public void closeDb() {
        mDatabase.close();
    }

    private ObservationEntity createObservationEntity(long id, long encounterID, String conceptUUID, String diagnosisCertainty, String diagnosisList, String diagnosisNote, String diagnosisOrder, String displayValue) {
        ObservationEntity entity = new ObservationEntity();
        entity.setId(id);
        entity.setEncounterKeyID(encounterID);
        entity.setConceptuuid(conceptUUID);
        entity.setUuid("uuid");
        entity.setDiagnosisCertainty(diagnosisCertainty);
        entity.setDiagnosisList(diagnosisList);
        entity.setDiagnosisNote(diagnosisNote);
        entity.setDiagnosisOrder(diagnosisOrder);
        entity.setDisplayValue(displayValue);
        return entity;
    }

}