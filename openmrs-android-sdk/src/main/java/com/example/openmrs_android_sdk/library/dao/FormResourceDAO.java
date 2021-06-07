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
import androidx.room.Insert;
import androidx.room.Query;

import com.example.openmrs_android_sdk.library.databases.entities.FormResourceEntity;

import java.util.List;

import io.reactivex.Single;

@Dao
public interface FormResourceDAO {
    @Query("SELECT * FROM forms WHERE name = :name")
    Single<FormResourceEntity> getFormResourceByName(String name);

    @Query("SELECT * FROM forms")
    Single<List<FormResourceEntity>> getFormResourceList();

    @Query("SELECT * FROM forms WHERE uuid = :uuid")
    Single<FormResourceEntity> getFormByUuid(String uuid);

    @Query("DELETE FROM forms")
    void deleteAllForms();

    @Insert
    void addFormResource(FormResourceEntity formResourceEntity);
}
