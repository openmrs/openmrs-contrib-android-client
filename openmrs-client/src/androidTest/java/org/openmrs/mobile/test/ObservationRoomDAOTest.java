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

package org.openmrs.mobile.test;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.room.Room;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openmrs.mobile.databases.AppDatabase;
import org.openmrs.mobile.databases.entities.ObservationEntity;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class ObservationRoomDAOTest {
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private AppDatabase mDatabase;
    private ObservationEntity expectedObservationEntity = createObservationEntity(10L, "uuid", "diagnosisCertainty", "diagnosisList", "diagnosisNote", "diagnosisOrder", "displayValue");
    private ObservationEntity updatedObservationEntity = createObservationEntity(20L, "updatedUuid", "updatedDiagnosisCertainty", "updatedDiagnosisList", "updatedDiagnosisNote", "updatedDiagnosisOrder", "updatedDisplayValue");

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
        mDatabase.observationRoomDAO().saveObservation(expectedObservationEntity);
        mDatabase.observationRoomDAO().getObservationByUUID("uuid").subscribe(new Subscriber<ObservationEntity>() {
            @Override
            public void onSubscribe(Subscription s) {

            }

            @Override
            public void onNext(ObservationEntity observationEntity) {
                Assert.assertEquals(observationEntity.getEncounterKeyID(), 10);
                Assert.assertEquals(observationEntity.getConceptuuid(), "uuid");
                Assert.assertEquals(observationEntity.getDiagnosisCertainty(), "diagnosisCertainty");
                Assert.assertEquals(observationEntity.getDiagnosisList(), "diagnosisList");
                Assert.assertEquals(observationEntity.getDiagnosisNote(), "diagnosisNote");
                Assert.assertEquals(observationEntity.getDiagnosisOrder(), "diagnosisOrder");
                Assert.assertEquals(observationEntity.getDisplayValue(), "displayValue");
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    @Test
    public void updateObservation_ShouldUpdateObservation() {
        mDatabase.observationRoomDAO().saveObservation(expectedObservationEntity);
        mDatabase.observationRoomDAO().updateObservation(updatedObservationEntity);
        mDatabase.observationRoomDAO().getObservationByUUID("updatedUuid").subscribe(new Subscriber<ObservationEntity>() {
            @Override
            public void onSubscribe(Subscription s) {

            }

            @Override
            public void onNext(ObservationEntity observationEntity) {
                Assert.assertEquals(observationEntity.getEncounterKeyID(), 20);
                Assert.assertEquals(observationEntity.getConceptuuid(), "updatedUuid");
                Assert.assertEquals(observationEntity.getDiagnosisCertainty(), "updatedDiagnosisCertainty");
                Assert.assertEquals(observationEntity.getDiagnosisList(), "updatedDiagnosisList");
                Assert.assertEquals(observationEntity.getDiagnosisNote(), "updatedDiagnosisNote");
                Assert.assertEquals(observationEntity.getDiagnosisOrder(), "updatedDiagnosisOrder");
                Assert.assertEquals(observationEntity.getDisplayValue(), "updatedDisplayValue");
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    @Test
    public void deleteObservation_ShouldDeleteObservation() {
        mDatabase.observationRoomDAO().saveObservation(expectedObservationEntity);
        mDatabase.observationRoomDAO().deleteObservation(expectedObservationEntity);
        mDatabase.observationRoomDAO().getObservationByUUID("uuid").subscribe(new Subscriber<ObservationEntity>() {
            @Override
            public void onSubscribe(Subscription s) {

            }

            @Override
            public void onNext(ObservationEntity observationEntity) {
                Assert.assertNull(observationEntity);
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    @Test
    public void getObservationByEncounterId_ShouldGetRightObservation() {
        mDatabase.observationRoomDAO().saveObservation(expectedObservationEntity);
        mDatabase.observationRoomDAO().findObservationByEncounterID(10).subscribe(new Subscriber<List<ObservationEntity>>() {
            @Override
            public void onSubscribe(Subscription s) {

            }

            @Override
            public void onNext(List<ObservationEntity> observationEntities) {
                ObservationEntity observationEntity = observationEntities.get(0);
                Assert.assertEquals(observationEntity.getEncounterKeyID(), 10);
                Assert.assertEquals(observationEntity.getConceptuuid(), "uuid");
                Assert.assertEquals(observationEntity.getDiagnosisCertainty(), "diagnosisCertainty");
                Assert.assertEquals(observationEntity.getDiagnosisList(), "diagnosisList");
                Assert.assertEquals(observationEntity.getDiagnosisNote(), "diagnosisNote");
                Assert.assertEquals(observationEntity.getDiagnosisOrder(), "diagnosisOrder");
                Assert.assertEquals(observationEntity.getDisplayValue(), "displayValue");
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    @After
    public void closeDb() {
        mDatabase.close();
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

}