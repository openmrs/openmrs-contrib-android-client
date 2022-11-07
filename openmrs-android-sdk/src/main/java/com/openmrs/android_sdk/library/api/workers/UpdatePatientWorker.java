/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */

package com.openmrs.android_sdk.library.api.workers;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedInject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.hilt.work.HiltWorker;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.openmrs.android_sdk.R;
import com.openmrs.android_sdk.library.OpenMRSLogger;
import com.openmrs.android_sdk.library.api.RestApi;
import com.openmrs.android_sdk.library.dao.PatientDAO;
import com.openmrs.android_sdk.library.models.Patient;
import com.openmrs.android_sdk.library.models.PatientDto;
import com.openmrs.android_sdk.library.models.PatientDtoUpdate;
import com.openmrs.android_sdk.library.models.PatientPhoto;
import com.openmrs.android_sdk.utilities.ApplicationConstants;
import com.openmrs.android_sdk.utilities.NetworkUtils;
import com.openmrs.android_sdk.utilities.ToastUtil;

import org.jetbrains.annotations.NotNull;

/**
 * The type Update patient worker.
 */
@HiltWorker
public class UpdatePatientWorker extends Worker {
    private static final int ON_SUCCESS = 1;
    private static final int ON_FAILURE = 2;
    private static final int ON_FAILURE_RESPONSE_PHOTO_UPDATE = 3;
    private final RestApi restApi;
    private final PatientDAO patientDAO;
    private final OpenMRSLogger logger;
    private final Handler mHandler;

    /**
     * Instantiates a new Update patient worker.
     *
     * @param appContext   the app context
     * @param workerParams the worker params
     */
    @AssistedInject
    public UpdatePatientWorker(@Assisted @NonNull Context appContext,
                               @Assisted @NonNull WorkerParameters workerParams,
                               RestApi restApi, PatientDAO patientDAO, OpenMRSLogger logger) {
        super(appContext, workerParams);
        this.restApi = restApi;
        this.patientDAO = patientDAO;
        this.logger = logger;

        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NotNull Message msg) {
                String responseMessage;
                switch (msg.what) {
                    case ON_SUCCESS:
                        String updateSuccessPatientName = (String) msg.obj;
                        ToastUtil.success(getApplicationContext().getString(R.string.patient_update_successful, updateSuccessPatientName));
                        logger.i(getApplicationContext().getString(R.string.patient_update_successful, updateSuccessPatientName));
                        break;
                    case ON_FAILURE:
                        String updateFailedPatientName = (String) msg.obj;
                        logger.e(getApplicationContext().getString(R.string.patient_update_unsuccessful, updateFailedPatientName));
                        break;
                    case ON_FAILURE_RESPONSE_PHOTO_UPDATE:
                        responseMessage = (String) msg.obj;
                        ToastUtil.notify(getApplicationContext().getString(R.string.patient_photo_update_unsuccessful, responseMessage));
                        logger.e(getApplicationContext().getString(R.string.patient_photo_update_unsuccessful, responseMessage));
                        break;
                }
                super.handleMessage(msg);
            }
        };
    }

    @NonNull
    @Override
    public Result doWork() {
        String patientIdTobeUpdated = getInputData().getString(ApplicationConstants.PRIMARY_KEY_ID);
        Patient patientTobeUpdated = patientDAO.findPatientByID(patientIdTobeUpdated);

        if (!NetworkUtils.isOnline()) return Result.retry();

        if (updatePatient(patientTobeUpdated)) {
            Message msg = new Message();
            msg.obj = patientTobeUpdated.getPerson().getName().getNameString();
            msg.what = ON_SUCCESS;
            mHandler.sendMessage(msg);
            return Result.success();
        } else {
            Message msg = new Message();
            msg.obj = patientTobeUpdated.getPerson().getName().getNameString();
            msg.what = ON_FAILURE;
            mHandler.sendMessage(msg);
            return Result.retry();
        }
    }

    /**
     * Update patient.
     *
     * @param patient the patient
     * @return boolean true if success otherwise false
     */
    public boolean updatePatient(final Patient patient) {
        PatientDtoUpdate patientDto = patient.getUpdatedPatientDto();
        try {
            Response<PatientDto> response = restApi.updatePatient(patientDto, patient.getUuid(), "full").execute();

            if (!response.isSuccessful()) return false;

            PatientDto returnedPatientDto = response.body();
            patient.setBirthdate(returnedPatientDto.getPerson().getBirthdate());
            patient.setUuid(patient.getUuid());

            if (patient.getPhoto() != null) uploadPatientPhoto(patient);

            patientDAO.updatePatient(patient.getId(), patient);

            return true;
        } catch (Exception e) {
            logger.e(e.getMessage());
            return false;
        }
    }

    private void uploadPatientPhoto(final Patient patient) {
        PatientPhoto patientPhoto = new PatientPhoto();
        patientPhoto.setPhoto(patient.getPhoto());
        patientPhoto.setPerson(patient);
        Call<PatientPhoto> personPhotoCall = restApi.uploadPatientPhoto(patient.getUuid(), patientPhoto);
        personPhotoCall.enqueue(new Callback<PatientPhoto>() {
            @Override
            public void onResponse(@NonNull Call<PatientPhoto> call, @NonNull Response<PatientPhoto> response) {
                if (!response.isSuccessful()) {
                    logger.e(response.message());
                    Message msg = new Message();
                    msg.obj = response.message();
                    msg.what = ON_FAILURE_RESPONSE_PHOTO_UPDATE;
                    mHandler.sendMessage(msg);
                }
            }

            @Override
            public void onFailure(@NonNull Call<PatientPhoto> call, @NonNull Throwable t) {
                logger.e(t.getMessage());
                Message msg = new Message();
                msg.obj = t.toString();
                msg.what = ON_FAILURE_RESPONSE_PHOTO_UPDATE;
                mHandler.sendMessage(msg);
            }
        });
    }
}

