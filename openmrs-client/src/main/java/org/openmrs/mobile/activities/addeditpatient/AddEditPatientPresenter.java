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

import org.openmrs.mobile.activities.BasePresenter;
import org.openmrs.mobile.api.RestApi;
import org.openmrs.mobile.api.RestServiceBuilder;
import org.openmrs.mobile.api.retrofit.PatientApi;
import org.openmrs.mobile.dao.PatientDAO;
import org.openmrs.mobile.listeners.retrofit.DefaultResponseCallbackListener;
import org.openmrs.mobile.models.Module;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.models.PersonName;
import org.openmrs.mobile.models.Results;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.ModuleUtils;
import org.openmrs.mobile.utilities.NetworkUtils;
import org.openmrs.mobile.utilities.PatientComparator;
import org.openmrs.mobile.utilities.StringUtils;
import org.openmrs.mobile.utilities.ToastUtil;
import org.openmrs.mobile.utilities.ViewUtils;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddEditPatientPresenter extends BasePresenter implements AddEditPatientContract.Presenter {

    private final AddEditPatientContract.View mPatientInfoView;

    private PatientApi patientApi;
    private RestApi restApi;
    private Patient mPatient;
    private String patientToUpdateId;
    private List<String> mCountries;
    private boolean registeringPatient = false;

    public AddEditPatientPresenter(AddEditPatientContract.View mPatientInfoView,
                                   List<String> countries,
                                   String patientToUpdateId) {
        this.mPatientInfoView = mPatientInfoView;
        this.mPatientInfoView.setPresenter(this);
        this.mCountries = countries;
        this.patientToUpdateId = patientToUpdateId;
        this.patientApi = new PatientApi();
        this.restApi = RestServiceBuilder.createService(RestApi.class);
    }

    public AddEditPatientPresenter(AddEditPatientContract.View mPatientInfoView, PatientApi patientApi,
                                   Patient mPatient, String patientToUpdateId,
                                   List<String> mCountries, RestApi restApi) {
        this.mPatientInfoView = mPatientInfoView;
        this.patientApi = patientApi;
        this.mPatient = mPatient;
        this.patientToUpdateId = patientToUpdateId;
        this.mCountries = mCountries;
        this.restApi = restApi;
        this.mPatientInfoView.setPresenter(this);
    }

    @Override
    public void subscribe(){
        // This method is intentionally empty
    }

    @Override
    public Patient getPatientToUpdate() {
        return new PatientDAO().findPatientByID(patientToUpdateId);
    }

    @Override
    public void confirmRegister(Patient patient) {
        if(!registeringPatient && validate(patient)) {
            mPatientInfoView.setProgressBarVisibility(true);
            mPatientInfoView.hideSoftKeys();
            registeringPatient = true;
            findSimilarPatients(patient);
        }
        else {
            mPatientInfoView.scrollToTop();
        }
    }

    @Override
    public void confirmUpdate(Patient patient) {
        if (!registeringPatient && validate(patient)) {
            mPatientInfoView.setProgressBarVisibility(true);
            mPatientInfoView.hideSoftKeys();
            registeringPatient = true;
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

        boolean givenNameError = false;
        boolean familyNameError = false;
        boolean dateOfBirthError = false;
        boolean genderError = false;
        boolean addressError = false;
        boolean countryError = false;

        mPatientInfoView.setErrorsVisibility(givenNameError, familyNameError, dateOfBirthError, genderError, addressError, countryError);

        // Validate names
        PersonName currentPersonName = patient.getPerson().getName();
        
        if (StringUtils.isBlank(currentPersonName.getGivenName())
                || !ViewUtils.validateText(currentPersonName.getGivenName(), ViewUtils.ILLEGAL_CHARACTERS)) {
            givenNameError = true;
        }

        // Middle name can be left empty
        if (!ViewUtils.validateText(currentPersonName.getMiddleName(), ViewUtils.ILLEGAL_CHARACTERS)) {
            givenNameError = true;
        }

        if (StringUtils.isBlank(currentPersonName.getFamilyName())
                || !ViewUtils.validateText(currentPersonName.getFamilyName(), ViewUtils.ILLEGAL_CHARACTERS)) {
            familyNameError = true;
        }

        // Validate date of birth
        if(StringUtils.isBlank(patient.getPerson().getBirthdate())) {
            dateOfBirthError = true;
        }

        // Validate address
        String patientAddress1 = patient.getPerson().getAddress().getAddress1();
        String patientAddress2 = patient.getPerson().getAddress().getAddress2();

        if ((StringUtils.isBlank(patientAddress1)
                && StringUtils.isBlank(patientAddress2)
                && StringUtils.isBlank(patient.getPerson().getAddress().getCityVillage())
                && StringUtils.isBlank(patient.getPerson().getAddress().getStateProvince())
                && StringUtils.isBlank(patient.getPerson().getAddress().getCountry())
                && StringUtils.isBlank(patient.getPerson().getAddress().getPostalCode()))
                || !ViewUtils.validateText(patientAddress1, ViewUtils.ILLEGAL_ADDRESS_CHARACTERS)
                || !ViewUtils.validateText(patientAddress2, ViewUtils.ILLEGAL_ADDRESS_CHARACTERS)) {
            addressError = true;
        }

        if (!StringUtils.isBlank(patient.getPerson().getAddress().getCountry()) && !mCountries.contains(patient.getPerson().getAddress().getCountry())) {
            countryError = true;
        }

        // Validate gender
        if (StringUtils.isBlank(patient.getPerson().getGender())) {
            genderError=true;
        }

        boolean result = !givenNameError && !familyNameError && !dateOfBirthError && !addressError && !countryError && !genderError;
        if (result) {
            mPatient = patient;
            return true;
        }
        else {
            mPatientInfoView.setErrorsVisibility(givenNameError, familyNameError, dateOfBirthError, addressError, countryError, genderError);
            return false;
        }
    }

    @Override
    public void registerPatient() {
        patientApi.registerPatient(mPatient, new DefaultResponseCallbackListener() {
            @Override
            public void onResponse() {
                mPatientInfoView.startPatientDashbordActivity(mPatient);
                mPatientInfoView.finishPatientInfoActivity();
            }

            @Override
            public void onErrorResponse(String errorMessage) {
                registeringPatient = false;
                mPatientInfoView.setProgressBarVisibility(false);
            }
        });
    }

    @Override
    public void updatePatient(Patient patient) {
        patientApi.updatePatient(patient, new DefaultResponseCallbackListener() {
            @Override
            public void onResponse() {
                mPatientInfoView.finishPatientInfoActivity();
            }

            @Override
            public void onErrorResponse(String errorMessage) {
                registeringPatient = false;
                mPatientInfoView.setProgressBarVisibility(false);
            }
        });
    }

    public void findSimilarPatients(final Patient patient){
        if (NetworkUtils.isOnline()) {
            Call<Results<Module>> moduleCall = restApi.getModules(ApplicationConstants.API.FULL);
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
                    registeringPatient = false;
                    mPatientInfoView.setProgressBarVisibility(false);
                    ToastUtil.error(t.getMessage());
                }
            });
        } else {
            List<Patient> similarPatient = new PatientComparator().findSimilarPatient(new PatientDAO().getAllPatients().toBlocking().first(), patient);
            if(!similarPatient.isEmpty()){
                mPatientInfoView.showSimilarPatientDialog(similarPatient, patient);
            } else {
                registerPatient();
            }
        }
    }

    private void fetchSimilarPatientAndCalculateLocally(final Patient patient) {
        Call<Results<Patient>> call = restApi.getPatients(patient.getPerson().getName().getGivenName(), ApplicationConstants.API.FULL);
        call.enqueue(new Callback<Results<Patient>>() {
            @Override
            public void onResponse(Call<Results<Patient>> call, Response<Results<Patient>> response) {
                registeringPatient = false;
                if(response.isSuccessful()){
                    List<Patient> patientList = response.body().getResults();
                    if(!patientList.isEmpty()){
                        List<Patient> similarPatient = new PatientComparator().findSimilarPatient(patientList, patient);
                        if (!similarPatient.isEmpty()) {
                            mPatientInfoView.showSimilarPatientDialog(similarPatient, patient);
                            mPatientInfoView.showUpgradeRegistrationModuleInfo();
                        } else {
                            registerPatient();
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
                registeringPatient = false;
                mPatientInfoView.setProgressBarVisibility(false);
                ToastUtil.error(t.getMessage());
            }
        });
    }

    private void fetchSimilarPatientsFromServer(final Patient patient) {
        Call<Results<Patient>> call = restApi.getSimilarPatients(patient.toMap());
        call.enqueue(new Callback<Results<Patient>>() {
            @Override
            public void onResponse(Call<Results<Patient>> call, Response<Results<Patient>> response) {
                registeringPatient = false;
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
                registeringPatient = false;
                mPatientInfoView.setProgressBarVisibility(false);
                ToastUtil.error(t.getMessage());
            }
        });
    }

    @Override
    public boolean isRegisteringPatient() {
        return registeringPatient;
    }

}
