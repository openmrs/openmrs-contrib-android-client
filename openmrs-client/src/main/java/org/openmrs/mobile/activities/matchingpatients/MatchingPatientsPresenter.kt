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
package org.openmrs.mobile.activities.matchingpatients

import com.google.common.collect.ComparisonChain
import org.openmrs.mobile.R
import org.openmrs.mobile.activities.BasePresenter
import org.openmrs.mobile.api.RestApi
import org.openmrs.mobile.api.RestServiceBuilder
import org.openmrs.mobile.api.promise.SimpleDeferredObject
import org.openmrs.mobile.api.repository.PatientRepository
import org.openmrs.mobile.dao.PatientDAO
import org.openmrs.mobile.listeners.retrofitcallbacks.DefaultResponseCallback
import org.openmrs.mobile.listeners.retrofitcallbacks.PatientDeferredResponseCallback
import org.openmrs.mobile.models.Patient
import org.openmrs.mobile.utilities.PatientAndMatchingPatients
import org.openmrs.mobile.utilities.PatientMerger
import org.openmrs.mobile.utilities.ToastUtil.error
import org.openmrs.mobile.utilities.ToastUtil.notify
import java.util.Queue

class MatchingPatientsPresenter : BasePresenter, MatchingPatientsContract.Presenter {
    private var restApi: RestApi
    private var patientDAO: PatientDAO
    private var patientRepository: PatientRepository
    private var view: MatchingPatientsContract.View
    private var matchingPatientsList: Queue<PatientAndMatchingPatients>
    private var selectedPatient: Patient? = null

    constructor(view: MatchingPatientsContract.View, matchingPatientsList: Queue<PatientAndMatchingPatients>) {
        this.view = view
        this.matchingPatientsList = matchingPatientsList
        this.restApi = RestServiceBuilder.createService(RestApi::class.java)
        this.patientDAO = PatientDAO()
        this.patientRepository = PatientRepository()
        this.view.setPresenter(this)
    }

    constructor(view: MatchingPatientsContract.View, matchingPatientsList: Queue<PatientAndMatchingPatients>,
                restApi: RestApi, patientDAO: PatientDAO, patientRepository: PatientRepository) {
        this.view = view
        this.matchingPatientsList = matchingPatientsList
        this.restApi = restApi
        this.patientDAO = patientDAO
        this.patientRepository = patientRepository
        this.view.setPresenter(this)
    }

    override fun subscribe() {
        view.showPatientsData(matchingPatientsList.peek()!!.patient, matchingPatientsList.peek()!!.matchingPatientList)
        setSelectedIfOnlyOneMatching()
    }

    override fun setSelectedPatient(patient: Patient?) {
        selectedPatient = patient
    }

    override fun removeSelectedPatient() {
        selectedPatient = null
    }

    override fun mergePatients() {
        if (selectedPatient != null) {
            val patientToMerge = matchingPatientsList.poll()!!.patient
            val mergedPatient = PatientMerger().mergePatient(selectedPatient!!, patientToMerge)
            updateMatchingPatient(mergedPatient)
            removeSelectedPatient()
            if (matchingPatientsList.peek() != null) {
                ComparisonChain.start()
            } else {
                view.finishActivity()
            }
        } else {
            view.notifyUser(R.string.no_patient_selected)
        }
    }

    fun updateMatchingPatient(patient: Patient) {
        patientRepository.updateMatchingPatient(patient, object : DefaultResponseCallback {
            override fun onResponse() {
                if (patientDAO.isUserAlreadySaved(patient.uuid)) {
                    val id: Long = patientDAO.findPatientByUUID(patient.uuid).id!!
                    patientDAO.updatePatient(id, patient)
                    patientDAO.deletePatient(patient.id!!)
                } else {
                    patientDAO.updatePatient(patient.id!!, patient)
                }
            }

            override fun onErrorResponse(errorMessage: String?) {
                view.showErrorToast(errorMessage)
            }
        })
    }

    override fun registerNewPatient() {
        val patient = matchingPatientsList.poll()!!.patient
        patientRepository.syncPatient(patient, object : PatientDeferredResponseCallback {
            override fun onResponse(response: SimpleDeferredObject<Patient?>?) {
                response!!.resolve(patient)
            }

            override fun onErrorResponse(errorMessage: String?, errorResponse: SimpleDeferredObject<Patient?>?) {
                errorResponse!!.reject(RuntimeException(errorMessage))
                error(errorMessage!!)
            }

            override fun onNotifyResponse(notifyMessage: String?) {
                notifyMessage?.let { notify(it) }
            }
        })
        removeSelectedPatient()
        if (matchingPatientsList.peek() != null) {
            subscribe()
        } else {
            view.finishActivity()
        }
    }

    private fun setSelectedIfOnlyOneMatching() {
        if (matchingPatientsList.peek()!!.matchingPatientList.size == 1) {
            selectedPatient = matchingPatientsList.peek()!!.matchingPatientList[0]
        }
    }
}