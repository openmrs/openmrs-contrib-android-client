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

package com.openmrs.android_sdk.library.api.workers.allergy;

import static com.openmrs.android_sdk.utilities.ApplicationConstants.BundleKeys.ALLERGY_UUID;
import static com.openmrs.android_sdk.utilities.ApplicationConstants.BundleKeys.PATIENT_UUID;

import okhttp3.ResponseBody;
import retrofit2.Response;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.openmrs.android_sdk.library.api.RestApi;
import com.openmrs.android_sdk.library.api.RestServiceBuilder;
import com.openmrs.android_sdk.library.dao.AllergyRoomDAO;
import com.openmrs.android_sdk.library.databases.AppDatabase;
import com.openmrs.android_sdk.utilities.NetworkUtils;


/**
 * The type Delete allergy worker.
 */
public class DeleteAllergyWorker extends Worker {
    AllergyRoomDAO allergyRoomDAO;
    RestApi restApi;

    /**
     * Instantiates a new Delete allergy worker.
     *
     * @param context      the context
     * @param workerParams the worker params
     */
    public DeleteAllergyWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        restApi = RestServiceBuilder.createService(RestApi.class);
        allergyRoomDAO = AppDatabase.getDatabase(getApplicationContext()).allergyRoomDAO();
    }

    @NonNull
    @Override
    public Result doWork() {

        String allergyUuid = getInputData().getString(ALLERGY_UUID);
        String patientUuid = getInputData().getString(PATIENT_UUID);

        boolean result = deleteAllergy(restApi, allergyUuid, patientUuid);

        if (result) {
            return Result.success();
        } else {
            return Result.retry();
        }
    }

    private boolean deleteAllergy(RestApi restApi, String allergyUuid, String patientUuid) {
        if (NetworkUtils.isOnline()) {
            Response<ResponseBody> response;
            try {
                response = restApi.deleteAllergy(patientUuid, allergyUuid).execute();
                allergyRoomDAO.deleteAllergyByUUID(allergyUuid);
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            return response != null && response.isSuccessful();
        }
        return false;
    }
}
