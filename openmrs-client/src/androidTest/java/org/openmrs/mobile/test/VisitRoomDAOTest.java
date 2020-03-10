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

import android.util.Log;
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
import org.openmrs.mobile.databases.entities.VisitEntity;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class VisitRoomDAOTest {
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private AppDatabase database;
    private final String TAG = "visitEntityTest";
    private VisitEntity actualVisitEntity = createVisitEntity(1L, "startDate", "stopDate", "visitPlace", "visitType", "uuid");
    private VisitEntity updatedVisitEntity = createVisitEntity(2L, "updatedStartDate", "updatedStopDate", "updatedVisitPlace", "updatedVisitType", "updatedUuid");

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
    public void updateVisit_ShouldUpdateVisit() {
        Long id = database.visitRoomDAO().saveVisit(actualVisitEntity);
        database.visitRoomDAO().updateVisit(updatedVisitEntity);
        database.visitRoomDAO().getVisitByID(id).take(1).subscribe(new Subscriber<VisitEntity>() {
            @Override
            public void onSubscribe(Subscription s) {

            }

            @Override
            public void onNext(VisitEntity visitEntity) {
                Assert.assertEquals(visitEntity.getPatientKeyID(), 2L);
                Assert.assertEquals(visitEntity.getStartDate(), "updatedStartDate");
                Assert.assertEquals(visitEntity.getStopDate(), "updatedStopDate");
                Assert.assertEquals(visitEntity.getVisitPlace(), "updatedVisitPlace");
                Assert.assertEquals(visitEntity.getVisitType(), "updatedVisitType");
                Assert.assertEquals(visitEntity.getUuid(), "updatedUuid");
            }

            @Override
            public void onError(Throwable t) {
                Log.i(TAG,t.getMessage());

            }

            @Override
            public void onComplete() {

            }
        });
    }

    @Test
    public void deleteVisitsByPatientId_ShouldDeleteVisit() {
        database.visitRoomDAO().saveVisit(actualVisitEntity);
        database.visitRoomDAO().deleteVisitsByPatientId(actualVisitEntity);
        database.visitRoomDAO().getActiveVisits().take(1).subscribe(new Subscriber<List<VisitEntity>>() {
            @Override
            public void onSubscribe(Subscription s) {

            }

            @Override
            public void onNext(List<VisitEntity> visitsEntities) {
                Assert.assertEquals(visitsEntities.size(), 0);
            }

            @Override
            public void onError(Throwable t) {
                Log.i(TAG,t.getMessage());
            }

            @Override
            public void onComplete() {

            }
        });
    }

    @Test
    public void getVisitsByPatientId_ShouldGetVisit() {
        database.visitRoomDAO().saveVisit(actualVisitEntity);
        database.visitRoomDAO().getVisitsByPatientID(actualVisitEntity.getPatientKeyID());
        database.visitRoomDAO().getActiveVisits().take(1).subscribe(new Subscriber<List<VisitEntity>>() {
            @Override
            public void onSubscribe(Subscription s) {

            }

            @Override
            public void onNext(List<VisitEntity> visitsEntities) {
                VisitEntity visitEntity = visitsEntities.get(0);
                Assert.assertEquals(visitEntity.getPatientKeyID(), 1L);
                Assert.assertEquals(visitEntity.getStartDate(), "startDate");
                Assert.assertEquals(visitEntity.getStopDate(), "stopDate");
                Assert.assertEquals(visitEntity.getVisitPlace(), "visitPlace");
                Assert.assertEquals(visitEntity.getVisitType(), "visitType");
                Assert.assertEquals(visitEntity.getUuid(), "uuid");
            }

            @Override
            public void onError(Throwable t) {
                Log.i(TAG,t.getMessage());
            }

            @Override
            public void onComplete() {

            }
        });
    }

    @Test
    public void getFirstActiveVisitByPatientId_ShouldGetFirstActiveVisit() {
        database.visitRoomDAO().saveVisit(actualVisitEntity);
        database.visitRoomDAO().getFirstActiveVisitByPatientId(actualVisitEntity.getPatientKeyID()).subscribe(new Subscriber<VisitEntity>() {
            @Override
            public void onSubscribe(Subscription s) {

            }

            @Override
            public void onNext(VisitEntity visitEntity) {
                Assert.assertEquals(visitEntity.getPatientKeyID(), 1L);
                Assert.assertEquals(visitEntity.getStartDate(), "startDate");
                Assert.assertEquals(visitEntity.getStopDate(), "stopDate");
                Assert.assertEquals(visitEntity.getVisitPlace(), "visitPlace");
                Assert.assertEquals(visitEntity.getVisitType(), "visitType");
                Assert.assertEquals(visitEntity.getUuid(), "uuid");
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
    public void saveVisit_ShouldSaveCorrectVisit() {
        Long id  = database.visitRoomDAO().saveVisit(actualVisitEntity);
        database.visitRoomDAO().getVisitByID(id).take(1).subscribe(new Subscriber<VisitEntity>() {
            @Override
            public void onSubscribe(Subscription s) {

            }

            @Override
            public void onNext(VisitEntity visitEntity) {
                Assert.assertEquals(visitEntity.getPatientKeyID(), 1L);
                Assert.assertEquals(visitEntity.getStartDate(), "startDate");
                Assert.assertEquals(visitEntity.getStopDate(), "stopDate");
                Assert.assertEquals(visitEntity.getVisitPlace(), "visitPlace");
                Assert.assertEquals(visitEntity.getVisitType(), "visitType");
                Assert.assertEquals(visitEntity.getUuid(), "uuid");
            }

            @Override
            public void onError(Throwable t) {
                Log.i(TAG,t.getMessage());
            }

            @Override
            public void onComplete() {

            }
        });
    }

    @Test
    public void getVisitByUuid_ShouldGetCorrectVisit() {
        database.visitRoomDAO().saveVisit(actualVisitEntity);
        database.visitRoomDAO().getVisitByUuid("uuid").subscribe(new Subscriber<VisitEntity>() {
            @Override
            public void onSubscribe(Subscription s) {

            }

            @Override
            public void onNext(VisitEntity visitEntity) {
                Assert.assertEquals(visitEntity.getPatientKeyID(), 1L);
                Assert.assertEquals(visitEntity.getStartDate(), "startDate");
                Assert.assertEquals(visitEntity.getStopDate(), "stopDate");
                Assert.assertEquals(visitEntity.getVisitPlace(), "visitPlace");
                Assert.assertEquals(visitEntity.getVisitType(), "visitType");
                Assert.assertEquals(visitEntity.getUuid(), "uuid");
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
    public void getVisitIdByUuid_ShouldGetCorrectVisitId() {
        Long id = database.visitRoomDAO().saveVisit(actualVisitEntity);
        Long visitId = database.visitRoomDAO().getVisitsIDByUUID("uuid");
        Assert.assertEquals(id, visitId);
    }

    @After
    public void closeDb(){
        database.close();
    }

    private VisitEntity createVisitEntity(long id, String startDate, String stopDate, String visitPlace, String visitType, String uuid) {
        VisitEntity visitEntity = new VisitEntity();
        visitEntity.setPatientKeyID(id);
        visitEntity.setStartDate(startDate);
        visitEntity.setStopDate(stopDate);
        visitEntity.setVisitPlace(visitPlace);
        visitEntity.setVisitType(visitType);
        visitEntity.setUuid(uuid);
        return visitEntity;
    }
}
