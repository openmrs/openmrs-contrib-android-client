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

import java.util.List;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.room.Room;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;
import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openmrs.mobile.databases.AppDatabase;
import org.openmrs.mobile.databases.entities.LocationEntity;
import org.openmrs.mobile.utilities.ActiveAndroid.util.Log;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class LocationRoomDAOTest {
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private AppDatabase mDatabase;
    private String TAG = "LocationRoomDAOTest";
    private LocationEntity expectedLocationEntity = createDemoLocationEntity(10L, "name", "description", "display");
    private LocationEntity expectedLocationEntity2 = createDemoLocationEntity(20L, "name2", "description2", "display2");

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
        mDatabase.locationRoomDAO().saveLocation(expectedLocationEntity);
        mDatabase.locationRoomDAO().getLocations().subscribe(new SingleObserver<List<LocationEntity>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onSuccess(List<LocationEntity> locationEntities) {
                Assert.assertEquals(locationEntities.get(0).getName(), "name");
                Assert.assertEquals(locationEntities.get(0).getDescription(), "description");
                Assert.assertEquals(locationEntities.get(0).getDisplay(), "display");
            }

            @Override
            public void onError(Throwable e) {
                Log.i(TAG, e.getMessage());
            }
        });
    }

    @Test
    public void findLocationByName_ShouldFindCorrectLocationByName() {
        mDatabase.locationRoomDAO().saveLocation(expectedLocationEntity);
        mDatabase.locationRoomDAO().findLocationByName("name").subscribe(new SingleObserver<LocationEntity>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onSuccess(LocationEntity locationEntity) {
                Assert.assertEquals(expectedLocationEntity, locationEntity);
            }

            @Override
            public void onError(Throwable e) {
                Log.i(TAG, e.getMessage());
            }
        });
    }

    @Test
    public void deleteAllLocations_ShouldDeleteAllSavedLoactions() {
        mDatabase.locationRoomDAO().saveLocation(expectedLocationEntity);
        mDatabase.locationRoomDAO().saveLocation(expectedLocationEntity2);

        mDatabase.locationRoomDAO().deleteAllLocations();

        mDatabase.locationRoomDAO().getLocations().subscribe(new SingleObserver<List<LocationEntity>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onSuccess(List<LocationEntity> locationEntities) {
                Assert.assertEquals(locationEntities.size(), 0);
            }

            @Override
            public void onError(Throwable e) {
                Log.i(TAG, e.getMessage());
            }
        });
    }

    private LocationEntity createDemoLocationEntity(Long id, String name, String description, String display) {
        LocationEntity entity = new LocationEntity();
        entity.setId(id);
        entity.setDisplay(display);
        entity.setUuid("uuid");
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
