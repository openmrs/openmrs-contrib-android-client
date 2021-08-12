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

package com.openmrs.android_sdk.library.api.workers.provider;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.openmrs.android_sdk.R;
import com.openmrs.android_sdk.library.OpenmrsAndroid;
import com.openmrs.android_sdk.library.api.RestApi;
import com.openmrs.android_sdk.library.api.RestServiceBuilder;
import com.openmrs.android_sdk.library.dao.ProviderRoomDAO;
import com.openmrs.android_sdk.library.databases.AppDatabase;
import com.openmrs.android_sdk.utilities.NetworkUtils;
import com.openmrs.android_sdk.utilities.ToastUtil;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * The type Delete provider worker.
 */
public class DeleteProviderWorker extends Worker {
    ProviderRoomDAO providerRoomDao;
    RestApi restApi;

    /**
     * Instantiates a new Delete provider worker.
     *
     * @param context      the context
     * @param workerParams the worker params
     */
    public DeleteProviderWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        restApi = RestServiceBuilder.createService(RestApi.class);
        providerRoomDao = AppDatabase.getDatabase(getApplicationContext()).providerRoomDAO();
    }

    @NonNull
    @Override
    public Result doWork() {
        String providerUuidTobeDeleted = getInputData().getString("uuid");

        if (deleteProvider(restApi, providerUuidTobeDeleted)) {
            new Handler(Looper.getMainLooper()).post(() -> {
                ToastUtil.success(OpenmrsAndroid.getInstance().getString(R.string.delete_provider_success_msg));
                OpenmrsAndroid.getOpenMRSLogger().e(OpenmrsAndroid.getInstance().getString(R.string.delete_provider_success_msg));
            });
            return Result.success();
        } else {
            return Result.retry();
        }
    }

    private boolean deleteProvider(RestApi restApi, String providerUuid) {
        if (NetworkUtils.isOnline()) {
            try {
                Response<ResponseBody> response = restApi.deleteProvider(providerUuid).execute();
                if (response.isSuccessful()) {
                    return true;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}

