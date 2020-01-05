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

@RunWith(AndroidJUnit4.class)
@LargeTest
public class LocationRoomDAOTest {
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private AppDatabase mDatabase;

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
        LocationEntity actualLocationEntity = createEntity(10L);

        mDatabase.locationRoomDAO().saveLocation(actualLocationEntity);
        mDatabase.locationRoomDAO().getLocations().subscribe(new SingleObserver<List<LocationEntity>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onSuccess(List<LocationEntity> locationEntities) {
                Assert.assertEquals(locationEntities.get(0).getName(), "name");
            }

            @Override
            public void onError(Throwable e) {

            }
        });
    }

    @Test
    public void saveLocation_VerifySavedLocationByName() {
        LocationEntity actualLocationEntity = createEntity(10L);

        mDatabase.locationRoomDAO().saveLocation(actualLocationEntity);
        mDatabase.locationRoomDAO().findLocationByName("name").subscribe(new SingleObserver<LocationEntity>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onSuccess(LocationEntity locationEntity) {
                Assert.assertEquals(actualLocationEntity, actualLocationEntity);
            }

            @Override
            public void onError(Throwable e) {

            }
        });
    }

    @Test
    public void saveLocation_VerifyDeleteAllLoaction() {
        LocationEntity actualLocationEntity1 = createEntity(10L);
        LocationEntity actualLocationEntity2 = createEntity(20L);

        mDatabase.locationRoomDAO().saveLocation(actualLocationEntity1);
        mDatabase.locationRoomDAO().saveLocation(actualLocationEntity2);

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

            }
        });
    }

    private LocationEntity createEntity(Long id) {
        LocationEntity entity = new LocationEntity();
        entity.setId(id);
        entity.setDisplay("location");
        entity.setUuid("uuid");
        entity.setName("name");
        entity.setDescription("description");
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
