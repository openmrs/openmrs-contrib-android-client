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
import android.util.Log
import org.openmrs.mobile.R
import org.openmrs.mobile.activities.matchingpatients.MatchingPatientsActivity
import org.openmrs.mobile.api.repository.PatientRepository
import org.openmrs.mobile.dao.PatientDAO
import org.openmrs.mobile.models.Patient
import org.openmrs.mobile.utilities.ApplicationConstants
import org.openmrs.mobile.utilities.ModuleUtils.isRegistrationCore1_7orAbove
import org.openmrs.mobile.utilities.NetworkUtils.isOnline
import org.openmrs.mobile.utilities.PatientAndMatchesWrapper
import org.openmrs.mobile.utilities.PatientAndMatchingPatients
import org.openmrs.mobile.utilities.PatientComparator
import org.openmrs.mobile.utilities.ToastUtil.error
import java.io.IOException

class PatientService : IntentService("Register Patients") {
    private var calculatedLocally = false
    override fun onHandleIntent(intent: Intent?) {
        if (isOnline()) {
            val patientAndMatchesWrapper = PatientAndMatchesWrapper()
            val patientList = PatientDAO().unSyncedPatients
            val it: ListIterator<Patient> = patientList.listIterator()
            while (it.hasNext()) {
                val patient = it.next()
                fetchSimilarPatients(patient, patientAndMatchesWrapper)
            }
            if (!patientAndMatchesWrapper.matchingPatients.isEmpty()) {
                val intent1 = Intent(applicationContext, MatchingPatientsActivity::class.java)
                intent1.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                intent1.putExtra(ApplicationConstants.BundleKeys.CALCULATED_LOCALLY, calculatedLocally)
                intent1.putExtra(ApplicationConstants.BundleKeys.PATIENTS_AND_MATCHES, patientAndMatchesWrapper)
                startActivity(intent1)
            }
        } else {
            error(getString(R.string.activity_no_internet_connection) +
                    getString(R.string.activity_sync_after_connection))
        }
    }

    private fun fetchSimilarPatients(patient: Patient, patientAndMatchesWrapper: PatientAndMatchesWrapper) {
        val restApi = RestServiceBuilder.createService(RestApi::class.java)
        val moduleCall = restApi.getModules(ApplicationConstants.API.FULL)
        try {
            val moduleResp = moduleCall.execute()
            if (moduleResp.isSuccessful) {
                if (isRegistrationCore1_7orAbove(moduleResp.body()!!.results)) {
                    fetchSimilarPatientsFromServer(patient, patientAndMatchesWrapper)
                } else {
                    fetchPatientsAndCalculateLocally(patient, patientAndMatchesWrapper)
                }
            } else {
                fetchPatientsAndCalculateLocally(patient, patientAndMatchesWrapper)
            }
        } catch (e: IOException) {
            Log.e(PATIENT_SERVICE_TAG, e.message)
        }
    }

    @Throws(IOException::class)
    private fun fetchPatientsAndCalculateLocally(patient: Patient, patientAndMatchesWrapper: PatientAndMatchesWrapper) {
        calculatedLocally = true
        val restApi = RestServiceBuilder.createService(RestApi::class.java)
        val patientCall = restApi.getPatients(patient.name.givenName, ApplicationConstants.API.FULL)
        val resp = patientCall.execute()
        if (resp.isSuccessful) {
            val similarPatient = PatientComparator().findSimilarPatient(resp.body()!!.results, patient)
            if (!similarPatient.isEmpty()) {
                patientAndMatchesWrapper.addToList(PatientAndMatchingPatients(patient, similarPatient))
            } else {
                PatientRepository().syncPatient(patient)
            }
        }
    }

    @Throws(IOException::class)
    private fun fetchSimilarPatientsFromServer(patient: Patient, patientAndMatchesWrapper: PatientAndMatchesWrapper) {
        calculatedLocally = false
        val restApi = RestServiceBuilder.createService(RestApi::class.java)
        val patientCall = restApi.getSimilarPatients(patient.toMap())
        val patientsResp = patientCall.execute()
        if (patientsResp.isSuccessful) {
            val patientList = patientsResp.body()!!.results
            if (!patientList.isEmpty()) {
                patientAndMatchesWrapper.addToList(PatientAndMatchingPatients(patient, patientList))
            } else {
                PatientRepository().syncPatient(patient)
            }
        }
    }

    companion object {
        const val PATIENT_SERVICE_TAG = "PATIENT_SERVICE"
    }
}