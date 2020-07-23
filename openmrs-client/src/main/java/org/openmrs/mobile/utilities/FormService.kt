/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.mobile.utilities

import com.google.gson.GsonBuilder
import org.openmrs.mobile.application.OpenMRS
import org.openmrs.mobile.databases.AppDatabase
import org.openmrs.mobile.databases.entities.FormResourceEntity
import org.openmrs.mobile.models.Form
import org.openmrs.mobile.utilities.StringUtils.isBlank
import org.openmrs.mobile.utilities.StringUtils.unescapeJavaString
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
                formResourceEntity = AppDatabase.getDatabase(OpenMRS.getInstance().applicationContext)
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
        return AppDatabase.getDatabase(OpenMRS.getInstance().applicationContext)
                .formResourceDAO()
                .getFormResourceByName(name)
                .blockingGet()
    }

    @JvmStatic
    fun getFormResourceList(): List<FormResourceEntity> {
        var list : List<FormResourceEntity>  = AppDatabase.getDatabase(OpenMRS.getInstance().applicationContext)
                .formResourceDAO()
                .formResourceList
                .blockingGet()
        return list
    }
}