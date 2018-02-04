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
package org.openmrs.mobile.activities.manageappointment;
import android.util.Log;

import org.openmrs.mobile.activities.BasePresenter;
import org.openmrs.mobile.api.RestApi;
import org.openmrs.mobile.api.RestServiceBuilder;
import org.openmrs.mobile.models.appointment.Appointment;
import org.openmrs.mobile.models.appointment.Result;
import org.openmrs.mobile.models.appointment.Status;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ManageAppointmentPresenter extends BasePresenter implements ManageAppointmentContract.Presenter {
    private ManageAppointmentContract.View mManageAppointmentView;
    public String mQuery;

    public ManageAppointmentPresenter(ManageAppointmentContract.View mManageAppointmentView) {
        this.mManageAppointmentView = mManageAppointmentView;
        this.mManageAppointmentView.setPresenter(this);

    }

    @Override
    public void subscribe() {
       getAppointment();
    }

    @Override
    public void setQuery(String query) {
        mQuery = query;
    }

    @Override
    public void setAppointmentStatus(Result result, String miss) {
        Status status=new Status();
        status.setCode(miss.toUpperCase());
        status.setName(miss);
        status.setType(miss.toUpperCase());
        result.setStatus(status);
        Appointment appointment=new Appointment();
        List<Result> list = new ArrayList<Result>();
            list.add(result);

        appointment.setResults(list);
        RestApi apiService = RestServiceBuilder.createService(RestApi.class);
        Call<Appointment> call = apiService.setAppointmentStatus(result.getUuid(),appointment);
        call.enqueue(new Callback<Appointment>() {

            @Override
            public void onResponse(Call<Appointment> call, Response<Appointment> response) {
                if (response.isSuccessful()) {
                    Log.e("Test",response.toString());
                }
            }
            @Override
            public void onFailure(Call<Appointment> call, Throwable t) {
                //This method is left blank intentionally.
            }

        });

    }
    @Override
    public void getAppointment() {
        RestApi apiService = RestServiceBuilder.createService(RestApi.class);
        Call<Appointment> call = apiService.getAppointments();
        call.enqueue(new Callback<Appointment>() {

            @Override
            public void onResponse(Call<Appointment> call, Response<Appointment> response) {
                if (response.isSuccessful()) {
                    List<Result> blocks =response.body().getResults();
                      mManageAppointmentView.updateListVisibility(true);
                    mManageAppointmentView.updateAdapter(blocks);
                }

            }

            @Override
            public void onFailure(Call<Appointment> call, Throwable t) {
                //This method is left blank intentionally.
            }

        });

    }


}
