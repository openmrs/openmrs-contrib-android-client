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

package org.openmrs.mobile.activities.registerpatient;

import android.view.View;

import org.openmrs.mobile.api.RestApi;
import org.openmrs.mobile.api.RestServiceBuilder;
import org.openmrs.mobile.api.retrofit.PatientApi;
import org.openmrs.mobile.listeners.retrofit.DefaultResponseCallbackListener;
import org.openmrs.mobile.models.retrofit.Patient;
import org.openmrs.mobile.models.retrofit.Results;
import org.openmrs.mobile.utilities.NetworkUtils;
import org.openmrs.mobile.utilities.PatientComparator;
import org.openmrs.mobile.utilities.StringUtils;
import org.openmrs.mobile.utilities.ToastUtil;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SyncPatientPresenter implements RegisterPatientContract.Presenter {

    private final RegisterPatientContract.View mRegisterPatientView;

    private Patient mPatient;

    public SyncPatientPresenter(RegisterPatientContract.View mRegisterPatientView) {
        this.mRegisterPatientView = mRegisterPatientView;
        this.mRegisterPatientView.setPresenter(this);
    }
    
    @Override
    public void start(){}

    @Override
    public void confirm(Patient patient) {
        if(validate(patient)) {
            mRegisterPatientView.setProgressBarVisibility(View.VISIBLE);
            mRegisterPatientView.hideSoftKeys();
            findSimilarPatients(patient);
        }
        else {
            mRegisterPatientView.scrollToTop();
        }
    }

    @Override
    public void finishRegisterActivity() {
        mRegisterPatientView.finishRegisterActivity();
    }

    private boolean validate(Patient patient) {

        boolean ferr=false, lerr=false, doberr=false, gerr=false, adderr=false;
        mRegisterPatientView.setErrorsVisibility(ferr, lerr, doberr, gerr, adderr);

        // Validate names
        if(StringUtils.isBlank(patient.getPerson().getName().getGivenName())) {
            ferr=true;
        }
        if(StringUtils.isBlank(patient.getPerson().getName().getFamilyName())) {
            lerr=true;
        }

        // Validate date of birth
        if(StringUtils.isBlank(patient.getPerson().getBirthdate())) {
            doberr=true;
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

        // Validate gender
        if (StringUtils.isBlank(patient.getPerson().getGender())) {
            gerr=true;
        }

        boolean result = !ferr && !lerr && !doberr && !adderr && !gerr;
        if (result) {
            mPatient = patient;
            return true;
        }
        else {
            mRegisterPatientView.setErrorsVisibility(ferr, lerr, doberr, adderr, gerr);
            return false;
        }
    }

    @Override
    public void registerPatient() {
        new PatientApi().registerPatient(mPatient, new DefaultResponseCallbackListener() {
            @Override
            public void onResponse() {
                mRegisterPatientView.startPatientDashbordActivity(mPatient);
                mRegisterPatientView.finishRegisterActivity();
            }

            @Override
            public void onErrorResponse() {
                if (!NetworkUtils.isOnline()) {
                    mRegisterPatientView.finishRegisterActivity();
                    mRegisterPatientView.startPatientDashbordActivity(mPatient);
                }
            }
        });
    }

    public void findSimilarPatients(final Patient patient){
        RestApi apiService = RestServiceBuilder.createService(RestApi.class);
        Call<Results<Patient>> call = apiService.getPatients(patient.getPerson().getName().getNameString(), PatientApi.FULL_REPRESENTATION);
        call.enqueue(new Callback<Results<Patient>>() {
            @Override
            public void onResponse(Call<Results<Patient>> call, Response<Results<Patient>> response) {
                if (response.isSuccessful()) {
                    List<Patient> patientsList = new PatientComparator().findSimilarPatient(response.body().getResults(), patient);
                    if (!patientsList.isEmpty()) {
                        mRegisterPatientView.showSimilarPatientDialog(patientsList, patient);
                    } else {
                        registerPatient();
                    }
                } else {
                    mRegisterPatientView.setProgressBarVisibility(View.GONE);
                    ToastUtil.error(response.message());
                }
            }

            @Override
            public void onFailure(Call<Results<Patient>> call, Throwable t) {
                mRegisterPatientView.setProgressBarVisibility(View.GONE);
                ToastUtil.error(t.toString());
            }
        });
    }

}
