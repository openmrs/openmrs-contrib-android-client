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

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.BasePresenter;
import org.openmrs.mobile.api.RestApi;
import org.openmrs.mobile.api.RestServiceBuilder;
import org.openmrs.mobile.api.repository.PatientRepository;
import org.openmrs.mobile.dao.PatientDAO;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.models.PatientDto;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.PatientAndMatchingPatients;
import org.openmrs.mobile.utilities.PatientMerger;

import java.util.Queue;

import androidx.annotation.NonNull;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.google.common.collect.ComparisonChain.start;

public class MatchingPatientsPresenter extends BasePresenter implements MatchingPatientsContract.Presenter{

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
            updatePatient(mergedPatient);
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

    private void updatePatient(final Patient patient) {
        PatientDto patientDto = patient.getPatientDto();
        patient.setUuid(null);
        Call<PatientDto> call = restApi.updatePatient(patientDto, patient.getUuid(), ApplicationConstants.API.FULL);
        call.enqueue(new Callback<PatientDto>() {
            @Override
            public void onResponse(@NonNull Call<PatientDto> call, @NonNull Response<PatientDto> response) {
                if(response.isSuccessful()){
                    if(patientDAO.isUserAlreadySaved(patient.getUuid())){
                        Long id = patientDAO.findPatientByUUID(patient.getUuid()).getId();
                        patientDAO.updatePatient(id, patient);
                        patientDAO.deletePatient(patient.getId());
                    } else {
                        patientDAO.updatePatient(patient.getId(), patient);
                    }
                } else {
                    view.showErrorToast(response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<PatientDto> call, @NonNull Throwable t) {
                view.showErrorToast(t.getMessage());
            }
        });
    }

    @Override
    public void registerNewPatient() {
        final Patient patient = matchingPatientsList.poll().getPatient();
        patientRepository.syncPatient(patient);
        removeSelectedPatient();
        if (matchingPatientsList.peek() != null) {
            subscribe();
        } else {
            view.finishActivity();
        }
    }

    private void setSelectedIfOnlyOneMatching() {
        if(matchingPatientsList.peek().getMatchingPatientList().size() == 1){
            selectedPatient = matchingPatientsList.peek().getMatchingPatientList().get(0);
        }
    }
}
