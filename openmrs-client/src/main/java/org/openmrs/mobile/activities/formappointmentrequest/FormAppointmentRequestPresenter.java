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
package org.openmrs.mobile.activities.formappointmentrequest;
import org.openmrs.mobile.activities.BasePresenter;
import org.openmrs.mobile.api.RestApi;
import org.openmrs.mobile.api.RestServiceBuilder;
import org.openmrs.mobile.models.Results;
import org.openmrs.mobile.models.appointmentrequestmodel.AppointmentRequest;
import org.openmrs.mobile.models.servicestypemodel.Services;
import org.openmrs.mobile.models.timeblocks.Result;
import org.openmrs.mobile.models.timeblocks.TimeBlocks;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
public class FormAppointmentRequestPresenter extends BasePresenter implements FormAppointmentRequestContract.Presenter {
    private FormAppointmentRequestContract.View mFormAppointmentRequestView;
    public FormAppointmentRequestPresenter(FormAppointmentRequestContract.View mFormAppointmentRequestView) {
        this.mFormAppointmentRequestView = mFormAppointmentRequestView;
        this.mFormAppointmentRequestView.setPresenter(this);
    }
    @Override
    public void subscribe() {
        getServiceTypes();
    }
    @Override
    public void getServiceTypes() {
        RestApi apiService = RestServiceBuilder.createService(RestApi.class);
        Call<Results<Services>> call = apiService.getServiceTypes();
        call.enqueue(new Callback<Results<Services>>() {

            @Override
            public void onResponse(Call<Results<Services>> call, Response<Results<Services>> response) {
                if (response.isSuccessful()) {
                    List<Services> services = response.body().getResults();
                    mFormAppointmentRequestView.fillServiceTypeDropDown(services);
                }

            }

            @Override
            public void onFailure(Call<Results<Services>> call, Throwable t) {
                //This method is left blank intentionally.
            }

        });

    }



    @Override
    public void deleteAppointmentRequest(String uuid) {
        RestApi restApi = RestServiceBuilder.createService(RestApi.class);
        Call<AppointmentRequest> call = restApi.deleteAppointmentRequest(uuid, true);
        call.enqueue(new Callback<AppointmentRequest>() {
            @Override
            public void onResponse(Call<AppointmentRequest> call, Response<AppointmentRequest> response) {
                if (response.isSuccessful()) {
                    mFormAppointmentRequestView.endActivity();
                }
            }
            @Override
            public void onFailure(Call<AppointmentRequest> call, Throwable t) {
                //This method is left blank intentionally.
            }
        });

    }

    @Override
    public void getTimeBlocks(String service) {
        RestApi apiService = RestServiceBuilder.createService(RestApi.class);
        Call <TimeBlocks> call = apiService.getTimeSlots();
        call.enqueue(new Callback<TimeBlocks>() {

            @Override
            public void onResponse(Call<TimeBlocks> call, Response<TimeBlocks> response) {
                if (response.isSuccessful()) {
                     List<Result> blocks = response.body().getResults();
                    mFormAppointmentRequestView.createDialog(blocks,service);
                }

            }

            @Override
            public void onFailure(Call<TimeBlocks> call, Throwable t){
                //This method is left blank intentionally.
            }

        });

    }
}