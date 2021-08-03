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

package org.openmrs.mobile.activities.matchingpatients;

import com.openmrs.android_sdk.library.dao.PatientDAO;
import com.openmrs.android_sdk.library.models.Patient;
import com.openmrs.android_sdk.utilities.PatientAndMatchingPatients;
import com.openmrs.android_sdk.utilities.ToastUtil;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.BasePresenter;
import com.openmrs.android_sdk.library.api.RestApi;
import com.openmrs.android_sdk.library.api.RestServiceBuilder;
import com.openmrs.android_sdk.library.api.promise.SimpleDeferredObject;
import com.openmrs.android_sdk.library.api.repository.PatientRepository;
import com.openmrs.android_sdk.library.listeners.retrofitcallbacks.DefaultResponseCallback;
import com.openmrs.android_sdk.library.listeners.retrofitcallbacks.PatientDeferredResponseCallback;
import org.openmrs.mobile.utilities.PatientMerger;

import java.util.Queue;

import static com.google.common.collect.ComparisonChain.start;

public class MatchingPatientsPresenter extends BasePresenter implements MatchingPatientsContract.Presenter {
    private RestApi restApi;
    private PatientDAO patientDAO;
    private PatientRepository patientRepository;
    private MatchingPatientsContract.View view;
    private Queue<PatientAndMatchingPatients> matchingPatientsList;
    private Patient selectedPatient;

    public MatchingPatientsPresenter(MatchingPatientsContract.View view, Queue<PatientAndMatchingPatients> matchingPatientsList) {
        this.view = view;
        this.matchingPatientsList = matchingPatientsList;
        this.restApi = RestServiceBuilder.createService(RestApi.class);
        this.patientDAO = new PatientDAO();
        this.patientRepository = new PatientRepository();
        this.view.setPresenter(this);
    }

    public MatchingPatientsPresenter(MatchingPatientsContract.View view, Queue<PatientAndMatchingPatients> matchingPatientsList,
                                     RestApi restApi, PatientDAO patientDAO, PatientRepository patientRepository) {
        this.view = view;
        this.matchingPatientsList = matchingPatientsList;
        this.restApi = restApi;
        this.patientDAO = patientDAO;
        this.patientRepository = patientRepository;
        this.view.setPresenter(this);
    }

    @Override
    public void subscribe() {
        view.showPatientsData(matchingPatientsList.peek().getPatient(), matchingPatientsList.peek().getMatchingPatientList());
        setSelectedIfOnlyOneMatching();
    }

    @Override
    public void setSelectedPatient(Patient patient) {
        selectedPatient = patient;
    }

    @Override
    public void removeSelectedPatient() {
        selectedPatient = null;
    }

    @Override
    public void mergePatients() {
        if (selectedPatient != null) {
            Patient patientToMerge = matchingPatientsList.poll().getPatient();
            Patient mergedPatient = new PatientMerger().mergePatient(selectedPatient, patientToMerge);
            updateMatchingPatient(mergedPatient);
            removeSelectedPatient();
            if (matchingPatientsList.peek() != null) {
                start();
            } else {
                view.finishActivity();
            }
        } else {
            view.notifyUser(R.string.no_patient_selected);
        }
    }

    public void updateMatchingPatient(final Patient patient) {
        patientRepository.updateMatchingPatient(patient, new DefaultResponseCallback() {
            @Override
            public void onResponse() {
                if (patientDAO.isUserAlreadySaved(patient.getUuid())) {
                    Long id = patientDAO.findPatientByUUID(patient.getUuid()).getId();
                    patientDAO.updatePatient(id, patient);
                    patientDAO.deletePatient(patient.getId());
                } else {
                    patientDAO.updatePatient(patient.getId(), patient);
                }
            }

            @Override
            public void onErrorResponse(String errorMessage) {
                view.showErrorToast(errorMessage);
            }
        });
    }

    @Override
    public void registerNewPatient() {
        final Patient patient = matchingPatientsList.poll().getPatient();
        patientRepository.syncPatient(patient, new PatientDeferredResponseCallback() {
            @Override
            public void onResponse(SimpleDeferredObject<Patient> response) {
                response.resolve(patient);
            }

            @Override
            public void onErrorResponse(String errorMessage, SimpleDeferredObject<Patient> errorResponse) {
                errorResponse.reject(new RuntimeException(errorMessage));
                ToastUtil.error(errorMessage);
            }

            @Override
            public void onNotifyResponse(String notifyMessage) {
                ToastUtil.notify(notifyMessage);
            }
        });

        removeSelectedPatient();
        if (matchingPatientsList.peek() != null) {
            subscribe();
        } else {
            view.finishActivity();
        }
    }

    private void setSelectedIfOnlyOneMatching() {
        if (matchingPatientsList.peek().getMatchingPatientList().size() == 1) {
            selectedPatient = matchingPatientsList.peek().getMatchingPatientList().get(0);
        }
    }
}
