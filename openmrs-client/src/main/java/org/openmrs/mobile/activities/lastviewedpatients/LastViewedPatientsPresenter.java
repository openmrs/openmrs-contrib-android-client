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

package org.openmrs.mobile.activities.lastviewedpatients;

import android.support.annotation.NonNull;

import org.openmrs.mobile.api.RestApi;
import org.openmrs.mobile.api.RestServiceBuilder;
import org.openmrs.mobile.dao.PatientDAO;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.models.Results;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.StringUtils;

import java.util.LinkedList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LastViewedPatientsPresenter implements LastViewedPatientsContract.Presenter{

    // View
    @NonNull
    private final LastViewedPatientsContract.View mLastViewedPatientsView;

    private PatientDAO patientDAO;
    private RestApi restApi;

    private String mQuery;
    private String mLastQuery = "QUERY";

    public LastViewedPatientsPresenter(@NonNull LastViewedPatientsContract.View mLastViewedPatientsView) {
        this.mLastViewedPatientsView = mLastViewedPatientsView;
        this.mLastViewedPatientsView.setPresenter(this);
        this.restApi = RestServiceBuilder.createService(RestApi.class);
        this.patientDAO = new PatientDAO();
    }

    public LastViewedPatientsPresenter(@NonNull LastViewedPatientsContract.View mLastViewedPatientsView,
                                       RestApi restApi, PatientDAO patientDAO) {
        this.mLastViewedPatientsView = mLastViewedPatientsView;
        this.mLastViewedPatientsView.setPresenter(this);
        this.restApi = restApi;
        this.patientDAO = patientDAO;
    }

    @Override
    public void start() {
        updateLastViewedList();
    }

    public void updateLastViewedList() {
        if (!mLastViewedPatientsView.isRefreshing()) {
            mLastViewedPatientsView.setSpinnerVisibility(true);
        }
        mLastViewedPatientsView.enableSwipeRefresh(false);
        mLastViewedPatientsView.setEmptyListVisibility(false);
        mLastViewedPatientsView.setListVisibility(false);

        Call<Results<Patient>> call = restApi.getLastViewedPatients();
        call.enqueue(new Callback<Results<Patient>>() {
            @Override
            public void onResponse(Call<Results<Patient>> call, Response<Results<Patient>> response) {
                mLastViewedPatientsView.setSpinnerVisibility(false);
                if (response.isSuccessful()) {
                    mLastViewedPatientsView.updateList(filterNotDownloadedPatients(response.body().getResults()));
                    mLastViewedPatientsView.setListVisibility(true);
                    mLastViewedPatientsView.setEmptyListVisibility(false);
                }
                else {
                    mLastViewedPatientsView.setListVisibility(false);
                    mLastViewedPatientsView.setEmptyListText(response.message());
                    mLastViewedPatientsView.setEmptyListVisibility(true);
                }
                mLastViewedPatientsView.stopRefreshing();
                mLastViewedPatientsView.enableSwipeRefresh(true);
            }

            @Override
            public void onFailure(Call<Results<Patient>> call, Throwable t) {
                mLastViewedPatientsView.showErrorToast(t.getMessage());
                mLastViewedPatientsView.setSpinnerVisibility(false);
                mLastViewedPatientsView.setListVisibility(false);
                mLastViewedPatientsView.stopRefreshing();
                mLastViewedPatientsView.enableSwipeRefresh(true);
            }
        });

    }

    private List<Patient> filterNotDownloadedPatients(List<Patient> patients) {
        List<Patient> newPatientList = new LinkedList<>();
        for (Patient patient: patients){
            if(!patientDAO.isUserAlreadySaved(patient.getUuid())){
                newPatientList.add(patient);
            }
        }
        return newPatientList;
    }

    public void updateLastViewedList(String query) {
        mQuery = query;
        if (query.isEmpty() && !mLastQuery.isEmpty()) {
            updateLastViewedList();
        }
    }

    public void findPatients(String query) {
        if (!mLastViewedPatientsView.isRefreshing()) {
            mLastViewedPatientsView.setSpinnerVisibility(true);
        }
        mLastViewedPatientsView.setEmptyListVisibility(false);
        mLastQuery = query;

        Call<Results<Patient>> call = restApi.getPatients(query, ApplicationConstants.API.FULL);
        call.enqueue(new Callback<Results<Patient>>() {
            @Override
            public void onResponse(Call<Results<Patient>> call, Response<Results<Patient>> response) {
                mLastViewedPatientsView.setSpinnerVisibility(false);
                if (response.isSuccessful()) {
                    mLastViewedPatientsView.updateList(filterNotDownloadedPatients(response.body().getResults()));
                    mLastViewedPatientsView.setListVisibility(true);
                    mLastViewedPatientsView.setEmptyListVisibility(false);
                }
                else {
                    mLastViewedPatientsView.setListVisibility(false);
                    mLastViewedPatientsView.setEmptyListText(response.message());
                    mLastViewedPatientsView.setEmptyListVisibility(true);
                }
                mLastViewedPatientsView.stopRefreshing();
            }
            @Override
            public void onFailure(Call<Results<Patient>> call, Throwable t) {
                mLastViewedPatientsView.showErrorToast(t.getMessage());
                mLastViewedPatientsView.setSpinnerVisibility(false);
                mLastViewedPatientsView.setListVisibility(false);
                mLastViewedPatientsView.stopRefreshing();
            }
        });
    }

    @Override
    public void refresh() {
        if (!StringUtils.isBlank(mQuery) && StringUtils.notEmpty(mQuery)) {
            findPatients(mQuery);
        }
        else {
            updateLastViewedList();
        }
    }

}
