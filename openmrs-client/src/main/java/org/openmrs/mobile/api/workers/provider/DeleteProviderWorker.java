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
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import org.openmrs.mobile.R;
import org.openmrs.mobile.api.RestApi;
import org.openmrs.mobile.api.RestServiceBuilder;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.dao.ProviderRoomDAO;
import org.openmrs.mobile.databases.AppDatabase;
import org.openmrs.mobile.utilities.NetworkUtils;
import org.openmrs.mobile.utilities.ToastUtil;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Response;

public class DeleteProviderWorker extends Worker {
    ProviderRoomDAO providerRoomDao;
    RestApi restApi;

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
                ToastUtil.success(OpenMRS.getInstance().getString(R.string.delete_provider_success_msg));
                OpenMRS.getInstance().getOpenMRSLogger().e(OpenMRS.getInstance().getString(R.string.delete_provider_success_msg));
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

