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

package org.openmrs.mobile.api.retrofit;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.jetbrains.annotations.NotNull;
import org.openmrs.mobile.R;
import org.openmrs.mobile.api.CustomApiCallback;
import org.openmrs.mobile.api.RestApi;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.models.Provider;
import org.openmrs.mobile.models.Results;
import org.openmrs.mobile.utilities.NetworkUtils;
import org.openmrs.mobile.utilities.ToastUtil;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProviderRepository {

    public LiveData<List<Provider>> getProviders(RestApi restApi) {

        final MutableLiveData<List<Provider>> providerLiveData = new MutableLiveData<>();
        if (NetworkUtils.isOnline()) {

            restApi.getProviderList().enqueue(new Callback<Results<Provider>>() {
                @Override
                public void onResponse(@NotNull Call<Results<Provider>> call, @NotNull Response<Results<Provider>> response) {
                    if (response.isSuccessful()) {
                        if (!response.body().getResults().isEmpty()) {
                            providerLiveData.setValue(response.body().getResults());
                        } else {
                            providerLiveData.setValue(null);
                        }
                    } else {
                        OpenMRS.getInstance().getOpenMRSLogger().e("Reading providers failed. Response: " + response.errorBody());
                        ToastUtil.error(OpenMRS.getInstance().getString(R.string.unable_to_fetch_providers));
                        providerLiveData.setValue(null);
                    }

                }

                @Override
                public void onFailure(@NotNull Call<Results<Provider>> call, @NotNull Throwable t) {
                    OpenMRS.getInstance().getOpenMRSLogger().e("Reading providers failed.", t);
                    ToastUtil.error(OpenMRS.getInstance().getString(R.string.unable_to_fetch_providers));
                    providerLiveData.setValue(null);
                }
            });
        } else {
            ToastUtil.error(OpenMRS.getInstance().getString(R.string.device_offline_msg));
            OpenMRS.getInstance().getOpenMRSLogger().e("Failed to read providers. Device Offline");
        }

        return providerLiveData;
    }

    public void deleteProviders(RestApi restApi, String uuid, CustomApiCallback callback) {
        if (NetworkUtils.isOnline()) {
            restApi.deleteProvider(uuid).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        ToastUtil.success(OpenMRS.getInstance().getString(R.string.delete_provider_success_msg));
                        OpenMRS.getInstance().getOpenMRSLogger().e("Deleting Provider Successful " + response.raw());
                        callback.onSuccess();
                    }
                }

                @Override
                public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                    ToastUtil.error(OpenMRS.getInstance().getString(R.string.delete_provider_failure_msg));
                    OpenMRS.getInstance().getOpenMRSLogger().e("Failed to delete provider. Error:  " + t.getMessage());
                    callback.onFailure();
                }
            });
        } else {
            ToastUtil.error(OpenMRS.getInstance().getString(R.string.delete_provider_no_network_msg));
            OpenMRS.getInstance().getOpenMRSLogger().e("Failed to delete provider. Device Offline.");
            callback.onFailure();
        }
    }

    public void addProvider(RestApi restApi, Provider provider, CustomApiCallback callback) {
        if (NetworkUtils.isOnline()) {
            restApi.addProvider(provider).enqueue(new Callback<Provider>() {
                @Override
                public void onResponse(@NotNull Call<Provider> call, @NotNull Response<Provider> response) {
                    if (response.isSuccessful()) {
                        ToastUtil.success(OpenMRS.getInstance().getString(R.string.add_provider_success_msg));
                        OpenMRS.getInstance().getOpenMRSLogger().e("Adding Provider Successful " + response.raw());
                        callback.onSuccess();
                    }
                }

                @Override
                public void onFailure(@NotNull Call<Provider> call, @NotNull Throwable t) {
                    ToastUtil.error(OpenMRS.getInstance().getString(R.string.add_provider_failure_msg));
                    OpenMRS.getInstance().getOpenMRSLogger().e("Failed to add provider. Error:  " + t.getMessage());
                    callback.onFailure();
                }
            });
        } else {
            ToastUtil.error(OpenMRS.getInstance().getString(R.string.add_provider_no_network_msg));
            OpenMRS.getInstance().getOpenMRSLogger().e("Failed to add provider. Device Offline");
            callback.onFailure();
        }
    }

    public void editProvider(RestApi restApi, Provider provider, CustomApiCallback callback) {
        if (NetworkUtils.isOnline()) {
            restApi.editProvider(provider.getUuid(), provider).enqueue(new Callback<Provider>() {
                @Override
                public void onResponse(@NotNull Call<Provider> call, @NotNull Response<Provider> response) {
                    if (response.isSuccessful()) {
                        ToastUtil.success(OpenMRS.getInstance().getString(R.string.edit_provider_success_msg));
                        OpenMRS.getInstance().getOpenMRSLogger().e("Editing Provider Successful " + response.raw());
                        callback.onSuccess();
                    }
                }

                @Override
                public void onFailure(@NotNull Call<Provider> call, @NotNull Throwable t) {
                    ToastUtil.error(OpenMRS.getInstance().getString(R.string.edit_provider_failure_msg));
                    OpenMRS.getInstance().getOpenMRSLogger().e("Failed to edit provider. Error:  " + t.getMessage());
                    callback.onFailure();
                }
            });
        } else {
            ToastUtil.error(OpenMRS.getInstance().getString(R.string.edit_provider_no_network_msg));
            OpenMRS.getInstance().getOpenMRSLogger().e("Failed to edit provider. Device Offline");
            callback.onFailure();
        }
    }
}
