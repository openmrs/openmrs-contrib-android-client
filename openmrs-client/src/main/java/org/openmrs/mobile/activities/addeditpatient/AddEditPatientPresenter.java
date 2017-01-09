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

package org.openmrs.mobile.activities.addeditpatient;

import org.openmrs.mobile.api.RestApi;
import org.openmrs.mobile.api.RestServiceBuilder;
import org.openmrs.mobile.api.retrofit.PatientApi;
import org.openmrs.mobile.dao.PatientDAO;
import org.openmrs.mobile.listeners.retrofit.DefaultResponseCallbackListener;
import org.openmrs.mobile.models.Module;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.models.Results;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.ModuleUtils;
import org.openmrs.mobile.utilities.NetworkUtils;
import org.openmrs.mobile.utilities.PatientComparator;
import org.openmrs.mobile.utilities.StringUtils;
import org.openmrs.mobile.utilities.ToastUtil;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddEditPatientPresenter implements AddEditPatientContract.Presenter {

    private final AddEditPatientContract.View mPatientInfoView;

    private Patient mPatient;
    private String patientToUpdateId;
    private List<String> mCountries;

    public AddEditPatientPresenter(AddEditPatientContract.View mPatientInfoView,
                                   List<String> countries,
                                   String patientToUpdateId) {
        this.mPatientInfoView = mPatientInfoView;
        this.mPatientInfoView.setPresenter(this);
        this.mCountries = countries;
        this.patientToUpdateId = patientToUpdateId;
    }
    
    @Override
    public void start(){}

    @Override
    public Patient getPatientToUpdate() {
        Patient patientToUpdate = new PatientDAO().findPatientByID(patientToUpdateId);
        return patientToUpdate;
    }

    @Override
    public void confirmRegister(Patient patient) {
        if(validate(patient)) {
            mPatientInfoView.setProgressBarVisibility(true);
            mPatientInfoView.hideSoftKeys();
            findSimilarPatients(patient);
        }
        else {
            mPatientInfoView.scrollToTop();
        }
    }

    @Override
    public void confirmUpdate(Patient patient) {
        if (validate(patient)) {
            mPatientInfoView.setProgressBarVisibility(true);
            mPatientInfoView.hideSoftKeys();
            updatePatient(patient);
        } else {
            mPatientInfoView.scrollToTop();
        }
    }

    @Override
    public void finishPatientInfoActivity() {
        mPatientInfoView.finishPatientInfoActivity();
    }

    private boolean validate(Patient patient) {

        boolean ferr=false, lerr=false, doberr=false, gerr=false, adderr=false, countryerr=false;
        mPatientInfoView.setErrorsVisibility(ferr, lerr, doberr, gerr, adderr, countryerr);

        // Validate names
        if(StringUtils.isBlank(patient.getPerson().getName().getGivenName())) {
            ferr=true;
        }
        if(StringUtils.isBlank(patient.getPerson().getName().getFamilyName())) {
            lerr=true;
        }

        // Validate date of birth
        if(StringUtils.isBlank(patient.getPerson().getBirthdate())) {
            doberr = true;
        }

        // Validate address
        if(StringUtils.isBlank(patient.getPerson().getAddress().getAddress1())
                && StringUtils.isBlank(patient.getPerson().getAddress().getAddress2())
                && StringUtils.isBlank(patient.getPerson().getAddress().getCityVillage())
                && StringUtils.isBlank(patient.getPerson().getAddress().getStateProvince())
                && StringUtils.isBlank(patient.getPerson().getAddress().getCountry())
                && StringUtils.isBlank(patient.getPerson().getAddress().getPostalCode())) {
            adderr=true;
        }

        if (!StringUtils.isBlank(patient.getPerson().getAddress().getCountry()) && !mCountries.contains(patient.getPerson().getAddress().getCountry())) {
            countryerr = true;
        }

        // Validate gender
        if (StringUtils.isBlank(patient.getPerson().getGender())) {
            gerr=true;
        }

        boolean result = !ferr && !lerr && !doberr && !adderr && !countryerr && !gerr;
        if (result) {
            mPatient = patient;
            return true;
        }
        else {
            mPatientInfoView.setErrorsVisibility(ferr, lerr, doberr, adderr, countryerr, gerr);
            return false;
        }
    }

    @Override
    public void registerPatient() {
        new PatientApi().registerPatient(mPatient, new DefaultResponseCallbackListener() {
            @Override
            public void onResponse() {
                mPatientInfoView.startPatientDashbordActivity(mPatient);
                mPatientInfoView.finishPatientInfoActivity();
            }

            @Override
            public void onErrorResponse(String errorMessage) {
                mPatientInfoView.setProgressBarVisibility(false);
            }
        });
    }

    @Override
    public void updatePatient(Patient patient) {
        new PatientApi().updatePatient(patient, new DefaultResponseCallbackListener() {
            @Override
            public void onResponse() {
                mPatientInfoView.finishPatientInfoActivity();
            }

            @Override
            public void onErrorResponse(String errorMessage) {
                mPatientInfoView.setProgressBarVisibility(false);
            }
        });
    }

    public void findSimilarPatients(final Patient patient){
        if (NetworkUtils.isOnline()) {
            RestApi apiService = RestServiceBuilder.createService(RestApi.class);
            Call<Results<Module>> moduleCall = apiService.getModules(ApplicationConstants.API.FULL);
            moduleCall.enqueue(new Callback<Results<Module>>() {
                @Override
                public void onResponse(Call<Results<Module>> call, Response<Results<Module>> response) {
                    if(response.isSuccessful()){
                        if(ModuleUtils.isRegistrationCore1_7orAbove(response.body().getResults())){
                            fetchSimilarPatientsFromServer(patient);
                        } else {
                            fetchSimilarPatientAndCalculateLocally(patient);
                                                    }
                    } else {
                        fetchSimilarPatientAndCalculateLocally(patient);
                    }
                }

                @Override
                public void onFailure(Call<Results<Module>> call, Throwable t) {
                    mPatientInfoView.setProgressBarVisibility(false);
                    ToastUtil.error(t.getMessage());
                }
            });
        } else {
            List<Patient> similarPatient = new PatientComparator().findSimilarPatient(new PatientDAO().getAllPatients(), patient);
            if(!similarPatient.isEmpty()){
                mPatientInfoView.showSimilarPatientDialog(similarPatient, patient);
            } else {
                registerPatient();
            }
        }
    }

    private void fetchSimilarPatientAndCalculateLocally(final Patient patient) {
        RestApi apiService = RestServiceBuilder.createService(RestApi.class);
        Call<Results<Patient>> call = apiService.getPatients(patient.getPerson().getName().getGivenName(), ApplicationConstants.API.FULL);
        call.enqueue(new Callback<Results<Patient>>() {
            @Override
            public void onResponse(Call<Results<Patient>> call, Response<Results<Patient>> response) {
                if(response.isSuccessful()){
                    List<Patient> patientList = response.body().getResults();
                    if(!patientList.isEmpty()){
                        List<Patient> similarPatient = new PatientComparator().findSimilarPatient(patientList, patient);
                        if (!similarPatient.isEmpty()) {
                            mPatientInfoView.showSimilarPatientDialog(similarPatient, patient);
                            mPatientInfoView.showUpgradeRegistrationModuleInfo();
                        }
                    } else {
                        registerPatient();
                    }
                } else {
                    mPatientInfoView.setProgressBarVisibility(false);
                    ToastUtil.error(response.message());
                }
            }

            @Override
            public void onFailure(Call<Results<Patient>> call, Throwable t) {
                mPatientInfoView.setProgressBarVisibility(false);
                ToastUtil.error(t.getMessage());
            }
        });
    }

    private void fetchSimilarPatientsFromServer(final Patient patient) {
        RestApi apiService = RestServiceBuilder.createService(RestApi.class);
        Call<Results<Patient>> call = apiService.getSimilarPatients(patient.toMap());
        call.enqueue(new Callback<Results<Patient>>() {
            @Override
            public void onResponse(Call<Results<Patient>> call, Response<Results<Patient>> response) {
                if(response.isSuccessful()){
                    List<Patient> similarPatients = response.body().getResults();
                    if(!similarPatients.isEmpty()){
                        mPatientInfoView.showSimilarPatientDialog(similarPatients, patient);
                    } else {
                        registerPatient();
                    }
                } else {
                    mPatientInfoView.setProgressBarVisibility(false);
                    ToastUtil.error(response.message());
                }
            }

            @Override
            public void onFailure(Call<Results<Patient>> call, Throwable t) {
                mPatientInfoView.setProgressBarVisibility(false);
                ToastUtil.error(t.getMessage());
            }
        });
    }

}
