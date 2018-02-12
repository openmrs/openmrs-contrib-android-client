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
package org.openmrs.mobile.activities.formaddeditservice;

import android.text.Editable;

import org.openmrs.mobile.activities.BasePresenter;
import org.openmrs.mobile.api.RestApi;
import org.openmrs.mobile.api.RestServiceBuilder;
import org.openmrs.mobile.models.servicestypemodel.Services;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FormAddEditServicePresenter extends BasePresenter implements FormAddEditServiceContract.Presenter {
    private FormAddEditServiceContract.View mFormAddEditServiceView;
    public FormAddEditServicePresenter(FormAddEditServiceContract.View mFormAddEditServiceView) {
        this.mFormAddEditServiceView = mFormAddEditServiceView;
        this.mFormAddEditServiceView.setPresenter(this);
    }
    @Override
    public void subscribe() {
        //This method is left blank intentionally
    }
    @Override
    public void deleteServiceTypes(String Uuid) {
        RestApi restApi = RestServiceBuilder.createService(RestApi.class);
        Call<Services> call = restApi.deleteServiceTypes(Uuid, true);
        call.enqueue(new Callback<Services>() {
            @Override
            public void onResponse(Call<Services> call, Response<Services> response) {
                if (response.isSuccessful()) {
                mFormAddEditServiceView.endActivity();
                }
            }
            @Override
            public void onFailure(Call<Services> call, Throwable t) {
                //This method is left blank intentionally.
            }
        });
    }

    @Override
    public void editServiceTypes(Editable mServiceNameText, Editable mServiceDiscriptionText, Editable mServiceDurationText, String uuid) {
       Services services=new Services();
        services.setName(String.valueOf(mServiceNameText));
        services.setDuration(Integer.valueOf(String.valueOf(mServiceDurationText)));
        services.setDescription(String.valueOf(mServiceDiscriptionText));
        RestApi restApi = RestServiceBuilder.createService(RestApi.class);
        Call<Services> call = restApi.editServiceTypes(uuid,services);
        call.enqueue(new Callback<Services>() {
            @Override
            public void onResponse(Call<Services> call, Response<Services> response) {
                if (response.isSuccessful()) {
                    mFormAddEditServiceView.endActivity();
                }
            }
            @Override
            public void onFailure(Call<Services> call, Throwable t) {
                //This method is left blank intentionally.
            }
        });

    }

    @Override
    public void addServiceTypes(Editable mServiceNameText, Editable mServiceDiscriptionText, Editable mServiceDurationText) {
        Services services=new Services();
        services.setName(String.valueOf(mServiceNameText));
        services.setDuration(Integer.valueOf(mServiceDurationText.toString()));
        services.setDescription(String.valueOf(mServiceDiscriptionText));
        RestApi restApi = RestServiceBuilder.createService(RestApi.class);
        Call<Services> call = restApi.newServiceTypes(services);
        call.enqueue(new Callback<Services>() {
            @Override
            public void onResponse(Call<Services> call, Response<Services> response) {
                if (response.isSuccessful()) {
                    mFormAddEditServiceView.endActivity();
                }
            }
            @Override
            public void onFailure(Call<Services> call, Throwable t) {
                //This method is left blank intentionally.
            }
        });

    }
}