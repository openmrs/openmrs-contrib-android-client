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

import android.content.Context;

import androidx.annotation.NonNull;

import com.openmrs.android_sdk.library.dao.PatientDAO;
import com.openmrs.android_sdk.library.models.ConceptAnswers;
import com.openmrs.android_sdk.library.models.Module;
import com.openmrs.android_sdk.library.models.Patient;
import com.openmrs.android_sdk.library.models.PersonName;
import com.openmrs.android_sdk.library.models.Results;
import com.openmrs.android_sdk.utilities.ApplicationConstants;
import com.openmrs.android_sdk.utilities.NetworkUtils;
import com.openmrs.android_sdk.utilities.StringUtils;
import com.openmrs.android_sdk.utilities.ToastUtil;
import com.google.android.libraries.places.api.net.PlacesClient;

import org.jetbrains.annotations.Nullable;
import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.BasePresenter;
import com.openmrs.android_sdk.library.api.RestApi;
import com.openmrs.android_sdk.library.api.RestServiceBuilder;
import com.openmrs.android_sdk.library.api.promise.SimpleDeferredObject;
import com.openmrs.android_sdk.library.api.repository.PatientRepository;
import com.openmrs.android_sdk.library.listeners.retrofitcallbacks.DefaultResponseCallback;
import com.openmrs.android_sdk.library.listeners.retrofitcallbacks.PatientDeferredResponseCallback;
import com.openmrs.android_sdk.library.listeners.retrofitcallbacks.PatientResponseCallback;
import com.openmrs.android_sdk.library.listeners.retrofitcallbacks.VisitsResponseCallback;
import org.openmrs.mobile.utilities.ModuleUtils;
import org.openmrs.mobile.utilities.PatientComparator;
import org.openmrs.mobile.utilities.ViewUtils;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddEditPatientPresenter extends BasePresenter implements AddEditPatientContract.Presenter {
    private final AddEditPatientContract.View mPatientInfoView;
    private boolean isPatientUnidentified = false;
    private PatientRepository patientRepository;
    private RestApi restApi;
    private Patient mPatient;
    private String patientToUpdateId;
    private List<String> mCountries;
    private boolean registeringPatient = false;
    private PlacesClient placesClient;

    public AddEditPatientPresenter(AddEditPatientContract.View mPatientInfoView,
                                   List<String> countries,
                                   String patientToUpdateId,
                                   PlacesClient placesClient,
                                   Context appContext) {
        this.mPatientInfoView = mPatientInfoView;
        this.mPatientInfoView.setPresenter(this);
        this.mCountries = countries;
        this.patientToUpdateId = patientToUpdateId;
        this.patientRepository = new PatientRepository();
        this.restApi = RestServiceBuilder.createService(RestApi.class);
        this.placesClient = placesClient;
    }

    public AddEditPatientPresenter(AddEditPatientContract.View mPatientInfoView, PatientRepository patientRepository,
                                   Patient mPatient, String patientToUpdateId,
                                   List<String> mCountries, RestApi restApi) {
        this.mPatientInfoView = mPatientInfoView;
        this.patientRepository = patientRepository;
        this.mPatient = mPatient;
        this.patientToUpdateId = patientToUpdateId;
        this.mCountries = mCountries;
        this.restApi = restApi;
        this.mPatientInfoView.setPresenter(this);
    }

    @Override
    public void subscribe() {
        
        // This method is intentionally empty
    }

    @Override
    public Patient getPatientToUpdate() {
        return new PatientDAO().findPatientByID(patientToUpdateId);
    }

    @Override
    public void confirmRegister(Patient patient, boolean isPatientUnidentified) {
        this.isPatientUnidentified = isPatientUnidentified;
        if (!registeringPatient && validate(patient)) {
            mPatientInfoView.setProgressBarVisibility(true);
            mPatientInfoView.hideSoftKeys();
            registeringPatient = true;
            if (!isPatientUnidentified) {
                findSimilarPatients(patient);
            } else {
                registerPatient();
            }
        } else {
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
        boolean countryNull = false;
        boolean stateError = false;
        boolean cityError = false;
        boolean postalError = false;

        if (!isPatientUnidentified) {

            mPatientInfoView
                .setErrorsVisibility(givenNameError, familyNameError, dateOfBirthError, genderError, addressError, countryError, countryNull, stateError, cityError, postalError);

            // Validate names
            PersonName currentPersonName = patient.getName();

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

            // Validate address
            String patientAddress1 = patient.getAddress().getAddress1();
            String patientAddress2 = patient.getAddress().getAddress2();

            if ((StringUtils.isBlank(patientAddress1)
                && StringUtils.isBlank(patientAddress2)
                || !ViewUtils.validateText(patientAddress1, ViewUtils.ILLEGAL_ADDRESS_CHARACTERS)
                || !ViewUtils.validateText(patientAddress2, ViewUtils.ILLEGAL_ADDRESS_CHARACTERS))) {
                addressError = true;
            }

            if (!StringUtils.isBlank(patient.getAddress().getCountry()) && !mCountries.contains(patient.getAddress().getCountry())) {
                countryError = true;
            }

            if (StringUtils.isBlank(patient.getAddress().getCountry())) {
                countryNull = true;
            }
            if (StringUtils.isBlank(patient.getAddress().getStateProvince())) {
                stateError = true;
            }
            if (StringUtils.isBlank(patient.getAddress().getCityVillage())) {
                cityError = true;
            }
            if (StringUtils.isBlank(patient.getAddress().getPostalCode())) {
                postalError = true;
            }
        }

        // Validate gender
        if (StringUtils.isBlank(patient.getGender())) {
            genderError = true;
        }

        // Validate date of birth
        if (StringUtils.isBlank(patient.getBirthdate())) {
            dateOfBirthError = true;
        }

        boolean result = !givenNameError && !familyNameError && !dateOfBirthError && !addressError && !countryError && !genderError;
        if (result) {
            mPatient = patient;
            return true;
        } else {
            mPatientInfoView
                .setErrorsVisibility(givenNameError, familyNameError, dateOfBirthError, addressError, countryError, genderError, countryNull, stateError, cityError, postalError);
            return false;
        }
    }

    @Override
    public void registerPatient() {
        patientRepository.registerPatient(mPatient, new PatientDeferredResponseCallback() {
            @Override
            public void onNotifyResponse(@Nullable String notifyMessage) {
                mPatientInfoView.startPatientDashbordActivity(mPatient);
                mPatientInfoView.finishPatientInfoActivity();
                ToastUtil.notify(notifyMessage);
            }

            @Override
            public void onErrorResponse(@Nullable String errorMessage, @Nullable SimpleDeferredObject<Patient> errorResponse) {

            }

            @Override
            public void onResponse(@Nullable SimpleDeferredObject<Patient> response) {

            }

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
        patientRepository.updatePatient(patient, new DefaultResponseCallback() {
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

    @Override
    public PlacesClient getPlaces() {
        return placesClient;
    }

    @Override
    public void getCauseOfDeathGlobalID() {
        patientRepository.getCauseOfDeathGlobalID(new VisitsResponseCallback() {
            @Override
            public void onSuccess(@Nullable String response) {
                if (response.length() == ApplicationConstants.UUID_LENGTH) {
                    getConceptCauseOfDeath(response);
                } else {
                    mPatientInfoView.cannotMarkDeceased(R.string.mark_patient_deceased_invalid_uuid);
                }
            }

            @Override
            public void onFailure(@Nullable String errorMessage) {
                mPatientInfoView.cannotMarkDeceased(errorMessage);
            }
        });
    }

    // left as it is due to private nature of the method just is being used from here only

    private void getConceptCauseOfDeath(String uuid) {
        restApi.getConceptFromUUID(uuid).enqueue(new Callback<ConceptAnswers>() {
            @Override
            public void onResponse(Call<ConceptAnswers> call, Response<ConceptAnswers> response) {
                if (response.isSuccessful()) {
                    if (response.body().getAnswers().size() != 0) {
                        mPatientInfoView.updateCauseOfDeathSpinner(response.body());
                    } else {
                        mPatientInfoView.cannotMarkDeceased(R.string.mark_patient_deceased_concept_has_no_answer);
                    }
                } else {
                    mPatientInfoView.cannotMarkDeceased(ApplicationConstants.EMPTY_STRING);
                }
            }

            @Override
            public void onFailure(Call<ConceptAnswers> call, Throwable t) {
                mPatientInfoView.cannotMarkDeceased(t.getMessage());
            }
        });
    }

    public void findSimilarPatients(final Patient patient) {
        if (NetworkUtils.isOnline()) {
            Call<Results<Module>> moduleCall = restApi.getModules(ApplicationConstants.API.FULL);
            moduleCall.enqueue(new Callback<Results<Module>>() {
                @Override
                public void onResponse(@NonNull Call<Results<Module>> call, @NonNull Response<Results<Module>> response) {
                    if (response.isSuccessful()) {
                        if (ModuleUtils.isRegistrationCore1_7orAbove(response.body().getResults())) {
                            fetchSimilarPatientsFromServer(patient);
                        } else {
                            fetchSimilarPatientAndCalculateLocally(patient);
                        }
                    } else {
                        fetchSimilarPatientAndCalculateLocally(patient);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Results<Module>> call, @NonNull Throwable t) {
                    registeringPatient = false;
                    mPatientInfoView.setProgressBarVisibility(false);
                    ToastUtil.error(t.getMessage());
                }
            });
        } else {
            List<Patient> similarPatient = new PatientComparator().findSimilarPatient(new PatientDAO().getAllPatients().toBlocking().first(), patient);
            if (!similarPatient.isEmpty()) {
                mPatientInfoView.showSimilarPatientDialog(similarPatient, patient);
            } else {
                registerPatient();
            }
        }
    }

    private void fetchSimilarPatientAndCalculateLocally(final Patient patient) {
        patientRepository.fetchSimilarPatientAndCalculateLocally(patient, new PatientResponseCallback() {
            @Override
            public void onResponse(@Nullable Results<Patient> patientResults) {
                registeringPatient = false;
                List<Patient> patientList = patientResults.getResults();
                if (!patientList.isEmpty()) {
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
            }

            @Override
            public void onErrorResponse(String errorMessage) {
                registeringPatient = false;
                mPatientInfoView.setProgressBarVisibility(false);
                ToastUtil.error(errorMessage);
            }
        });
    }

    private void fetchSimilarPatientsFromServer(final Patient patient) {
        patientRepository.fetchSimilarPatientsFromServer(patient, new PatientResponseCallback() {
            @Override
            public void onResponse(@Nullable Results<Patient> patientResults) {
                registeringPatient = false;
                List<Patient> similarPatients = patientResults.getResults();
                if (!similarPatients.isEmpty()) {
                    mPatientInfoView.showSimilarPatientDialog(similarPatients, patient);
                } else {
                    registerPatient();
                }
            }

            @Override
            public void onErrorResponse(String errorMessage) {
                registeringPatient = false;
                mPatientInfoView.setProgressBarVisibility(false);
                ToastUtil.error(errorMessage);
            }
        });
    }

    @Override
    public boolean isRegisteringPatient() {
        return registeringPatient;
    }
}
