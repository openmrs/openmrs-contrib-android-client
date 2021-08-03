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
import com.openmrs.android_sdk.library.databases.AppDatabase;
import com.openmrs.android_sdk.library.databases.entities.LocationEntity;
import com.openmrs.android_sdk.utilities.ApplicationConstants;

import java.util.Objects;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class LocationRoomDAOTest {
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private AppDatabase mDatabase;
    private LocationEntity expectedLocationEntity1 = createDemoLocationEntity(10L, "uuid1", "name", "description", "display");
    private LocationEntity expectedLocationEntity2 = createDemoLocationEntity(20L, "uuid2", "name2", "description2", "display2");

    @Before
    public void initDb() {
        mDatabase = Room.inMemoryDatabaseBuilder(
                InstrumentationRegistry.getInstrumentation().getTargetContext(),
                AppDatabase.class)
                // allowing main thread queries, just for testing
                .allowMainThreadQueries()
                .build();
    }

    @After
    public void closeDb() {
        mDatabase.close();
    }

    @Test
    public void saveLocation_ShouldSaveCorrectLocation() {
        mDatabase.locationRoomDAO().addLocation(expectedLocationEntity1);
        mDatabase.locationRoomDAO().getLocations()
                .test()
                .assertValue(locationEntities -> {
                    LocationEntity actualLocationEntity = locationEntities.get(0);
                    return Objects.equals(actualLocationEntity.getName(), "name")
                            && Objects.equals(actualLocationEntity.getDescription(), "description")
                            && Objects.equals(actualLocationEntity.getDisplay(), "display");
                });
    }

    @Test
    public void findLocationByName_ShouldFindCorrectLocationByName() {
        mDatabase.locationRoomDAO().addLocation(expectedLocationEntity1);
        mDatabase.locationRoomDAO().findLocationByName("display")
                .test()
                .assertValue(actualLocationEntity -> Objects.equals(actualLocationEntity.getName(), "name")
                        && Objects.equals(actualLocationEntity.getDescription(), "description")
                        && Objects.equals(actualLocationEntity.getDisplay(), "display"));
    }

    @Test
    public void findLocationByUUID_ShouldFindCorrectLocationByUUID() {
        mDatabase.locationRoomDAO().addLocation(expectedLocationEntity1);
        mDatabase.locationRoomDAO().findLocationByUUID(expectedLocationEntity1.getUuid())
                .test()
                .assertValue(actualLocationEntity -> Objects.equals(actualLocationEntity.getName(), "name")
                        && Objects.equals(actualLocationEntity.getDescription(), "description")
                        && Objects.equals(actualLocationEntity.getDisplay(), "display"));
    }

    @Test
    public void deleteAllLocations_ShouldDeleteAllSavedLoactions() {
        mDatabase.locationRoomDAO().addLocation(expectedLocationEntity1);
        mDatabase.locationRoomDAO().addLocation(expectedLocationEntity2);

        mDatabase.locationRoomDAO().deleteAllLocations();

        mDatabase.locationRoomDAO().getLocations()
                .test()
                .assertValue(locationEntities -> Objects.equals(locationEntities.size(), 0));
    }

    private LocationEntity createDemoLocationEntity(Long id, String uuid, String name, String description, String display) {
        LocationEntity entity = new LocationEntity(ApplicationConstants.EMPTY_STRING);
        entity.setId(id);
        entity.setDisplay(display);
        entity.setUuid(uuid);
        entity.setName(name);
        entity.setDescription(description);
        entity.setAddress_1("address 1");
        entity.setAddress_2("address2");
        entity.setCity("city");
        entity.setState("state");
        entity.setCountry("country");
        entity.setPostalCode("postal code");
        entity.setParentLocationuuid("location");
        return entity;
    }
}
