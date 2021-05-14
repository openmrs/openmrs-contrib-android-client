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
import org.openmrs.mobile.R
import org.openmrs.mobile.api.repository.VisitRepository
import org.openmrs.mobile.application.OpenMRS
import org.openmrs.mobile.dao.PatientDAO
import org.openmrs.mobile.dao.VisitDAO
import org.openmrs.mobile.databases.AppDatabase
import org.openmrs.mobile.listeners.retrofitcallbacks.DefaultResponseCallback
import org.openmrs.mobile.listeners.retrofitcallbacks.StartVisitResponseCallback
import org.openmrs.mobile.models.Encounter
import org.openmrs.mobile.models.EncounterType
import org.openmrs.mobile.models.Encountercreate
import org.openmrs.mobile.models.Visit
import org.openmrs.mobile.utilities.NetworkUtils.isOnline
import org.openmrs.mobile.utilities.ToastUtil.error
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import rx.android.schedulers.AndroidSchedulers

class EncounterService : IntentService("Save Encounter") {
    private val apiService = RestServiceBuilder.createService(RestApi::class.java)
    @JvmOverloads
    fun addEncounter(encountercreate: Encountercreate, callbackListener: DefaultResponseCallback? = null) {
        if (isOnline()) {
            VisitDAO().getActiveVisitByPatientId(encountercreate.patientId)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { visit: Visit? ->
                        if (visit != null) {
                            encountercreate.visit = visit.uuid
                            if (callbackListener != null) {
                                syncEncounter(encountercreate, callbackListener)
                            } else {
                                syncEncounter(encountercreate)
                            }
                        } else {
                            startNewVisitForEncounter(encountercreate)
                        }
                    }
        } else {
            error(getString(R.string.form_data_will_be_synced_later_error_message))
        }
    }

    private fun startNewVisitForEncounter(encountercreate: Encountercreate, callbackListener: DefaultResponseCallback?) {
        VisitRepository().startVisit(PatientDAO().findPatientByUUID(encountercreate.patient),
                object : StartVisitResponseCallback {
                    override fun onStartVisitResponse(id: Long) {
                        VisitDAO().getVisitByID(id)
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe { visit: Visit ->
                                    encountercreate.visit = visit.uuid
                                    if (callbackListener != null) {
                                        syncEncounter(encountercreate, callbackListener)
                                    } else {
                                        syncEncounter(encountercreate)
                                    }
                                }
                    }

                    override fun onResponse() {
                        // This method is intentionally empty
                    }

                    override fun onErrorResponse(errorMessage: String) {
                        error(errorMessage)
                    }
                })
    }

    fun startNewVisitForEncounter(encountercreate: Encountercreate) {
        startNewVisitForEncounter(encountercreate, null)
    }

    @JvmOverloads
    fun syncEncounter(encountercreate: Encountercreate, callbackListener: DefaultResponseCallback? = null) {
        if (isOnline()) {
            val call = apiService.createEncounter(encountercreate)
            call.enqueue(object : Callback<Encounter?> {
                override fun onResponse(call: Call<Encounter?>, response: Response<Encounter?>) {
                    if (response.isSuccessful) {
                        val encounter = response.body()
                        linkvisit(encountercreate.patientId, encountercreate.formname, encounter, encountercreate)
                        encountercreate.synced = true
                        AppDatabase.getDatabase(OpenMRS.getInstance().applicationContext)
                                .encounterCreateRoomDAO()
                                .updateExistingEncounter(encountercreate)
                        VisitRepository().syncLastVitals(encountercreate.patient)
                        callbackListener?.onResponse()
                    } else {
                        callbackListener?.onErrorResponse(response.errorBody().toString())
                    }
                }

                override fun onFailure(call: Call<Encounter?>, t: Throwable) {
                    callbackListener?.onErrorResponse(t.localizedMessage)
                }
            })
        } else {
            error(getString(R.string.form_data_sync_is_off_message))
        }
    }

    private fun linkvisit(patientid: Long?, formname: String?, encounter: Encounter?, encountercreate: Encountercreate) {
        val visitDAO = VisitDAO()
        visitDAO.getVisitByUuid(encounter!!.visit!!.uuid)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { visit: Visit ->
                    encounter.encounterType = EncounterType(formname)
                    for (i in encountercreate.observations.indices) {
                        encounter.observations[i].displayValue = encountercreate.observations[i].value
                    }
                    val encounterList: MutableList<Encounter?> = visit.encounters as MutableList<Encounter?>
                    encounterList.add(encounter)
                    visitDAO.saveOrUpdate(visit, patientid!!)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe()
                }
    }

    override fun onHandleIntent(intent: Intent?) {
        if (isOnline()) {
            val encountercreatelist = AppDatabase.getDatabase(OpenMRS.getInstance().applicationContext)
                    .encounterCreateRoomDAO()
                    .allCreatedEncounters
            for (encountercreate in encountercreatelist) {
                if (!encountercreate.synced &&
                        PatientDAO().findPatientByID(java.lang.Long.toString(encountercreate.patientId!!)).isSynced) {
                    VisitDAO().getActiveVisitByPatientId(encountercreate.patientId)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe { visit: Visit? ->
                                if (visit != null) {
                                    encountercreate.visit = visit.uuid
                                    syncEncounter(encountercreate)
                                } else {
                                    startNewVisitForEncounter(encountercreate)
                                }
                            }
                }
            }
        } else {
            error(getString(R.string.form_data_will_be_synced_later_error_message))
        }
    }
}