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
package org.openmrs.mobile.activities.addeditpatient

import android.content.Context
import com.google.android.libraries.places.api.net.PlacesClient
import org.openmrs.mobile.R
import org.openmrs.mobile.activities.BasePresenter
import org.openmrs.mobile.api.RestApi
import org.openmrs.mobile.api.RestServiceBuilder
import org.openmrs.mobile.api.promise.SimpleDeferredObject
import org.openmrs.mobile.api.repository.PatientRepository
import org.openmrs.mobile.dao.PatientDAO
import org.openmrs.mobile.listeners.retrofitcallbacks.DefaultResponseCallback
import org.openmrs.mobile.listeners.retrofitcallbacks.PatientDeferredResponseCallback
import org.openmrs.mobile.listeners.retrofitcallbacks.PatientResponseCallback
import org.openmrs.mobile.listeners.retrofitcallbacks.VisitsResponseCallback
import org.openmrs.mobile.models.ConceptAnswers
import org.openmrs.mobile.models.Module
import org.openmrs.mobile.models.Patient
import org.openmrs.mobile.models.Results
import org.openmrs.mobile.utilities.ApplicationConstants
import org.openmrs.mobile.utilities.ModuleUtils.isRegistrationCore1_7orAbove
import org.openmrs.mobile.utilities.NetworkUtils
import org.openmrs.mobile.utilities.PatientComparator
import org.openmrs.mobile.utilities.StringUtils.isBlank
import org.openmrs.mobile.utilities.ToastUtil.error
import org.openmrs.mobile.utilities.ViewUtils
import org.openmrs.mobile.utilities.ViewUtils.validateText
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddEditPatientPresenter : BasePresenter, AddEditPatientContract.Presenter {
    private val mPatientInfoView: AddEditPatientContract.View
    private var isPatientUnidentified = false
    private var patientRepository: PatientRepository
    private var restApi: RestApi
    private var mPatient: Patient? = null
    private var patientToUpdateId: String
    private var mCountries: List<String>
    private var registeringPatient = false
    private lateinit var placesClient: PlacesClient

    override val isRegisteringPatient: Boolean
        get() = registeringPatient

    override val places: PlacesClient
        get() = placesClient

    override val causeOfDeathGlobalID: Unit
        get() = patientRepository.getCauseOfDeathGlobalID(object : VisitsResponseCallback {
            override fun onSuccess(response: String?) {
                if (response != null) {
                    if (response.length == ApplicationConstants.UUID_LENGTH) {
                        getConceptCauseOfDeath(response)
                    } else {
                        mPatientInfoView.cannotMarkDeceased(R.string.mark_patient_deceased_invalid_uuid)
                    }
                }
            }

            override fun onFailure(errorMessage: String?) {
                mPatientInfoView.cannotMarkDeceased(errorMessage)
            }
        })

    override fun updatePatient(patient: Patient?) {
        patientRepository.updatePatient(patient, object : DefaultResponseCallback {

        })
    }

    override fun confirmUpdate(patient: Patient) {
        if (!registeringPatient and validate(patient)) {
            mPatientInfoView.setProgressBarVisibility(true)
            mPatientInfoView.hideSoftKeys()
            registeringPatient = true
            updatePatient(patient)
        }
        else {
            mPatientInfoView.scrollToTop()
        }
    }

    override fun confirmRegister(patient: Patient, isPatientUnidentified: Boolean) {
        this.isPatientUnidentified = isPatientUnidentified
        if (!registeringPatient && validate(patient)) {
            mPatientInfoView.setProgressBarVisibility(true)
            mPatientInfoView.hideSoftKeys()
            registeringPatient = true
            if (!isPatientUnidentified) {
                findSimilarPatients(patient)
            } else {
                registerPatient()
            }
        } else {
            mPatientInfoView.scrollToTop()
        }
    }

    override val patientToUpdate: Patient?
        get() = PatientDAO().findPatientByID(patientToUpdateId)

    constructor(mPatientInfoView: AddEditPatientContract.View,
                countries: List<String>,
                patientToUpdateId: String,
                placesClient: PlacesClient,
                appContext: Context?) {
        this.mPatientInfoView = mPatientInfoView
        this.mPatientInfoView.setPresenter(this)
        mCountries = countries
        this.patientToUpdateId = patientToUpdateId
        patientRepository = PatientRepository()
        restApi = RestServiceBuilder.createService(RestApi::class.java)
        this.placesClient = placesClient
    }

    constructor(mPatientInfoView: AddEditPatientContract.View, patientRepository: PatientRepository,
                mPatient: Patient?, patientToUpdateId: String,
                mCountries: List<String>, restApi: RestApi) {
        this.mPatientInfoView = mPatientInfoView
        this.patientRepository = patientRepository
        this.mPatient = mPatient
        this.patientToUpdateId = patientToUpdateId
        this.mCountries = mCountries
        this.restApi = restApi
        this.mPatientInfoView.setPresenter(this)
    }

    override fun subscribe() {

        // This method is intentionally empty
    }

    override fun finishPatientInfoActivity() {
        mPatientInfoView.finishPatientInfoActivity()
    }

    private fun validate(patient: Patient): Boolean {
        var givenNameError = false
        var familyNameError = false
        var dateOfBirthError = false
        var genderError = false
        var addressError = false
        var countryError = false
        var countryNull = false
        var stateError = false
        var cityError = false
        var postalError = false
        if (!isPatientUnidentified) {
            mPatientInfoView
                    .setErrorsVisibility(givenNameError, familyNameError, dateOfBirthError, genderError, addressError, countryError, countryNull, stateError, cityError, postalError)

            // Validate names
            val currentPersonName = patient.name
            if (isBlank(currentPersonName.givenName)
                    || !validateText(currentPersonName.givenName, ViewUtils.ILLEGAL_CHARACTERS)) {
                givenNameError = true
            }

            // Middle name can be left empty
            if (!validateText(currentPersonName.middleName, ViewUtils.ILLEGAL_CHARACTERS)) {
                givenNameError = true
            }
            if (isBlank(currentPersonName.familyName)
                    || !validateText(currentPersonName.familyName, ViewUtils.ILLEGAL_CHARACTERS)) {
                familyNameError = true
            }

            // Validate address
            val patientAddress1 = patient.address.address1
            val patientAddress2 = patient.address.address2
            if ((isBlank(patientAddress1)
                            && isBlank(patientAddress2)) || !validateText(patientAddress1, ViewUtils.ILLEGAL_ADDRESS_CHARACTERS)
                    || !validateText(patientAddress2, ViewUtils.ILLEGAL_ADDRESS_CHARACTERS)) {
                addressError = true
            }
            if (!isBlank(patient.address.country) && !mCountries.contains(patient.address.country)) {
                countryError = true
            }
            if (isBlank(patient.address.country)) {
                countryNull = true
            }
            if (isBlank(patient.address.stateProvince)) {
                stateError = true
            }
            if (isBlank(patient.address.cityVillage)) {
                cityError = true
            }
            if (isBlank(patient.address.postalCode)) {
                postalError = true
            }
        }

        // Validate gender
        if (isBlank(patient.gender)) {
            genderError = true
        }

        // Validate date of birth
        if (isBlank(patient.birthdate)) {
            dateOfBirthError = true
        }
        val result = !givenNameError && !familyNameError && !dateOfBirthError && !addressError && !countryError && !genderError
        return if (result) {
            mPatient = patient
            true
        } else {
            mPatientInfoView
                    .setErrorsVisibility(givenNameError, familyNameError, dateOfBirthError, addressError, countryError, genderError, countryNull, stateError, cityError, postalError)
            false
        }
    }

    override fun registerPatient() {
        patientRepository.registerPatient(mPatient, object : PatientDeferredResponseCallback {
            override fun onNotifyResponse(notifyMessage: String?) {}
            override fun onErrorResponse(errorMessage: String?, errorResponse: SimpleDeferredObject<Patient?>?) {}
            override fun onResponse(response: SimpleDeferredObject<Patient?>?) {}
            override fun onResponse() {
                mPatientInfoView.startPatientDashbordActivity(mPatient)
                mPatientInfoView.finishPatientInfoActivity()
            }

            override fun onErrorResponse(errorMessage: String) {
                registeringPatient = false
                mPatientInfoView.setProgressBarVisibility(false)
            }
        })
    }

    // left as it is due to private nature of the method just is being used from here only
    private fun getConceptCauseOfDeath(uuid: String?) {
        restApi.getConceptFromUUID(uuid).enqueue(object : Callback<ConceptAnswers> {
            override fun onResponse(call: Call<ConceptAnswers>, response: Response<ConceptAnswers>) {
                if (response.isSuccessful) {
                    if (response.body()!!.answers.isNotEmpty()) {
                        mPatientInfoView.updateCauseOfDeathSpinner(response.body())
                    } else {
                        mPatientInfoView.cannotMarkDeceased(R.string.mark_patient_deceased_concept_has_no_answer)
                    }
                } else {
                    mPatientInfoView.cannotMarkDeceased(ApplicationConstants.EMPTY_STRING)
                }
            }

            override fun onFailure(call: Call<ConceptAnswers>, t: Throwable) {
                mPatientInfoView.cannotMarkDeceased(t.message)
            }
        })
    }

    private fun findSimilarPatients(patient: Patient) {
        if (NetworkUtils.isOnline()) {
            val moduleCall = restApi.getModules(ApplicationConstants.API.FULL)
            moduleCall.enqueue(object : Callback<Results<Module>> {
                override fun onResponse(call: Call<Results<Module>>, response: Response<Results<Module>>) {
                    if (response.isSuccessful) {
                        if (isRegistrationCore1_7orAbove(response.body()!!.results)) {
                            fetchSimilarPatientsFromServer(patient)
                        }
                        else {
                            fetchSimilarPatientAndCalculateLocally(patient)
                        }
                    }
                    else {
                        fetchSimilarPatientAndCalculateLocally(patient)
                    }
                }

                override fun onFailure(call: Call<Results<Module>>, t: Throwable) {
                    registeringPatient = false
                    mPatientInfoView.setProgressBarVisibility(false)
                    error(t.message!!)
                }
            })
        } else {
            val similarPatient = PatientComparator().findSimilarPatient(PatientDAO().allPatients.toBlocking().first(), patient)
            if (similarPatient.isNotEmpty()) {
                mPatientInfoView.showSimilarPatientDialog(similarPatient, patient)
            } else {
                registerPatient()
            }
        }
    }

    private fun fetchSimilarPatientAndCalculateLocally(patient: Patient) {
        patientRepository.fetchSimilarPatientAndCalculateLocally(patient, object : PatientResponseCallback {
            override fun onResponse(patientResults: Results<Patient?>?) {
                registeringPatient = false
                val similarPatients = patientResults!!.results
                if (similarPatients.isNotEmpty()) {
                    mPatientInfoView.showSimilarPatientDialog(similarPatients, patient)
                } else {
                    registerPatient()
                }
            }

            override fun onErrorResponse(errorMessage: String) {
                registeringPatient = false
                mPatientInfoView.setProgressBarVisibility(false)
                error(errorMessage)
            }
        })
    }

    private fun fetchSimilarPatientsFromServer(patient: Patient) {
        patientRepository.fetchSimilarPatientsFromServer(patient, object : PatientResponseCallback {
            override fun onResponse(patientResults: Results<Patient?>?) {
                registeringPatient = false
                val similarPatients = patientResults!!.results
                if (similarPatients.isNotEmpty()) {
                    mPatientInfoView.showSimilarPatientDialog(similarPatients, patient)
                } else {
                    registerPatient()
                }
            }

            override fun onErrorResponse(errorMessage: String) {
                registeringPatient = false
                mPatientInfoView.setProgressBarVisibility(false)
                error(errorMessage)
            }
        })
    }
}