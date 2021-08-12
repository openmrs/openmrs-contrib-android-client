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

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.openmrs.android_sdk.library.databases.AppDatabase;
import com.openmrs.android_sdk.library.databases.entities.VisitEntity;

import java.util.Objects;

@RunWith(AndroidJUnit4.class)
public class VisitRoomDAOTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();
    private AppDatabase database;
    private VisitEntity expectedVisitEntity = createVisitEntity(10L, 1L, "startDate", "stopDate", "visitPlace", "visitType", "uuid");
    private VisitEntity expectedActiveVisitEntity = createVisitEntity(20L, 5L, "startDate", null, "visitPlace", "visitType", "uuid");
    private VisitEntity updatedVisitEntity = createVisitEntity(10L, 2L, "updatedStartDate", "updatedStopDate", "updatedVisitPlace", "updatedVisitType", "updatedUuid");

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
        Long id = database.visitRoomDAO().addVisit(expectedVisitEntity);
        database.visitRoomDAO().updateVisit(updatedVisitEntity);
        database.visitRoomDAO().getVisitByID(id).
                test()
                .assertValue(actualVisitEntity -> Objects.equals(renderVisitEntityString(actualVisitEntity), renderVisitEntityString(updatedVisitEntity)));
    }

    @Test
    public void deleteVisitsByPatientId_ShouldDeleteVisit() {
        database.visitRoomDAO().addVisit(expectedVisitEntity);
        database.visitRoomDAO().deleteVisitsByPatientId(expectedVisitEntity.getPatientKeyID());
        database.visitRoomDAO().getActiveVisits()
                .test()
                .assertValue(actualVisitEntities -> Objects.equals(actualVisitEntities.size(), 0));
    }

    @Test
    public void getVisitsByPatientId_ShouldGetVisit() {
        database.visitRoomDAO().addVisit(expectedVisitEntity);
        database.visitRoomDAO().getVisitsByPatientID(expectedVisitEntity.getPatientKeyID())
                .test()
                .assertValue(visitEntities -> {
                    VisitEntity actualVisitEntity = visitEntities.get(0);
                    return Objects.equals(renderVisitEntityString(actualVisitEntity), renderVisitEntityString(expectedVisitEntity));
                });
    }

    @Test
    public void getFirstActiveVisitByPatientId_ShouldGetFirstActiveVisit() {
        database.visitRoomDAO().addVisit(expectedActiveVisitEntity);
        database.visitRoomDAO().getActiveVisitByPatientId(expectedActiveVisitEntity.getPatientKeyID())
                .test()
                .assertValue(actualVisitEntity -> Objects.equals(renderVisitEntityString(actualVisitEntity), renderVisitEntityString(expectedActiveVisitEntity)));
    }

    @Test
    public void saveVisit_ShouldSaveCorrectVisit() {
        Long id = database.visitRoomDAO().addVisit(expectedVisitEntity);
        database.visitRoomDAO().getVisitByID(id)
                .test()
                .assertValue(actualVisitEntity -> Objects.equals(renderVisitEntityString(actualVisitEntity), renderVisitEntityString(expectedVisitEntity)));
    }

    @Test
    public void getVisitByUuid_ShouldGetCorrectVisit() {
        database.visitRoomDAO().addVisit(expectedVisitEntity);
        database.visitRoomDAO().getVisitByUuid("uuid")
                .test()
                .assertValue(actualVisitEntity -> Objects.equals(renderVisitEntityString(actualVisitEntity), renderVisitEntityString(expectedVisitEntity)));
    }

    @Test
    public void getVisitIdByUuid_ShouldGetCorrectVisitId() {
        Long id = database.visitRoomDAO().addVisit(expectedVisitEntity);
        Long visitId = database.visitRoomDAO().getVisitsIDByUUID("uuid");
        Assert.assertEquals(id, visitId);
    }

    @After
    public void closeDb() {
        database.close();
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

    private String renderVisitEntityString(VisitEntity visitEntity) {
        return visitEntity.getId() + visitEntity.getPatientKeyID() + visitEntity.getStartDate()
                + visitEntity.getStopDate() + visitEntity.getVisitPlace()
                + visitEntity.getVisitType() + visitEntity.getUuid();
    }
}
