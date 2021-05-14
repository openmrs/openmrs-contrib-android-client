/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.mobile.api

import android.app.IntentService
import android.content.Intent
import org.openmrs.mobile.application.OpenMRS
import org.openmrs.mobile.databases.AppDatabase
import org.openmrs.mobile.databases.entities.FormResourceEntity
import org.openmrs.mobile.models.EncounterType
import org.openmrs.mobile.models.Results
import org.openmrs.mobile.utilities.NetworkUtils.isOnline
import org.openmrs.mobile.utilities.ToastUtil.error
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FormListService : IntentService("Sync Form List") {
    private val apiService = RestServiceBuilder.createService(RestApi::class.java)
    private var formresourcelist: List<FormResourceEntity?>? = null
    override fun onHandleIntent(intent: Intent?) {
        val formResourceDAO = AppDatabase.getDatabase(OpenMRS.getInstance().applicationContext).formResourceDAO()
        val encounterTypeRoomDAO = AppDatabase.getDatabase(OpenMRS.getInstance().applicationContext).encounterTypeRoomDAO()
        if (isOnline()) {
            val call = apiService.forms
            call.enqueue(object : Callback<Results<FormResourceEntity?>> {
                override fun onResponse(call: Call<Results<FormResourceEntity?>>, response: Response<Results<FormResourceEntity?>>) {
                    if (response.isSuccessful) {
                        formResourceDAO.deleteAllForms()
                        formresourcelist = response.body()!!.results
                        val size = formresourcelist!!.size
                        for (i in 0 until size) {
                            formResourceDAO.addFormResource(formresourcelist!![i])
                        }
                    }
                }

                override fun onFailure(call: Call<Results<FormResourceEntity?>>, t: Throwable) {
                    error(t.message!!)
                }
            })
            val call2 = apiService.encounterTypes
            call2.enqueue(object : Callback<Results<EncounterType?>> {
                override fun onResponse(call: Call<Results<EncounterType?>>, response: Response<Results<EncounterType?>>) {
                    if (response.isSuccessful) {
                        encounterTypeRoomDAO.deleteAllEncounterTypes()
                        val encounterTypeList = response.body()!!
                        for (encounterType in encounterTypeList.results) encounterTypeRoomDAO.addEncounterType(encounterType)
                    }
                }

                override fun onFailure(call: Call<Results<EncounterType?>>, t: Throwable) {
                    error(t.message!!)
                }
            })
        }
    }
}