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

package org.openmrs.mobile.api.workers.allergy;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import org.jetbrains.annotations.NotNull;
import org.openmrs.mobile.R;
import org.openmrs.mobile.api.RestApi;
import org.openmrs.mobile.api.RestServiceBuilder;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.dao.AllergyRoomDAO;
import org.openmrs.mobile.databases.AppDatabase;
import org.openmrs.mobile.listeners.retrofit.CustomApiCallback;
import org.openmrs.mobile.utilities.NetworkUtils;
import org.openmrs.mobile.utilities.ToastUtil;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static org.openmrs.mobile.utilities.ApplicationConstants.BundleKeys.ALLERGY_UUID;
import static org.openmrs.mobile.utilities.ApplicationConstants.BundleKeys.PATIENT_UUID;


public class DeleteAllergyWorker extends Worker {
    AllergyRoomDAO allergyRoomDAO;
    RestApi restApi;

    public DeleteAllergyWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        restApi = RestServiceBuilder.createService(RestApi.class);
        allergyRoomDAO = AppDatabase.getDatabase(getApplicationContext()).allergyRoomDAO();
    }

    @NonNull
    @Override
    public Result doWork() {
        final boolean[] result = new boolean[1];
        String allergyUuid = getInputData().getString(ALLERGY_UUID);
        String patientUuid = getInputData().getString(PATIENT_UUID);

        deleteAllergy(restApi, allergyUuid, patientUuid, new CustomApiCallback() {
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

    private void deleteAllergy(RestApi restApi, String allergyUuid, String patientUuid, CustomApiCallback callback) {

        if (NetworkUtils.isOnline()) {
            restApi.deleteAllergy(patientUuid, allergyUuid).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                    if (response.isSuccessful()) {

                        ToastUtil.success(OpenMRS.getInstance().getString(R.string.delete_allergy_success));
                        callback.onSuccess();
                    }
                }

                @Override
                public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                    callback.onFailure();
                }
            });
        }
    }
}
