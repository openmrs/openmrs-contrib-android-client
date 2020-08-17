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
package org.openmrs.mobile.activities.formadmission

import android.content.Context
import androidx.lifecycle.Observer
import org.openmrs.mobile.R
import org.openmrs.mobile.activities.BasePresenter
import org.openmrs.mobile.api.EncounterService
import org.openmrs.mobile.api.RestApi
import org.openmrs.mobile.api.RestServiceBuilder
import org.openmrs.mobile.api.repository.ProviderRepository
import org.openmrs.mobile.application.OpenMRS
import org.openmrs.mobile.dao.PatientDAO
import org.openmrs.mobile.databases.AppDatabase
import org.openmrs.mobile.databases.entities.LocationEntity
import org.openmrs.mobile.listeners.retrofitcallbacks.DefaultResponseCallback
import org.openmrs.mobile.models.Patient
import org.openmrs.mobile.models.Provider
import org.openmrs.mobile.models.Resource
import org.openmrs.mobile.models.Encountercreate
import org.openmrs.mobile.models.Obscreate
import org.openmrs.mobile.models.EncounterProviderCreate
import org.openmrs.mobile.utilities.FormService.getFormResourceByName
import org.openmrs.mobile.utilities.ToastUtil.error
import org.openmrs.mobile.utilities.ToastUtil.success

class FormAdmissionPresenter : BasePresenter, FormAdmissionContract.Presenter {
    private var view: FormAdmissionContract.View
    private var patientID: Long? = null
    private var encounterType: String? = null
    private var formName: String? = null
    private var formUUID: String? = null
    private var mPatient: Patient? = null
    private var restApi: RestApi
    private var mContext: Context
    private var providerRepository: ProviderRepository

    constructor(view: FormAdmissionContract.View, patientID: Long?, encounterType: String?, formName: String?, context: Context) {
        this.view = view
        this.patientID = patientID
        this.encounterType = encounterType
        this.formName = formName
        mPatient = PatientDAO().findPatientByID((patientID!!).toString())
        formUUID = getFormResourceByName(formName).uuid
        restApi = RestServiceBuilder.createService(RestApi::class.java)
        this.view.setPresenter(this)
        mContext = context
        providerRepository = ProviderRepository(context)
    }

    constructor(formAdmissionView: FormAdmissionContract.View, restApi: RestApi, context: Context) {
        view = formAdmissionView
        this.restApi = restApi
        view.setPresenter(this)
        mContext = context
        providerRepository = ProviderRepository(restApi)
    }

    override fun subscribe() {
        //the function to start with
    }

    override fun getProviders(formAdmissionFragment: FormAdmissionFragment?) {
        providerRepository.providers.observe(formAdmissionFragment!!, Observer { providerList: List<Provider?>? -> updateViews(providerList) })
    }

    override fun updateViews(providerList: List<Provider?>?) {
        if (providerList != null && providerList.isNotEmpty()) {
            view.updateProviderAdapter(providerList)
        } else {
            view.showToast(mContext.resources.getString(R.string.error_occurred))
            view.enableSubmitButton(false)
        }
    }

    override fun getLocation(url: String?, formAdmissionFragment: FormAdmissionFragment?) {
        providerRepository.getLocation(url).observe(formAdmissionFragment!!, Observer { locationList: List<LocationEntity?>? -> updateLocationList(locationList) })
    }

    fun updateLocationList(locationList: List<LocationEntity?>?) {
        if(locationList != null && locationList.isNotEmpty()) {
            view.updateLocationAdapter(locationList)
        } else {
            view.enableSubmitButton(false)
            view.showToast(mContext.resources.getString(R.string.error_occurred))
        }
    }

    override fun getEncounterRoles(formAdmissionFragment: FormAdmissionFragment?) {
        providerRepository.encounterRoles.observe(formAdmissionFragment!!, Observer { encounterRolesList: List<Resource?>? -> updateEncounterRoles(encounterRolesList) })
    }

    fun updateEncounterRoles(encounterRolesList: List<Resource?>?) {
        if(encounterRolesList != null && encounterRolesList.isNotEmpty()) {
            view.updateEncounterRoleList(encounterRolesList)
        } else {
            view.enableSubmitButton(false)
            view.showToast(mContext.resources.getString(R.string.error_occurred))
        }
    }

    override fun createEncounter(providerUUID: String?, locationUUID: String?, encounterRoleUUID: String?) {
        view.enableSubmitButton(false)
        val encountercreate = Encountercreate()
        encountercreate.patient = mPatient!!.uuid
        encountercreate.encounterType = encounterType
        encountercreate.formname = formName
        encountercreate.patientId = patientID
        encountercreate.formUuid = formUUID
        encountercreate.location = locationUUID
        val observations: List<Obscreate> = ArrayList()
        encountercreate.observations = observations
        val encounterProviderCreate: MutableList<EncounterProviderCreate> = ArrayList()
        encounterProviderCreate.add(EncounterProviderCreate(providerUUID!!, encounterRoleUUID!!))
        encountercreate.encounterProvider = encounterProviderCreate
        val id = AppDatabase.getDatabase(OpenMRS.getInstance().applicationContext)
                .encounterCreateRoomDAO()
                .addEncounterCreated(encountercreate)
        encountercreate.id = id
        if (!mPatient!!.isSynced) {
            mPatient!!.addEncounters(encountercreate.id)
            mPatient!!.id?.let { PatientDAO().updatePatient(it, mPatient) }
            view.showToast(mContext.resources.getString(R.string.form_data_will_be_synced_later_error_message))
            view.enableSubmitButton(true)
        } else {
            EncounterService().addEncounter(encountercreate, object : DefaultResponseCallback {
                override fun onResponse() {
                    view.enableSubmitButton(true)
                    success(mContext.getString(R.string.form_submitted_successfully))
                }

                override fun onErrorResponse(errorMessage: String) {
                    error(errorMessage)
                    view.enableSubmitButton(true)
                }
            })
            view.quitFormEntry()
        }
    }
}