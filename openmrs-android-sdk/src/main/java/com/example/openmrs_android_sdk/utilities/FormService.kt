/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package com.example.openmrs_android_sdk.utilities

import com.example.openmrs_android_sdk.library.OpenmrsAndroid
import com.example.openmrs_android_sdk.library.databases.AppDatabase
import com.example.openmrs_android_sdk.library.databases.entities.FormResourceEntity
import com.example.openmrs_android_sdk.library.models.Form
import com.example.openmrs_android_sdk.utilities.StringUtils.isBlank
import com.example.openmrs_android_sdk.utilities.StringUtils.unescapeJavaString
import com.google.gson.GsonBuilder
import java.lang.reflect.Modifier

object FormService {

    @JvmStatic
    fun getForm(valueReference: String?): Form {
        val unescapedValueReference = unescapeJavaString(valueReference!!)
        val builder = GsonBuilder()
        builder.excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC)
        builder.excludeFieldsWithoutExposeAnnotation()
        val gson = builder.create()
        return gson.fromJson(unescapedValueReference, Form::class.java)
    }

    @JvmStatic
    fun getFormByUuid(uuid: String?): Form? {
        if (!isBlank(uuid)) {
            var formResourceEntity : FormResourceEntity?
            try {
                formResourceEntity = AppDatabase.getDatabase(OpenmrsAndroid.getInstance()?.getApplicationContext())
                        .formResourceDAO()
                        .getFormByUuid(uuid)
                        .blockingGet()
            } catch (e: Exception) {
                formResourceEntity = null;
            }
            if (formResourceEntity != null) {
                val resourceList = formResourceEntity.resources
                for (resource in resourceList) {
                    if ("json" == resource.name) {
                        val valueRefString = resource.valueReference
                        val form = getForm(valueRefString)
                        form.valueReference = valueRefString
                        form.name = formResourceEntity.name
                        return form
                    }
                }
            }
        }
        return null
    }

    @JvmStatic
    fun getFormResourceByName(name: String?): FormResourceEntity {
        return AppDatabase.getDatabase(OpenmrsAndroid.getInstance()?.getApplicationContext())
                .formResourceDAO()
                .getFormResourceByName(name)
                .blockingGet()
    }

    @JvmStatic
    fun getFormResourceList(): List<FormResourceEntity> {
        var list : List<FormResourceEntity>  = AppDatabase.getDatabase(OpenmrsAndroid.getInstance()?.getApplicationContext())
                .formResourceDAO()
                .formResourceList
                .blockingGet()
        return list
    }
}