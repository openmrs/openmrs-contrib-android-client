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
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openmrs.mobile.databases.AppDatabase;
import org.openmrs.mobile.databases.entities.VisitsEntity;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import java.util.List;
import static org.hamcrest.CoreMatchers.is;


@RunWith(AndroidJUnit4.class)
public class VisitRoomDAOTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();


    private AppDatabase database;

    @Before
    public void initDb() {
        database = Room.inMemoryDatabaseBuilder(
                androidx.test.platform.app.InstrumentationRegistry.getInstrumentation().getTargetContext(),
                AppDatabase.class)
                // allowing main thread queries, just for testing
                .allowMainThreadQueries()
                .build();
    }

    @Test
    public void updateVisit_shouldUpdateVisit(){
        VisitsEntity visitsEntity = new VisitsEntity();
        createEntity(visitsEntity);
        Long id = database.visitRoomDAO().saveVisit(visitsEntity);
        VisitsEntity updatedVisitsEntity = new VisitsEntity();
        visitsEntity.setPatientKeyID(1);
        visitsEntity.setStartDate("startDate");
        visitsEntity.setStopDate("stopDate");
        visitsEntity.setVisitPlace("visitPlace");
        visitsEntity.setVisitType("visitType");
        visitsEntity.setUuid("updatedUuid");
        database.visitRoomDAO().updateVisit(updatedVisitsEntity);
        database.visitRoomDAO().getVisitByID(id).take(1).subscribe(new Subscriber<VisitsEntity>() {
            @Override
            public void onSubscribe(Subscription s) {

            }

            @Override
            public void onNext(VisitsEntity visitsEntity) {
                Assert.assertThat(visitsEntity.getUuid(), is("updateUuid"));
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
    public void saveVisit_ShouldSaveCorrectVisist() {
        VisitsEntity visitsEntity = new VisitsEntity();
        createEntity(visitsEntity);
        Long id  = database.visitRoomDAO().saveVisit(visitsEntity);
        database.visitRoomDAO().getVisitByID(id).take(1).subscribe(new Subscriber<VisitsEntity>() {
            @Override
            public void onSubscribe(Subscription s) {

            }

            @Override
            public void onNext(VisitsEntity visitsEntity) {
                Assert.assertThat(visitsEntity.getVisitType(), is("visitType"));
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
    public void closeDb(){
        database.close();
    }

    private void createEntity(VisitsEntity visitsEntity) {
        visitsEntity.setPatientKeyID(1);
        visitsEntity.setStartDate("startDate");
        visitsEntity.setStopDate("stopDate");
        visitsEntity.setVisitPlace("visitPlace");
        visitsEntity.setVisitType("visitType");
        visitsEntity.setUuid("uuid");
    }

}