/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.mobile.api;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.matchingpatients.MatchingPatientsActivity;
import org.openmrs.mobile.api.retrofit.PatientApi;
import org.openmrs.mobile.dao.PatientDAO;
import org.openmrs.mobile.models.Module;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.models.Results;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.ModuleUtils;
import org.openmrs.mobile.utilities.NetworkUtils;
import org.openmrs.mobile.utilities.PatientAndMatchesWrapper;
import org.openmrs.mobile.utilities.PatientAndMatchingPatients;
import org.openmrs.mobile.utilities.PatientComparator;
import org.openmrs.mobile.utilities.ToastUtil;

import java.io.IOException;
import java.util.List;
import java.util.ListIterator;

import retrofit2.Call;
import retrofit2.Response;

public class PatientService extends IntentService {

    public static final String PATIENT_SERVICE_TAG = "PATIENT_SERVICE";
    private boolean calculatedLocally = false;

    public PatientService() {
        super("Register Patients");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if(NetworkUtils.isOnline()) {
            PatientAndMatchesWrapper patientAndMatchesWrapper = new PatientAndMatchesWrapper();
            List<Patient> patientList = new PatientDAO().getUnsyncedPatients();
            final ListIterator<Patient> it = patientList.listIterator();
            while (it.hasNext()) {
                final Patient patient=it.next();
                fetchSimilarPatients(patient, patientAndMatchesWrapper);
            }
            if (!patientAndMatchesWrapper.getMatchingPatients().isEmpty()) {
                Intent intent1 = new Intent(getApplicationContext(), MatchingPatientsActivity.class);
                intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent1.putExtra(ApplicationConstants.BundleKeys.CALCULATED_LOCALLY, calculatedLocally);
                intent1.putExtra(ApplicationConstants.BundleKeys.PATIENTS_AND_MATCHES, patientAndMatchesWrapper);
                startActivity(intent1);
            }
        } else {
            ToastUtil.error(getString(R.string.activity_no_internet_connection) +
                    getString(R.string.activity_sync_after_connection));
        }
    }
    private void fetchSimilarPatients(final Patient patient, final PatientAndMatchesWrapper patientAndMatchesWrapper) {
        RestApi restApi = RestServiceBuilder.createService(RestApi.class);
        Call<Results<Module>> moduleCall = restApi.getModules(ApplicationConstants.API.FULL);
        try {
            Response<Results<Module>> moduleResp = moduleCall.execute();
            if(moduleResp.isSuccessful()){
                if(ModuleUtils.isRegistrationCore1_7orAbove(moduleResp.body().getResults())){
                    fetchSimilarPatientsFromServer(patient, patientAndMatchesWrapper);
                } else {
                    fetchPatientsAndCalculateLocally(patient, patientAndMatchesWrapper);
                }
            } else {
                fetchPatientsAndCalculateLocally(patient, patientAndMatchesWrapper);
            }
        } catch (IOException e) {
            Log.e(PATIENT_SERVICE_TAG, e.getMessage());
        }
    }

    private void fetchPatientsAndCalculateLocally(Patient patient, PatientAndMatchesWrapper patientAndMatchesWrapper) throws IOException {
        calculatedLocally = true;
        RestApi restApi = RestServiceBuilder.createService(RestApi.class);
        Call<Results<Patient>> patientCall = restApi.getPatients(patient.getPerson().getName().getGivenName(), ApplicationConstants.API.FULL);
        Response<Results<Patient>> resp = patientCall.execute();
        if(resp.isSuccessful()){
            List<Patient> similarPatient = new PatientComparator().findSimilarPatient(resp.body().getResults(), patient);
            if(!similarPatient.isEmpty()){
                patientAndMatchesWrapper.addToList(new PatientAndMatchingPatients(patient, similarPatient));
            } else {
                new PatientApi().syncPatient(patient);
            }
        }
    }

    private void fetchSimilarPatientsFromServer(Patient patient, PatientAndMatchesWrapper patientAndMatchesWrapper) throws IOException {
        calculatedLocally = false;
        RestApi restApi = RestServiceBuilder.createService(RestApi.class);
        Call<Results<Patient>> patientCall = restApi.getSimilarPatients(patient.toMap());
        Response<Results<Patient>> patientsResp = patientCall.execute();
        if(patientsResp.isSuccessful()) {
            List<Patient> patientList = patientsResp.body().getResults();
            if (!patientList.isEmpty()) {
                patientAndMatchesWrapper.addToList(new PatientAndMatchingPatients(patient, patientList));
            } else {
                new PatientApi().syncPatient(patient);
            }
        }
    }

}