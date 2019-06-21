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

package org.openmrs.mobile.activities.providermanager;

import androidx.fragment.app.Fragment;

import org.jetbrains.annotations.NotNull;
import org.openmrs.mobile.activities.BasePresenter;
import org.openmrs.mobile.api.retrofit.ProviderRepository;
import org.openmrs.mobile.api.RestApi;
import org.openmrs.mobile.api.RestServiceBuilder;
import org.openmrs.mobile.models.Provider;

import java.util.List;

public class ProviderManagementPresenter extends BasePresenter implements ProviderManagerContract.Presenter {

    private RestApi restApi;
    @NotNull
    private final ProviderManagerContract.View providerManagerView;

    ProviderManagementPresenter(@NotNull ProviderManagerContract.View providerManagerView) {
        this.providerManagerView = providerManagerView;
        this.providerManagerView.setPresenter(this);
        restApi = RestServiceBuilder.createService(RestApi.class);
    }

    public ProviderManagementPresenter(@NotNull ProviderManagerContract.View providerManagerView, @NotNull RestApi restApi) {
        this.providerManagerView = providerManagerView;
        this.restApi = restApi;
        this.providerManagerView.setPresenter(this);
    }

    @Override
    public void getProviders(Fragment fragment) {
        ProviderRepository providerRepository = new ProviderRepository();
        providerRepository.getProviders(restApi).observe(fragment, this::updateViews);
    }

    @Override
    public void updateViews(List<Provider> providerList){
        if (providerList != null && providerList.size() != 0) {
            providerManagerView.updateAdapter(providerList);
            providerManagerView.updateVisibility(true, null);
        } else {
            providerManagerView.updateVisibility(false, "No Data to display.");
        }
    }

    @Override
    public void subscribe() {

    }
}