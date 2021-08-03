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

package org.openmrs.mobile.api.workers.patient;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.openmrs.android_sdk.library.OpenMRSLogger;
import com.openmrs.android_sdk.library.api.RestApi;
import com.openmrs.android_sdk.library.api.RestServiceBuilder;
import com.openmrs.android_sdk.library.dao.PatientDAO;
import com.openmrs.android_sdk.library.models.Patient;
import com.openmrs.android_sdk.library.models.PatientDto;
import com.openmrs.android_sdk.library.models.PatientDtoUpdate;
import com.openmrs.android_sdk.library.models.PatientPhoto;
import com.openmrs.android_sdk.utilities.ApplicationConstants;
import com.openmrs.android_sdk.utilities.NetworkUtils;
import com.openmrs.android_sdk.utilities.ToastUtil;

import org.openmrs.mobile.R;
import java.io.IOException;

import retrofit2.Response;

public class UpdatePatientWorker extends Worker {
    private RestApi restApi;
    private OpenMRSLogger logger;
    private PatientDAO patientDAO;

    public UpdatePatientWorker(@NonNull Context appContext, @NonNull WorkerParameters workerParams) {
        super(appContext, workerParams);
        restApi = RestServiceBuilder.createService(RestApi.class);
        logger = new OpenMRSLogger();
        patientDAO = new PatientDAO();
    }

    @NonNull
    @Override
    public Result doWork() {
        String patientIdTobeUpdated = getInputData().getString(ApplicationConstants.PRIMARY_KEY_ID);
        Patient patientTobeUpdated = patientDAO.findPatientByID(patientIdTobeUpdated);

        boolean result = updatePatient(patientTobeUpdated);

        if (result)
            return Result.success();
        else
            return Result.failure();
    }

    public boolean updatePatient(final Patient patient) {
        PatientDtoUpdate patientDto = patient.getUpdatedPatientDto();
        if (NetworkUtils.isOnline()) {

            try {
                Response<PatientDto> response = restApi.updatePatient(patientDto, patient.getUuid(), "full").execute();

                if (response.isSuccessful()) {
                    PatientDto responsePatientDto = response.body();
                    patient.setBirthdate(responsePatientDto.getPerson().getBirthdate());

                    patient.setUuid(patient.getUuid());
                    if (patient.getPhoto() != null) {
                        uploadPatientPhoto(patient);
                    }
                    patientDAO.updatePatient(patient.getId(), patient);

                    new Handler(Looper.getMainLooper()).post(() -> {
                        ToastUtil.success(getApplicationContext().getString(R.string.patient_update_successful, patient.getDisplay()));
                    });
                    return true;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private void uploadPatientPhoto(final Patient patient) {
        PatientPhoto patientPhoto = new PatientPhoto();
        patientPhoto.setPhoto(patient.getPhoto());
        patientPhoto.setPerson(patient);

        try {
            Response<PatientPhoto> response = restApi.uploadPatientPhoto(patient.getUuid(), patientPhoto).execute();

            if (response.isSuccessful()) {
                logger.i(response.message());
            } else {
                new Handler(Looper.getMainLooper()).post(() -> {
                    ToastUtil.error(getApplicationContext().getString(R.string.patient_photo_update_unsuccessful, response.message()));
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

