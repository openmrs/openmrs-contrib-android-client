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

import dagger.assisted.Assisted;
import dagger.assisted.AssistedInject;
import okhttp3.ResponseBody;
import retrofit2.Response;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.hilt.work.HiltWorker;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.openmrs.android_sdk.R;
import com.openmrs.android_sdk.library.OpenmrsAndroid;
import com.openmrs.android_sdk.library.api.RestApi;
import com.openmrs.android_sdk.library.dao.AllergyRoomDAO;
import com.openmrs.android_sdk.utilities.NetworkUtils;


/**
 * The type Delete allergy worker.
 */
@HiltWorker
public class DeleteAllergyWorker extends Worker {
    private final AllergyRoomDAO allergyRoomDAO;
    private final RestApi restApi;

    /**
     * Instantiates a new Delete allergy worker.
     *
     * @param context      the context
     * @param workerParams the worker params
     */
    @AssistedInject
    public DeleteAllergyWorker(@Assisted @NonNull Context context,
                               @Assisted @NonNull WorkerParameters workerParams,
                               AllergyRoomDAO allergyRoomDAO, RestApi restApi) {
        super(context, workerParams);
        this.allergyRoomDAO = allergyRoomDAO;
        this.restApi = restApi;
    }

    @NonNull
    @Override
    public Result doWork() {

        String allergyUuid = getInputData().getString(ALLERGY_UUID);
        String patientUuid = getInputData().getString(PATIENT_UUID);

        boolean result = deleteAllergy(restApi, allergyUuid, patientUuid);

        if (result) {
            OpenmrsAndroid.getOpenMRSLogger().i(getApplicationContext().getString(R.string.delete_allergy_success));
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
