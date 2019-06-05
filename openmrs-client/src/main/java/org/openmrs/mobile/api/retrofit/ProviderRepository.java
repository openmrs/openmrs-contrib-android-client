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

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.jetbrains.annotations.NotNull;
import org.openmrs.mobile.R;
import org.openmrs.mobile.api.RestApi;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.models.Provider;
import org.openmrs.mobile.models.Results;
import org.openmrs.mobile.utilities.NetworkUtils;
import org.openmrs.mobile.utilities.ToastUtil;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProviderRepository {

    final String TAG = getClass().getName();

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
                        Log.d(TAG, "Response Unsuccessful!" + response.errorBody());
                        ToastUtil.error(OpenMRS.getInstance().getString(R.string.unable_to_fetch_providers));
                        providerLiveData.setValue(null);
                    }

                }

                @Override
                public void onFailure(@NotNull Call<Results<Provider>> call, @NotNull Throwable t) {
                    Log.d(TAG, "Call Failed! Error: " + t.getMessage());
                    ToastUtil.error(OpenMRS.getInstance().getString(R.string.unable_to_fetch_providers));
                    providerLiveData.setValue(null);
                }
            });
        } else {
            ToastUtil.error(OpenMRS.getInstance().getString(R.string.device_offline_msg));
        }

        return providerLiveData;
    }
}
