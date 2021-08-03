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

import com.openmrs.android_sdk.library.models.EncounterType;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.openmrs.android_sdk.library.databases.AppDatabase;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class EncounterTypeRoomDAOTest {
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private EncounterType expectedEncounterType1 = newEncounterType(10L, "123", EncounterType.VITALS);
    private EncounterType expectedEncounterType2 = newEncounterType(20L, "124", EncounterType.VISIT_NOTE);

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
    public void deleteEncounterType_shouldDeleteEncounterType() {
        mDatabase.encounterTypeRoomDAO().addEncounterType(expectedEncounterType1);
        mDatabase.encounterTypeRoomDAO().addEncounterType(expectedEncounterType2);
        Assert.assertEquals(mDatabase.encounterTypeRoomDAO().getAllEncounterTypes().size(), 2);
        mDatabase.encounterTypeRoomDAO().deleteAllEncounterTypes();
        Assert.assertEquals(mDatabase.encounterTypeRoomDAO().getAllEncounterTypes().size(), 0);
    }

    @Test
    public void getEncounterTypeByFormName_shouldGetEncounterTypeByFormName() {
        mDatabase.encounterTypeRoomDAO().addEncounterType(expectedEncounterType1);
        EncounterType actualEncounterType = mDatabase.encounterTypeRoomDAO().getEncounterTypeByFormName(expectedEncounterType1.getDisplay());
        Assert.assertEquals(actualEncounterType.getUuid(), expectedEncounterType1.getUuid());
        Assert.assertEquals(actualEncounterType.getId(), expectedEncounterType1.getId());
        Assert.assertEquals(actualEncounterType.getDisplay(), expectedEncounterType1.getDisplay());
    }

    private EncounterType newEncounterType(long id, String display, String encType) {
        EncounterType encounterType = new EncounterType(encType);
        encounterType.setId(id);
        encounterType.setUuid(display);
        return encounterType;
    }
}

