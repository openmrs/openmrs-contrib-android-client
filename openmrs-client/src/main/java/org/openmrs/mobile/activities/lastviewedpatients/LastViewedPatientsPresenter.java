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
import org.openmrs.mobile.models.retrofit.Patient;
import org.openmrs.mobile.models.retrofit.Results;
import org.openmrs.mobile.utilities.StringUtils;
import org.openmrs.mobile.utilities.ToastUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LastViewedPatientsPresenter implements LastViewedPatientsContract.Presenter{

    // View
    @NonNull
    private final LastViewedPatientsContract.View mLastViewedPatientsView;

    private boolean mIsSearching;
    private String mQuery;
    private String mLastQuery = "QUERY";

    public LastViewedPatientsPresenter(@NonNull LastViewedPatientsContract.View mLastViewedPatientsView) {
        this.mLastViewedPatientsView = mLastViewedPatientsView;
        this.mLastViewedPatientsView.setPresenter(this);
    }


    @Override
    public void start() {
        updateLastViewedList();
    }

    public void updateLastViewedList() {
        if (!mLastViewedPatientsView.isRefreshing()) {
            mLastViewedPatientsView.setSpinnerVisibility(true);
        }
        mLastViewedPatientsView.setEmptyListVisibility(false);
        mLastViewedPatientsView.setListVisibility(false);

        RestApi restApi = RestServiceBuilder.createService(RestApi.class);
        Call<Results<Patient>> call = restApi.getLastViewedPatients();
        call.enqueue(new Callback<Results<Patient>>() {
            @Override
            public void onResponse(Call<Results<Patient>> call, Response<Results<Patient>> response) {
                mLastViewedPatientsView.setSpinnerVisibility(false);
                if (response.isSuccessful()) {
                    mLastViewedPatientsView.updateList(response.body().getResults());
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
                ToastUtil.error(t.getMessage());
                mLastViewedPatientsView.setSpinnerVisibility(false);
                mLastViewedPatientsView.setListVisibility(false);
                mLastViewedPatientsView.stopRefreshing();
            }
        });

    }

    public void updateLastViewedList(String query) {
        mQuery = query;
        if (query.isEmpty() && !mLastQuery.isEmpty()) {
            mIsSearching = false;
            updateLastViewedList();
        }
    }

    public void findPatients(String query) {
        if (!mLastViewedPatientsView.isRefreshing()) {
            mLastViewedPatientsView.setSpinnerVisibility(true);
        }
        mLastViewedPatientsView.setEmptyListVisibility(false);
        mLastQuery = query;
        mIsSearching = true;

        RestApi restApi = RestServiceBuilder.createService(RestApi.class);
        Call<Results<Patient>> call = restApi.getPatients(query);
        call.enqueue(new Callback<Results<Patient>>() {
            @Override
            public void onResponse(Call<Results<Patient>> call, Response<Results<Patient>> response) {
                mLastViewedPatientsView.setSpinnerVisibility(false);
                if (response.isSuccessful()) {
                    mLastViewedPatientsView.updateList(response.body().getResults());
                    mLastViewedPatientsView.setListVisibility(true);
                    mLastViewedPatientsView.setEmptyListVisibility(false);
                }
                else {
                    mLastViewedPatientsView.setListVisibility(false);
                    mLastViewedPatientsView.setEmptyListText(response.message());
                    mLastViewedPatientsView.setEmptyListVisibility(true);
                }
                mIsSearching = false;
                mLastViewedPatientsView.stopRefreshing();
            }
            @Override
            public void onFailure(Call<Results<Patient>> call, Throwable t) {
                ToastUtil.error(t.getMessage());
                mLastViewedPatientsView.setSpinnerVisibility(false);
                mLastViewedPatientsView.setListVisibility(false);
                mIsSearching = false;
                mLastViewedPatientsView.stopRefreshing();
            }
        });
    }

    @Override
    public void refresh() {
        if (StringUtils.notEmpty(mQuery)) {
            findPatients(mQuery);
        }
        else {
            updateLastViewedList();
        }
    }

}
