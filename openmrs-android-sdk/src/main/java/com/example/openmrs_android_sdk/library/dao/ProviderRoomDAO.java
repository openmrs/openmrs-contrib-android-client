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

package com.example.openmrs_android_sdk.library.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.TypeConverters;
import androidx.room.Update;

import com.example.openmrs_android_sdk.library.models.Person;
import com.example.openmrs_android_sdk.library.models.Provider;
import com.example.openmrs_android_sdk.library.models.typeConverters.PersonConverter;

import java.util.List;

import io.reactivex.Single;

@Dao
public interface ProviderRoomDAO {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    long addProvider(Provider provider);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAllOrders(List<Provider> order);

    @Delete
    void deleteProvider(Provider provider);

    @Query("DELETE FROM provider_table WHERE uuid = :uuid")
    void deleteByUuid(String uuid);

    @Update
    void updateProvider(Provider provider);

    /**
     *  since the current implementation of the provider update operation
     *  only changes display and person field in the provider entity
     *
     */

    @TypeConverters(PersonConverter.class)
    @Query("UPDATE provider_table SET person= :person, _id= :id, display=:display, identifier=:identifier WHERE uuid = :uuid")
    void updateProviderByUuid(String display, long id, Person person, String uuid, String identifier);

    @Query("UPDATE provider_table SET uuid=:uuid WHERE _id=:id")
    void updateProviderUuidById(long id, String uuid);

    @Query("SELECT * FROM provider_table")
    Single<List<Provider>> getProviderList();

    @Query("SELECT * FROM provider_table WHERE _id = :id")
    Single<Provider> findProviderByID(long id);

    @Query("SELECT * FROM provider_table WHERE uuid = :uuid")
    Single<Provider> findProviderByUUID(String uuid);

    @Query("SELECT uuid FROM provider_table")
    Single<List<String>> getCurrentUUIDs();

    @Query("DELETE FROM provider_table")
    void deleteAll();
}
