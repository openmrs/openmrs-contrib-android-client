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
package com.example.openmrs_android_sdk.library.models.typeConverters

import androidx.room.TypeConverter
import com.example.openmrs_android_sdk.library.models.PersonAddress
import com.google.gson.GsonBuilder
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.Serializable
import java.lang.reflect.Modifier

object PersonAddressConverter : Serializable {
    @TypeConverter
    fun fromString(value: String?): List<PersonAddress> {
        val listType = object : TypeToken<List<PersonAddress?>?>() {}.type
        val builder = GsonBuilder()
        builder.excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
        val gson = builder.create()
        return gson.fromJson(value, listType)
    }

    @TypeConverter
    fun listToString(list: List<PersonAddress?>?): String {
        val builder = GsonBuilder()
        builder.excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
        val gson = builder.create()
        return gson.toJson(list)
    }
}