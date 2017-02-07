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

import android.os.Bundle;
import android.support.annotation.NonNull;

import org.openmrs.mobile.activities.BasePresenter;
import org.openmrs.mobile.api.RestApi;
import org.openmrs.mobile.api.RestServiceBuilder;
import org.openmrs.mobile.dao.PatientDAO;
import org.openmrs.mobile.models.Link;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.models.Results;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.StringUtils;
import org.openmrs.mobile.utilities.ToastUtil;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LastViewedPatientsPresenter extends BasePresenter implements LastViewedPatientsContract.Presenter{

    private static final int MIN_NUMBER_OF_PATIENTS_TO_SHOW = 7;
    // View
    @NonNull
    private final LastViewedPatientsContract.View mLastViewedPatientsView;

    private PatientDAO patientDAO;
    private RestApi restApi;

    private String mQuery;
    private String lastQuery = "";
    private int limit = 15;
    private int startIndex = 0;
    private boolean isDownloadedAll = false;

    public LastViewedPatientsPresenter(@NonNull LastViewedPatientsContract.View mLastViewedPatientsView, String lastQuery) {
        this.mLastViewedPatientsView = mLastViewedPatientsView;
        this.mLastViewedPatientsView.setPresenter(this);
        this.restApi = RestServiceBuilder.createService(RestApi.class);
        this.patientDAO = new PatientDAO();
        this.lastQuery = lastQuery;
    }

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
    public void subscribe() {
        updateLastViewedList();
    }

    public void updateLastViewedList() {
        startIndex = 0;
        setViewBeforePatientDownload();
        updateLastViewedList(new ArrayList<>());
    }

    private void updateLastViewedList(List<Patient> patients){
        Call<Results<Patient>> call = restApi.getLastViewedPatients(limit, startIndex);
        call.enqueue(new Callback<Results<Patient>>() {
            @Override
            public void onResponse(Call<Results<Patient>> call, Response<Results<Patient>> response) {
                if (response.isSuccessful()) {
                    setStartIndexIfMorePatientsAvailable(response.body().getLinks());
                    patients.addAll(filterNotDownloadedPatients(response.body().getResults()));
                    if (patients.size() < MIN_NUMBER_OF_PATIENTS_TO_SHOW && !isDownloadedAll) {
                        updateLastViewedList(patients);
                    } else {
                        mLastViewedPatientsView.updateList(patients);
                        setViewAfterPatientDownloadSuccess();
                        mLastViewedPatientsView.enableSwipeRefresh(true);
                        if(!isDownloadedAll){
                            mLastViewedPatientsView.showRecycleViewProgressBar(true);
                        }
                    }
                }
                else {
                    setViewAfterPatientDownloadError(response.message());
                    mLastViewedPatientsView.enableSwipeRefresh(true);
                }
            }

            @Override
            public void onFailure(Call<Results<Patient>> call, Throwable t) {
                setViewAfterPatientDownloadError(t.getMessage());
                mLastViewedPatientsView.enableSwipeRefresh(true);
            }
        });
    }

    public void findPatients(String query) {
        setViewBeforePatientDownload();
        lastQuery = query;
        Call<Results<Patient>> call = restApi.getPatients(query, ApplicationConstants.API.FULL);
        call.enqueue(new Callback<Results<Patient>>() {
            @Override
            public void onResponse(Call<Results<Patient>> call, Response<Results<Patient>> response) {
                if (response.isSuccessful()) {
                    mLastViewedPatientsView.updateList(filterNotDownloadedPatients(response.body().getResults()));
                    setViewAfterPatientDownloadSuccess();
                }
                else {
                    setViewAfterPatientDownloadError(response.message());
                }
            }
            @Override
            public void onFailure(Call<Results<Patient>> call, Throwable t) {
                setViewAfterPatientDownloadError(t.getMessage());
            }
        });
    }

    @Override
    public void loadMorePatients() {
        if (!isDownloadedAll) {
            Call<Results<Patient>> call = restApi.getLastViewedPatients(limit, startIndex);
            call.enqueue(new Callback<Results<Patient>>() {
                @Override
                public void onResponse(Call<Results<Patient>> call, Response<Results<Patient>> response) {
                    if (response.isSuccessful()) {
                        List<Patient> patients = response.body().getResults();
                        mLastViewedPatientsView.showRecycleViewProgressBar(false);
                        mLastViewedPatientsView.addPatientsToList(filterNotDownloadedPatients(patients));
                        setStartIndexIfMorePatientsAvailable(response.body().getLinks());
                        if(!isDownloadedAll){
                            mLastViewedPatientsView.showRecycleViewProgressBar(true);
                        }
                    }
                    else {
                        ToastUtil.error(response.message());
                        mLastViewedPatientsView.showRecycleViewProgressBar(false);
                    }
                }

                @Override
                public void onFailure(Call<Results<Patient>> call, Throwable t) {
                    ToastUtil.error(t.getMessage());
                    mLastViewedPatientsView.showRecycleViewProgressBar(false);
                }
            });
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(ApplicationConstants.BundleKeys.PATIENTS_START_INDEX, startIndex);
    }

    @Override
    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }

    private void setStartIndexIfMorePatientsAvailable(List<Link> links) {
        boolean linkFound = false;
        for (Link link : links) {
            if("next".equals(link.getRel())){
                startIndex += limit;
                isDownloadedAll = false;
                linkFound = true;
            }
        }
        if(!linkFound){
            isDownloadedAll = true;
        }
    }

    private void setViewBeforePatientDownload() {
        if (!mLastViewedPatientsView.isRefreshing()) {
            mLastViewedPatientsView.setProgressBarVisibility(true);
        }
        mLastViewedPatientsView.enableSwipeRefresh(false);
        mLastViewedPatientsView.setEmptyListVisibility(false);
        mLastViewedPatientsView.setListVisibility(false);
    }

    private void setViewAfterPatientDownloadError(String errorMessage) {
        mLastViewedPatientsView.setProgressBarVisibility(false);
        mLastViewedPatientsView.setListVisibility(false);
        mLastViewedPatientsView.setEmptyListText(errorMessage);
        mLastViewedPatientsView.setEmptyListVisibility(true);
        mLastViewedPatientsView.stopRefreshing();
    }

    private void setViewAfterPatientDownloadSuccess() {
        mLastViewedPatientsView.setProgressBarVisibility(false);
        mLastViewedPatientsView.setListVisibility(true);
        mLastViewedPatientsView.setEmptyListVisibility(false);
        mLastViewedPatientsView.stopRefreshing();
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
        if (query.isEmpty() && !lastQuery.isEmpty()) {
            updateLastViewedList();
        }
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

    public void setLastQueryEmpty() {
        lastQuery = "";
    }

}
