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

import com.google.android.libraries.places.api.net.PlacesClient
import org.openmrs.mobile.activities.BasePresenterContract
import org.openmrs.mobile.activities.BaseView
import org.openmrs.mobile.models.ConceptAnswers
import org.openmrs.mobile.models.Patient

interface AddEditPatientContract {
    interface View : BaseView<Presenter> {
        fun finishPatientInfoActivity()
        fun setErrorsVisibility(givenNameError: Boolean,
                                familyNameError: Boolean,
                                dayOfBirthError: Boolean,
                                addressError: Boolean,
                                countryError: Boolean,
                                genderError: Boolean,
                                countryNull: Boolean,
                                stateError: Boolean,
                                cityError: Boolean,
                                postalError: Boolean)

        fun scrollToTop()
        fun hideSoftKeys()
        fun setProgressBarVisibility(visibility: Boolean)
        fun showSimilarPatientDialog(patients: List<Patient?>, newPatient: Patient?)
        fun startPatientDashbordActivity(patient: Patient?)
        fun showUpgradeRegistrationModuleInfo()
        fun areFieldsNotEmpty(): Boolean
        fun cannotMarkDeceased(message: String?)
        fun cannotMarkDeceased(messageID: Int)
        fun updateCauseOfDeathSpinner(concept: ConceptAnswers?)
    }

    interface Presenter : BasePresenterContract {
        val patientToUpdate: Patient?
        val isRegisteringPatient: Boolean
        fun confirmRegister(patient: Patient, isPatientUnidentified: Boolean)
        fun confirmUpdate(patient: Patient)
        fun finishPatientInfoActivity()
        fun registerPatient()
        fun updatePatient(patient: Patient?)
        val places: PlacesClient?
        val causeOfDeathGlobalID: Unit
    }
}