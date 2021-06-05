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

package com.example.openmrs_android_sdk.library.databases.entities;

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.TypeConverters
import com.example.openmrs_android_sdk.library.models.Resource
import com.example.openmrs_android_sdk.library.models.typeConverters.FormResourceConverter
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Entity(tableName = "forms")
class FormResourceEntity : Resource() {

    @ColumnInfo(name = "name")
    @SerializedName("name")
    @Expose
    var name: String? = null

    @TypeConverters(FormResourceConverter::class)
    @ColumnInfo(name = "resources")
    @SerializedName("resources")
    @Expose
    var resources: List<FormResourceEntity> = ArrayList()

    @ColumnInfo(name = "valueReference")
    @SerializedName("valueReference")
    @Expose
    var valueReference: String? = null
}
