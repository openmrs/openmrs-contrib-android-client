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

import java.util.List;

import rx.android.schedulers.AndroidSchedulers;

import androidx.fragment.app.Fragment;

import com.openmrs.android_sdk.library.api.RestApi;
import com.openmrs.android_sdk.library.api.RestServiceBuilder;
import com.openmrs.android_sdk.library.api.repository.ProviderRepository;
import com.openmrs.android_sdk.library.listeners.retrofitcallbacks.DefaultResponseCallback;
import com.openmrs.android_sdk.library.models.Provider;
import com.openmrs.android_sdk.utilities.ToastUtil;

import org.jetbrains.annotations.NotNull;
import org.openmrs.mobile.activities.BasePresenter;

public class ProviderManagerDashboardPresenter extends BasePresenter implements ProviderManagerDashboardContract.Presenter, DefaultResponseCallback {
    private RestApi restApi;
    private ProviderRepository providerRepository;
    @NotNull
    private final ProviderManagerDashboardContract.View providerManagerView;

    ProviderManagerDashboardPresenter(@NotNull ProviderManagerDashboardContract.View providerManagerView) {
        this.providerManagerView = providerManagerView;
        this.providerManagerView.setPresenter(this);
        restApi = RestServiceBuilder.createService(RestApi.class);
        providerRepository = new ProviderRepository();
    }

    public ProviderManagerDashboardPresenter(@NotNull ProviderManagerDashboardContract.View providerManagerView, @NotNull RestApi restApi, ProviderRepository providerRepository) {
        this.providerManagerView = providerManagerView;
        this.restApi = restApi;
        this.providerManagerView.setPresenter(this);
        this.providerRepository = providerRepository;
    }

    @Override
    public void getProviders(Fragment fragment) {
        providerRepository.getProviders().observeOn(AndroidSchedulers.mainThread()).subscribe(this::updateViews);
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
    public void deleteProvider(String providerUuid) {
        providerRepository.deleteProviders(providerUuid, this);
    }

    @Override
    public void addProvider(Provider provider) {
        providerRepository.addProvider(provider, this);
    }

    @Override
    public void updateProvider(Provider provider) {
        providerRepository.updateProvider(provider, this);
    }

    @Override
    public void subscribe() {

    }

    @Override
    public void onErrorResponse(String errorMessage) {
        ToastUtil.error(errorMessage);
    }

    @Override
    public void onResponse() {
        providerManagerView.refreshUI();
    }
}
