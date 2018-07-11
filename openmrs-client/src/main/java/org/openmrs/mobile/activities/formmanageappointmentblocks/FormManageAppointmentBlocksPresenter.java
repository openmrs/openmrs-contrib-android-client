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
package org.openmrs.mobile.activities.formmanageappointmentblocks;

import android.widget.Spinner;
import android.widget.TextView;

import org.openmrs.mobile.activities.BasePresenter;
import org.openmrs.mobile.api.RestApi;
import org.openmrs.mobile.api.RestServiceBuilder;
import org.openmrs.mobile.dao.LocationDAO;
import org.openmrs.mobile.models.Location;
import org.openmrs.mobile.models.Results;
import org.openmrs.mobile.models.appointmentblocksmodel.AppointmentBlocks;
import org.openmrs.mobile.models.provider.Provider;
import org.openmrs.mobile.models.provider.Result;
import org.openmrs.mobile.models.servicestypemodel.Services;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.android.schedulers.AndroidSchedulers;


public class FormManageAppointmentBlocksPresenter extends BasePresenter implements FormManageAppointmentBlocksContract.Presenter {
    private FormManageAppointmentBlocksContract.View mFormManageAppointmentBlocksView;

    private LocationDAO locationDAO;

    public FormManageAppointmentBlocksPresenter(FormManageAppointmentBlocksContract.View mFormManageAppointmentBlocksView) {

        this.locationDAO = new LocationDAO();
        this.mFormManageAppointmentBlocksView = mFormManageAppointmentBlocksView;
        this.mFormManageAppointmentBlocksView.setPresenter(this);

    }

    @Override
    public void subscribe() {
        loadLocations();
        getProvider();

    }

    @Override
    public void loadLocations() {
        addSubscription(locationDAO.getLocations()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(locations -> {
                    if (locations.size() > 0) {
                        mFormManageAppointmentBlocksView.fillLocationDropDown(locations);
                    }
                }));
    }

    @Override
    public void addAppointmentBlocks(Spinner locations, List<Location> locationList, Spinner mDropdownProvider, List<Result> providerList, TextView datetime, TextView datetimeend, TextView mAppointmentBlocksServiceType, List<Services> serviceList) {
        AppointmentBlocks blocks = new AppointmentBlocks();
        org.openmrs.mobile.models.appointmentblocksmodel.Result result = new org.openmrs.mobile.models.appointmentblocksmodel.Result();

        Location location = locationList.get(locations.getSelectedItemPosition());
        Result provider = providerList.get(mDropdownProvider.getSelectedItemPosition());
        result.setLocation(location);
        result.setProvider(provider);
        result.setEndDate(datetimeend.getText().toString());
        result.setStartDate(datetime.getText().toString());
        result.setTypes(serviceList);
        List<org.openmrs.mobile.models.appointmentblocksmodel.Result> list = new ArrayList<>();
        list.add(result);

        blocks.setResults(list);
        RestApi restApi = RestServiceBuilder.createService(RestApi.class);
        Call<AppointmentBlocks> call = restApi.newAppointmentBlocks(blocks);
        call.enqueue(new Callback<AppointmentBlocks>() {
            @Override
            public void onResponse(Call<AppointmentBlocks> call, Response<AppointmentBlocks> response) {
                if (response.isSuccessful()) {
                    mFormManageAppointmentBlocksView.endActivity();
                }
            }

            @Override
            public void onFailure(Call<AppointmentBlocks> call, Throwable t) {
                //This method is left blank intentionally.
            }
        });

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
                    mFormManageAppointmentBlocksView.createDialog(services);
                }

            }

            @Override
            public void onFailure(Call<Results<Services>> call, Throwable t) {
                //This method is left blank intentionally.
            }

        });

    }


    @Override
    public void getProvider() {
        RestApi restApi = RestServiceBuilder.createService(RestApi.class);
        Call<Provider> call = restApi.getProvider();
        call.enqueue(new Callback<Provider>() {
            @Override
            public void onResponse(Call<Provider> call, Response<Provider> response) {
                if (response.isSuccessful()) {
                    List<Result> provider = response.body().getResults();
                    mFormManageAppointmentBlocksView.fillProviderDropDown(provider);

                }
            }

            @Override
            public void onFailure(Call<Provider> call, Throwable t) {
                //This method is left blank intentionally.
            }
        });
    }


    @Override
    public void deleteAppointmentBlocks(String uuid) {
        RestApi restApi = RestServiceBuilder.createService(RestApi.class);
        Call<AppointmentBlocks> call = restApi.deleteAppointmentBlocks(uuid, true);
        call.enqueue(new Callback<AppointmentBlocks>() {
            @Override
            public void onResponse(Call<AppointmentBlocks> call, Response<AppointmentBlocks> response) {
                if (response.isSuccessful()) {

                    mFormManageAppointmentBlocksView.endActivity();
                }
            }

            @Override
            public void onFailure(Call<AppointmentBlocks> call, Throwable t) {
                //This method is left blank intentionally.
            }
        });

    }


}


