/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.mobile.activities.formdisplay

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import com.openmrs.android_sdk.library.api.repository.EncounterRepository
import com.openmrs.android_sdk.library.api.repository.FormRepository
import com.openmrs.android_sdk.library.dao.PatientDAO
import com.openmrs.android_sdk.library.models.Encountercreate
import com.openmrs.android_sdk.library.models.Obscreate
import com.openmrs.android_sdk.library.models.Patient
import com.openmrs.android_sdk.library.models.ResultType
import com.openmrs.android_sdk.utilities.ApplicationConstants.BundleKeys.ENCOUNTERTYPE
import com.openmrs.android_sdk.utilities.ApplicationConstants.BundleKeys.ENCOUNTER_UUID
import com.openmrs.android_sdk.utilities.ApplicationConstants.BundleKeys.FORM_NAME
import com.openmrs.android_sdk.utilities.ApplicationConstants.BundleKeys.PATIENT_ID_BUNDLE
import com.openmrs.android_sdk.utilities.InputField
import com.openmrs.android_sdk.utilities.SelectOneField
import com.openmrs.android_sdk.utilities.execute
import dagger.hilt.android.lifecycle.HiltViewModel
import org.joda.time.LocalDateTime
import org.openmrs.mobile.activities.BaseViewModel
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import javax.inject.Inject

@HiltViewModel
class FormDisplayMainViewModel @Inject constructor(
        private val patientDAO: PatientDAO,
        private val formRepository: FormRepository,
        private val encounterRepository: EncounterRepository,
        private val savedStateHandle: SavedStateHandle
) : BaseViewModel<Unit>() {

    private val patientId: Long = savedStateHandle.get(PATIENT_ID_BUNDLE)!!
    private val encounterType: String = savedStateHandle.get(ENCOUNTERTYPE)!!
    private val formName: String = savedStateHandle.get(FORM_NAME)!!
    private val encounterUuid: String? = savedStateHandle.get(ENCOUNTER_UUID)
    private val isUpdateEncounter = !encounterUuid.isNullOrEmpty()

    val patient: Patient = patientDAO.findPatientByID(patientId.toString())

    fun submitForm(inputFields: List<InputField>, radioGroupFields: List<SelectOneField>): LiveData<ResultType> {
        val enc = Encountercreate()
        enc.patientId = patientId
        enc.observations = createObservationsFromInputFields(inputFields) + createObservationsFromRadioGroupFields(radioGroupFields)

        return if (isUpdateEncounter) updateRecords(encounterUuid!!, enc) else createRecords(enc)
    }

    private fun createRecords(enc: Encountercreate): LiveData<ResultType> {
        val resultLiveData = MutableLiveData<ResultType>()

        addSubscription(Observable.fromCallable {
            enc.patient = patient.uuid
            enc.encounterType = encounterType
            enc.formname = formName
            enc.formUuid = formRepository.fetchFormResourceByName(formName).execute().uuid
            return@fromCallable enc
        }
                .flatMap { encounterCreate -> encounterRepository.saveEncounter(encounterCreate) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { resultLiveData.value = it },
                        { resultLiveData.value = ResultType.EncounterSubmissionError }
                )
        )

        return resultLiveData
    }

    private fun updateRecords(encounterUuid: String, enc: Encountercreate): LiveData<ResultType> {
        val resultLiveData = MutableLiveData<ResultType>()

        addSubscription(encounterRepository.updateEncounter(encounterUuid, enc)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { resultLiveData.value = ResultType.EncounterSubmissionSuccess },
                        { resultLiveData.value = ResultType.EncounterSubmissionError }
                )
        )
        return resultLiveData
    }

    private fun createObservationsFromInputFields(inputFields: List<InputField>): List<Obscreate> {
        val observations = mutableListOf<Obscreate>()

        for (input in inputFields) {
            if (input.value != InputField.DEFAULT_VALUE) {
                observations += Obscreate().apply {
                    concept = input.concept
                    value = input.value.toString()
                    obsDatetime = LocalDateTime().toString()
                    person = patient.uuid
                }
            }
        }

        return observations
    }

    private fun createObservationsFromRadioGroupFields(radioGroupFields: List<SelectOneField>): List<Obscreate> {
        val observations = mutableListOf<Obscreate>()

        for (radioGroupField in radioGroupFields) {
            if (radioGroupField.chosenAnswer != null) {
                observations += Obscreate().apply {
                    concept = radioGroupField.concept
                    value = radioGroupField.chosenAnswer!!.concept
                    obsDatetime = LocalDateTime().toString()
                    person = patient.uuid
                }
            }
        }

        return observations
    }


}
