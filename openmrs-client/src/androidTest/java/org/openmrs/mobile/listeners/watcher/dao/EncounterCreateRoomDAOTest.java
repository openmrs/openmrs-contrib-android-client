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

import com.openmrs.android_sdk.library.models.Encountercreate;
import com.openmrs.android_sdk.library.models.Obscreate;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.openmrs.android_sdk.library.dao.EncounterCreateRoomDAO;
import com.openmrs.android_sdk.library.databases.AppDatabase;

import java.util.ArrayList;
import java.util.List;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class EncounterCreateRoomDAOTest {
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private Encountercreate expectedEncounterCreate1 = newEncountercreate(10L, "visit_uuid1", "patient_uuid1", 101L, "vitals", "vitals", true, new ArrayList<>());
    private Encountercreate expectedEncounterCreate2 = newEncountercreate(20L, "visit_uuid1", "patient_uuid1", 101L, "vitals", "vitals", true, new ArrayList<>());
    private Encountercreate updatedEncounterCreate = newEncountercreate(10L, "visit_uuid2", "patient_uuid2", 102L, "admission", "admission", true, new ArrayList<>());

    private EncounterCreateRoomDAO encounterCreateRoomDAO;
    private AppDatabase mDatabase;

    @Before
    public void initDb() {
        mDatabase = Room.inMemoryDatabaseBuilder(
                InstrumentationRegistry.getInstrumentation().getTargetContext(),
                AppDatabase.class)
                .allowMainThreadQueries()
                .build();
        encounterCreateRoomDAO = mDatabase.encounterCreateRoomDAO();
    }

    @After
    public void closeDb() {
        mDatabase.close();
    }

    @Test
    public void getAllCreatedEncounters_ShouldGetAllCreatedEncounters() {
        encounterCreateRoomDAO.addEncounterCreated(expectedEncounterCreate1);
        encounterCreateRoomDAO.addEncounterCreated(expectedEncounterCreate2);
        List<Encountercreate> list = encounterCreateRoomDAO.getAllCreatedEncounters();
        Assert.assertEquals(list.size(), 2);
        Assert.assertEquals(renderEncounterCreateString(list.get(0)), renderEncounterCreateString(expectedEncounterCreate1));
        Assert.assertEquals(renderEncounterCreateString(list.get(1)), renderEncounterCreateString(expectedEncounterCreate2));
    }

    @Test
    public void getCreatedEncountersByID_ShouldGetCreatedEncountersByID() {
        encounterCreateRoomDAO.addEncounterCreated(expectedEncounterCreate1);
        encounterCreateRoomDAO.addEncounterCreated(expectedEncounterCreate2);
        Encountercreate encountercreate = encounterCreateRoomDAO.getCreatedEncountersByID(expectedEncounterCreate1.getId());
        Assert.assertEquals(renderEncounterCreateString(encountercreate), renderEncounterCreateString(expectedEncounterCreate1));
    }

    @Test
    public void updateExistingEncounter_ShouldUpdateExistingEncounter() {
        encounterCreateRoomDAO.addEncounterCreated(expectedEncounterCreate1);
        encounterCreateRoomDAO.updateExistingEncounter(updatedEncounterCreate);
        List<Encountercreate> list = encounterCreateRoomDAO.getAllCreatedEncounters();
        Assert.assertEquals(list.size(), 1);
        Assert.assertEquals(renderEncounterCreateString(list.get(0)), renderEncounterCreateString(updatedEncounterCreate));
    }

    private Encountercreate newEncountercreate(long id, String visit_uuid, String patient_uuid, long patientID, String encType, String formName, boolean synced, List<Obscreate> objects) {
        Encountercreate encountercreate = new Encountercreate();
        encountercreate.setId(id);
        encountercreate.setVisit(visit_uuid);
        encountercreate.setPatient(patient_uuid);
        encountercreate.setPatientId(patientID);
        encountercreate.setEncounterType(encType);
        encountercreate.setFormname(formName);
        encountercreate.setSynced(synced);
        encountercreate.setObservations(objects);
        return encountercreate;
    }

    private String renderEncounterCreateString(Encountercreate encountercreate) {
        return encountercreate.getId() + encountercreate.getVisit() + encountercreate.getPatient()
                + encountercreate.getPatientId() + encountercreate.getEncounterType()
                + encountercreate.getFormname() + encountercreate.getSynced();
    }
}
