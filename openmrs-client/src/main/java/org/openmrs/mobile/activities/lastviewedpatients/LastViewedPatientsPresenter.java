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

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.openmrs.android_sdk.library.dao.PatientDAO;
import com.openmrs.android_sdk.library.models.Link;
import com.openmrs.android_sdk.library.models.Patient;
import com.openmrs.android_sdk.library.models.Results;
import com.openmrs.android_sdk.utilities.ApplicationConstants;
import com.openmrs.android_sdk.utilities.StringUtils;
import com.openmrs.android_sdk.utilities.ToastUtil;

import org.openmrs.mobile.activities.BasePresenter;
import com.openmrs.android_sdk.library.api.RestApi;
import com.openmrs.android_sdk.library.api.RestServiceBuilder;
import com.openmrs.android_sdk.library.api.repository.PatientRepository;
import com.openmrs.android_sdk.library.listeners.retrofitcallbacks.PatientResponseCallback;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class LastViewedPatientsPresenter extends BasePresenter implements LastViewedPatientsContract.Presenter {
    @NonNull
    private final LastViewedPatientsContract.View mLastViewedPatientsView;
    private PatientDAO patientDAO;
    private RestApi restApi;
    private String mQuery;
    private String lastQuery = "";
    private int limit = 15;
    private int startIndex = 0;
    private boolean isDownloadedAll = false;
    private PatientRepository patientRepository;

    public LastViewedPatientsPresenter(@NonNull LastViewedPatientsContract.View mLastViewedPatientsView, String lastQuery, Context appContext) {
        this.mLastViewedPatientsView = mLastViewedPatientsView;
        this.mLastViewedPatientsView.setPresenter(this);
        this.restApi = RestServiceBuilder.createService(RestApi.class);
        this.patientDAO = new PatientDAO();
        this.lastQuery = lastQuery;
        this.patientRepository = new PatientRepository();
    }

    public LastViewedPatientsPresenter(@NonNull LastViewedPatientsContract.View mLastViewedPatientsView, Context appContext) {
        this.mLastViewedPatientsView = mLastViewedPatientsView;
        this.mLastViewedPatientsView.setPresenter(this);
        this.restApi = RestServiceBuilder.createService(RestApi.class);
        this.patientDAO = new PatientDAO();
        this.patientRepository = new PatientRepository();
    }

    public LastViewedPatientsPresenter(@NonNull LastViewedPatientsContract.View mLastViewedPatientsView,
                                       RestApi restApi, PatientDAO patientDAO, PatientRepository patientRepository) {
        this.mLastViewedPatientsView = mLastViewedPatientsView;
        this.mLastViewedPatientsView.setPresenter(this);
        this.restApi = restApi;
        this.patientDAO = patientDAO;
        this.patientRepository = patientRepository;
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

    private void updateLastViewedList(List<Patient> patients) {
        patientRepository.updateLastViewedList(limit, startIndex, new PatientResponseCallback() {
            @Override
            public void onResponse(Results<Patient> patientResults) {
                setStartIndexIfMorePatientsAvailable(patientResults.getLinks());
                patients.addAll(filterNotDownloadedPatients(patientResults.getResults()));
                if (patients.size() < ApplicationConstants.MIN_NUMBER_OF_PATIENTS_TO_SHOW && !isDownloadedAll) {
                    updateLastViewedList(patients);
                } else {
                    mLastViewedPatientsView.updateList(patients);
                    setViewAfterPatientDownloadSuccess();
                    mLastViewedPatientsView.enableSwipeRefresh(true);
                    if (!isDownloadedAll) {
                        mLastViewedPatientsView.showRecycleViewProgressBar(true);
                    }
                }
            }

            @Override
            public void onErrorResponse(String errorMessage) {
                setViewAfterPatientDownloadError(errorMessage);
                mLastViewedPatientsView.enableSwipeRefresh(true);
            }
        });
    }

    public void findPatients(String query) {
        setViewBeforePatientDownload();
        lastQuery = query;
        patientRepository.findPatients(query, new PatientResponseCallback() {
            @Override
            public void onResponse(Results<Patient> patientResults) {
                mLastViewedPatientsView.updateList(filterNotDownloadedPatients(patientResults.getResults()));
                setViewAfterPatientDownloadSuccess();
            }

            @Override
            public void onErrorResponse(String errorMessage) {
                setViewAfterPatientDownloadError(errorMessage);
            }
        });
    }

    @Override
    public void loadMorePatients() {
        if (!isDownloadedAll) {
            patientRepository.loadMorePatients(limit, startIndex, new PatientResponseCallback() {
                @Override
                public void onResponse(Results<Patient> patientResults) {
                    List<Patient> patients = patientResults.getResults();
                    mLastViewedPatientsView.showRecycleViewProgressBar(false);
                    mLastViewedPatientsView.addPatientsToList(filterNotDownloadedPatients(patients));
                    setStartIndexIfMorePatientsAvailable(patientResults.getLinks());
                    if (!isDownloadedAll) {
                        mLastViewedPatientsView.showRecycleViewProgressBar(true);
                    }
                }

                @Override
                public void onErrorResponse(String errorMessage) {
                    ToastUtil.error(errorMessage);
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
            if ("next".equals(link.getRel())) {
                startIndex += limit;
                isDownloadedAll = false;
                linkFound = true;
            }
        }
        if (!linkFound) {
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

    public List<Patient> filterNotDownloadedPatients(List<Patient> patients) {
        List<Patient> newPatientList = new LinkedList<>();
        for (Patient patient : patients) {
            if (!patientDAO.isUserAlreadySaved(patient.getUuid())) {
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
        } else {
            updateLastViewedList();
        }
    }

    public void setLastQueryEmpty() {
        lastQuery = "";
    }
}
