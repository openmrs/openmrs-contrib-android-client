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

package org.openmrs.mobile.api.workers.provider;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import org.jetbrains.annotations.NotNull;
import org.openmrs.mobile.R;
import org.openmrs.mobile.listeners.retrofit.CustomApiCallback;
import org.openmrs.mobile.api.RestApi;
import org.openmrs.mobile.api.RestServiceBuilder;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.dao.ProviderRoomDAO;
import org.openmrs.mobile.databases.AppDatabase;
import org.openmrs.mobile.models.Provider;
import org.openmrs.mobile.utilities.NetworkUtils;
import org.openmrs.mobile.utilities.ToastUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateProviderWorker extends Worker {
    ProviderRoomDAO providerRoomDao;
    RestApi restApi;

    public UpdateProviderWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        restApi = RestServiceBuilder.createService(RestApi.class);
        providerRoomDao = AppDatabase.getDatabase(getApplicationContext()).providerRoomDAO();
    }

    @NonNull
    @Override
    public Result doWork() {

        final boolean[] result = new boolean[1];
        String providerUuidTobeUpdated = getInputData().getString("uuid");
        Provider providerTobeUpdated = providerRoomDao.findProviderByUUID(providerUuidTobeUpdated).blockingGet();

        //preprocessing needed just because in some server examples this fields is set to null
        providerTobeUpdated.getPerson().setUuid(null);

        updateProvider(restApi, providerTobeUpdated, new CustomApiCallback() {
            @Override
            public void onSuccess() {
                result[0] = true;
            }

            @Override
            public void onFailure() {
                result[0] = false;
            }
        });

        if (result[0]) {
            return Result.success();
        } else {
            return Result.retry();
        }
    }

    private void updateProvider(RestApi restApi, Provider provider, CustomApiCallback callback) {

        if (NetworkUtils.isOnline()) {
            restApi.UpdateProvider(provider.getUuid(), provider).enqueue(new Callback<Provider>() {
                @Override
                public void onResponse(@NotNull Call<Provider> call, @NotNull Response<Provider> response) {
                    if (response.isSuccessful()) {
                        providerRoomDao.updateProviderByUuid(response.body().getDisplay(), provider.getId(), response.body().getPerson(), response.body().getUuid(),
                            response.body().getIdentifier());
                        ToastUtil.success(OpenMRS.getInstance().getString(R.string.edit_provider_success_msg));
                        OpenMRS.getInstance().getOpenMRSLogger().e("Editing Provider Successful ");
                        callback.onSuccess();
                    }
                }

                @Override
                public void onFailure(@NotNull Call<Provider> call, @NotNull Throwable t) {
                    callback.onFailure();
                }
            });
        }
    }
}
