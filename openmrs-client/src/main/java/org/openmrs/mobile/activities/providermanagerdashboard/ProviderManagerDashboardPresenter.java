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

package org.openmrs.mobile.activities.providermanagerdashboard;

import androidx.fragment.app.Fragment;

import org.jetbrains.annotations.NotNull;
import org.openmrs.mobile.activities.BasePresenter;
import org.openmrs.mobile.api.CustomApiCallback;
import org.openmrs.mobile.api.retrofit.ProviderRepository;
import org.openmrs.mobile.api.RestApi;
import org.openmrs.mobile.api.RestServiceBuilder;
import org.openmrs.mobile.models.Provider;

import java.util.List;

public class ProviderManagerDashboardPresenter extends BasePresenter implements ProviderManagerDashboardContract.Presenter, CustomApiCallback {

    private RestApi restApi;
    @NotNull
    private final ProviderManagerDashboardContract.View providerManagerView;

    ProviderManagerDashboardPresenter(@NotNull ProviderManagerDashboardContract.View providerManagerView) {
        this.providerManagerView = providerManagerView;
        this.providerManagerView.setPresenter(this);
        restApi = RestServiceBuilder.createService(RestApi.class);
    }

    public ProviderManagerDashboardPresenter(@NotNull ProviderManagerDashboardContract.View providerManagerView, @NotNull RestApi restApi) {
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
    public void updateViews(List<Provider> providerList) {
        if (providerList != null && providerList.size() != 0) {
            providerManagerView.updateAdapter(providerList);
            providerManagerView.updateVisibility(true, null);
        } else {
            providerManagerView.updateVisibility(false, "No Data to display.");
        }
    }

    @Override
    public void deleteProvider(String uuid) {
        ProviderRepository providerRepository = new ProviderRepository();
        providerRepository.deleteProviders(restApi, uuid, this);
    }

    @Override
    public void addProvider(Provider provider) {
        ProviderRepository providerRepository = new ProviderRepository();
        providerRepository.addProvider(restApi, provider, this);
    }

    @Override
    public void editProvider(Provider provider) {
        ProviderRepository providerRepository = new ProviderRepository();
        providerRepository.editProvider(restApi, provider, this);
    }

    @Override
    public void subscribe() {

    }

    @Override
    public void onSuccess() {
        providerManagerView.refreshUI();
    }

    @Override
    public void onFailure() {

    }
}