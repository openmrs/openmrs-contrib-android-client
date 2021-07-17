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

package com.example.openmrs_android_sdk.library.api.workers.provider;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.openmrs_android_sdk.R;
import com.example.openmrs_android_sdk.library.OpenmrsAndroid;
import com.example.openmrs_android_sdk.library.api.RestApi;
import com.example.openmrs_android_sdk.library.api.RestServiceBuilder;
import com.example.openmrs_android_sdk.library.dao.ProviderRoomDAO;
import com.example.openmrs_android_sdk.library.databases.AppDatabase;
import com.example.openmrs_android_sdk.library.models.Provider;
import com.example.openmrs_android_sdk.utilities.NetworkUtils;
import com.example.openmrs_android_sdk.utilities.ToastUtil;

import java.io.IOException;

import retrofit2.Response;

public class AddProviderWorker extends Worker {
    ProviderRoomDAO providerRoomDao;
    RestApi restApi;

    public AddProviderWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        restApi = RestServiceBuilder.createService(RestApi.class);
        providerRoomDao = AppDatabase.getDatabase(getApplicationContext()).providerRoomDAO();
    }

    @NonNull
    @Override
    public Result doWork() {
        Provider provider = providerRoomDao.findProviderByID(getInputData().getLong("id", 0l)).blockingGet();

        if (provider == null) {
            return Result.failure();
        } else {
            // Its necessary since server gives Error 500 if request has UUID set as ""
            provider.getPerson().setUuid(null);

            if (addProvider(restApi, provider)) {
                new Handler(Looper.getMainLooper()).post(() -> {
                    ToastUtil.success(OpenmrsAndroid.getInstance().getString(R.string.add_provider_success_msg));
                    OpenmrsAndroid.getOpenMRSLogger().e(OpenmrsAndroid.getInstance().getString(R.string.add_provider_success_msg));
                });
                return Result.success();
            } else {
                return Result.retry();
            }
        }
    }

    private boolean addProvider(RestApi restApi, Provider provider) {
        if (NetworkUtils.isOnline()) {
            try {
                Response<Provider> response = restApi.addProvider(provider).execute();
                if (response.isSuccessful()) {

                    providerRoomDao.updateProviderUuidById(provider.getId(), response.body().getUuid());
                    providerRoomDao.updateProviderByUuid(response.body().getDisplay(), provider.getId(),
                            response.body().getPerson(), response.body().getUuid(), response.body().getIdentifier());
                    return true;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
