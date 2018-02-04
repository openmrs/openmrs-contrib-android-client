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

package org.openmrs.mobile.activities.manageservice;
import org.openmrs.mobile.activities.BasePresenter;
import org.openmrs.mobile.api.RestApi;
import org.openmrs.mobile.api.RestServiceBuilder;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.application.OpenMRSLogger;
import org.openmrs.mobile.models.servicestypemodel.Services;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
public class ManageServicePresenter extends BasePresenter implements ManageServiceContract.Presenter {
    private ManageServiceContract.View mManageServiceView;
    public OpenMRSLogger mLogger;
    public OpenMRS mOpenMRS;

    public ManageServicePresenter(ManageServiceContract.View mManageServiceView, OpenMRS openMRS) {
        this.mManageServiceView = mManageServiceView;
        this.mOpenMRS = openMRS;
        this.mLogger = openMRS.getOpenMRSLogger();
        this.mManageServiceView.setPresenter(this);

    }

    @Override
    public void subscribe() {
        mManageServiceView.getServiceTypes();
    }


    @Override
    public void deleteServiceTypes(Services services) {

        RestApi restApi = RestServiceBuilder.createService(RestApi.class);
        Call<Services> call = restApi.deleteServiceTypes(services.getUuid(), true);
        call.enqueue(new Callback<Services>() {
            @Override
            public void onResponse(Call<Services> call, Response<Services> response) {
                if (response.isSuccessful()) {

                    mManageServiceView.getServiceTypes();
                }
            }
            @Override
            public void onFailure(Call<Services> call, Throwable t) {
                //This method is left blank intentionally.
            }
        });
    }
    @Override
    public void setQuery(String query) {
        //This method is left blank intentionally.
    }
}
