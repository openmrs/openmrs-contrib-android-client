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
package com.openmrs.android_sdk.library.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.openmrs.android_sdk.library.databases.entities.FormResourceEntity

/**
 * The interface Form resource dao.
 */
@Dao
interface FormResourceDAO {

    /**
     * Gets form resource by name.
     *
     * @param name the name
     * @return the form resource by name
     */
    @Query("SELECT * FROM forms WHERE name = :name")
    fun getFormResourceByName(name: String): FormResourceEntity

    /**
     * Gets form resource list.
     *
     * @return the form resource list
     */
    @Query("SELECT * FROM forms")
    fun getFormResourceList(): List<FormResourceEntity>

    /**
     * Gets form by uuid.
     *
     * @param uuid the uuid
     * @return the form by uuid
     */
    @Query("SELECT * FROM forms WHERE uuid = :uuid")
    fun getFormByUuid(uuid: String): FormResourceEntity

    /**
     * Delete all forms.
     */
    @Query("DELETE FROM forms")
    fun deleteAllForms()

    /**
     * Add form resource.
     *
     * @param formResourceEntity the form resource entity
     */
    @Insert
    fun addFormResource(formResourceEntity: FormResourceEntity)
}