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

package com.openmrs.android_sdk.library.dao;

import com.openmrs.android_sdk.library.OpenmrsAndroid;
import com.openmrs.android_sdk.library.databases.AppDatabase;
import com.openmrs.android_sdk.library.databases.AppDatabaseHelper;
import com.openmrs.android_sdk.library.databases.entities.LocationEntity;
import com.openmrs.android_sdk.utilities.StringUtils;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;


/**
 * The type Location dao.
 */
public class LocationDAO {
    /**
     * The Location room dao.
     */
    LocationRoomDAO locationRoomDAO = AppDatabase.getDatabase(OpenmrsAndroid.getInstance().getApplicationContext()).locationRoomDAO();

    /**
     * Save location observable.
     *
     * @param location the location
     * @return the observable
     */
    public Observable<Long> saveLocation(LocationEntity location) {
        return AppDatabaseHelper.createObservableIO(() -> locationRoomDAO.addLocation(location));
    }

    /**
     * Delete all locations.
     */
    public void deleteAllLocations() {
        locationRoomDAO.deleteAllLocations();
    }

    /**
     * Gets locations.
     *
     * @return the locations
     */
    public Observable<List<LocationEntity>> getLocations() {
        return AppDatabaseHelper.createObservableIO(() -> {
            try {
                return locationRoomDAO.getLocations().blockingGet();
            } catch (Exception e) {
                return new ArrayList<>();
            }
        });
    }

    /**
     * Find location by name location entity.
     *
     * @param name the name
     * @return the location entity
     */
    public LocationEntity findLocationByName(String name) {
        if (!StringUtils.notNull(name)) {
            return null;
        }
        try {
            return locationRoomDAO.findLocationByName(name).blockingGet();
        } catch (Exception e) {
            return new LocationEntity(name);
        }
    }

    /**
     * Find location by uuid location entity.
     *
     * @param uuid the uuid
     * @return the location entity
     */
    public LocationEntity findLocationByUUID(String uuid) {
        if (!StringUtils.notNull(uuid)) {
            return null;
        }
        try {
            return locationRoomDAO.findLocationByUUID(uuid).blockingGet();
        } catch (Exception e) {
            return new LocationEntity("");
        }
    }

}