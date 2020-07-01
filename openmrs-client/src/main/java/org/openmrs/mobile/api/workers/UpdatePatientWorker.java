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

package org.openmrs.mobile.api.workers;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import org.openmrs.mobile.R;
import org.openmrs.mobile.api.RestApi;
import org.openmrs.mobile.api.RestServiceBuilder;
import org.openmrs.mobile.application.OpenMRSLogger;
import org.openmrs.mobile.dao.PatientDAO;
import org.openmrs.mobile.databases.tables.PatientTable;
import org.openmrs.mobile.listeners.retrofit.DefaultResponseCallbackListener;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.models.PatientDto;
import org.openmrs.mobile.models.PatientDtoUpdate;
import org.openmrs.mobile.models.PatientPhoto;
import org.openmrs.mobile.utilities.NetworkUtils;
import org.openmrs.mobile.utilities.ToastUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdatePatientWorker extends Worker {
    private static final int ON_SUCCESS = 1;
    private static final int ON_FAILURE = 2;
    private static final int ON_UNSUCCESSFUL_RESPONSE_PHOTO_UPDATE = 3;
    private static final int ON_FAILURE_RESPONSE_PHOTO_UPDATE = 4;
    private RestApi restApi;
    private OpenMRSLogger logger;
    private Handler mHandler;

    public UpdatePatientWorker(@NonNull Context appContext, @NonNull WorkerParameters workerParams) {
        super(appContext, workerParams);
        restApi = RestServiceBuilder.createService(RestApi.class);
        logger = new OpenMRSLogger();

        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                String responseMessage;
                switch (msg.what) {
                    case ON_SUCCESS:
                        String updateSuccessPatientName = (String) msg.obj;
                        ToastUtil.success(getApplicationContext().getString(R.string.patient_update_successful, updateSuccessPatientName));
                        break;
                    case ON_FAILURE:
                        String updateFailedPatientName = (String) msg.obj;
                        ToastUtil.error(getApplicationContext().getString(R.string.patient_update_unsuccessful, updateFailedPatientName));
                        break;
                    case ON_UNSUCCESSFUL_RESPONSE_PHOTO_UPDATE:
                        responseMessage = (String) msg.obj;
                        ToastUtil.error(getApplicationContext().getString(R.string.patient_photo_update_unsuccessful, responseMessage));
                    case ON_FAILURE_RESPONSE_PHOTO_UPDATE:
                        responseMessage = (String) msg.obj;
                        ToastUtil.notify(getApplicationContext().getString(R.string.patient_photo_update_unsuccessful, responseMessage));
                }
                super.handleMessage(msg);
            }
        };
    }

    @NonNull
    @Override
    public Result doWork() {
        final boolean[] result = new boolean[1];
        String patientIdTobeUpdated = getInputData().getString(PatientTable.Column.ID);
        PatientDAO patientDAO = new PatientDAO();
        Patient patientTobeUpdated = patientDAO.findPatientByID(patientIdTobeUpdated);

        updatePatient(patientTobeUpdated, new DefaultResponseCallbackListener() {
            @Override
            public void onResponse() {
                result[0] = true;
                Message msg = new Message();
                msg.obj = patientTobeUpdated.getPerson().getName().getNameString();
                msg.what = ON_SUCCESS;
                mHandler.sendMessage(msg);
            }

            @Override
            public void onErrorResponse(String errorMessage) {
                result[0] = false;
                Message msg = new Message();
                msg.obj = patientTobeUpdated.getPerson().getName().getNameString();
                msg.what = ON_FAILURE;
                mHandler.sendMessage(msg);
            }
        });
        return result[0] ? Result.success() : Result.retry();
    }

    public void updatePatient(final Patient patient, @Nullable final DefaultResponseCallbackListener callbackListener) {
        PatientDtoUpdate patientDto = patient.getUpdatedPatientDto();
        if (NetworkUtils.isOnline()) {
            Call<PatientDto> call = restApi.updatePatient(patientDto, patient.getUuid(), "full");
            call.enqueue(new Callback<PatientDto>() {
                @Override
                public void onResponse(@NonNull Call<PatientDto> call, @NonNull Response<PatientDto> response) {
                    if (response.isSuccessful()) {
                        PatientDto patientDto = response.body();
                        patient.setBirthdate(patientDto.getPerson().getBirthdate());

                        patient.setUuid(patient.getUuid());
                        if (patient.getPhoto() != null) {
                            uploadPatientPhoto(patient);
                        }

                        new PatientDAO().updatePatient(patient.getId(), patient);

                        if (callbackListener != null) {
                            callbackListener.onResponse();
                        }
                    } else {
                        if (callbackListener != null) {
                            callbackListener.onErrorResponse(response.message());
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<PatientDto> call, @NonNull Throwable t) {
                    if (callbackListener != null) {
                        callbackListener.onErrorResponse(t.getMessage());
                    }
                }
            });
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
                logger.i(response.message());

                if (!response.isSuccessful()) {
                    Message msg = new Message();
                    msg.obj = response.message();
                    msg.what = ON_UNSUCCESSFUL_RESPONSE_PHOTO_UPDATE;
                    mHandler.sendMessage(msg);
                }
            }

            @Override
            public void onFailure(@NonNull Call<PatientPhoto> call, @NonNull Throwable t) {
                Message msg = new Message();
                msg.obj = t.toString();
                msg.what = ON_FAILURE_RESPONSE_PHOTO_UPDATE;
                mHandler.sendMessage(msg);
            }
        });
    }
}

