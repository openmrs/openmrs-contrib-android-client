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

package com.openmrs.android_sdk.library.api.repository;

import static com.openmrs.android_sdk.utilities.ApplicationConstants.BundleKeys.ALLERGY_UUID;
import static com.openmrs.android_sdk.utilities.ApplicationConstants.BundleKeys.PATIENT_UUID;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Response;
import rx.Observable;

import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;

import com.openmrs.android_sdk.library.api.workers.allergy.DeleteAllergyWorker;
import com.openmrs.android_sdk.library.dao.AllergyRoomDAO;
import com.openmrs.android_sdk.library.databases.AppDatabaseHelper;
import com.openmrs.android_sdk.library.databases.entities.AllergyEntity;
import com.openmrs.android_sdk.library.models.Allergy;
import com.openmrs.android_sdk.library.models.AllergyCreate;
import com.openmrs.android_sdk.library.models.Patient;
import com.openmrs.android_sdk.library.models.ResultType;
import com.openmrs.android_sdk.library.models.Results;
import com.openmrs.android_sdk.utilities.NetworkUtils;

/**
 * The type Allergy repository.
 */
@Singleton
public class AllergyRepository extends BaseRepository {
    private final AllergyRoomDAO allergyRoomDAO;

    @Inject
    public AllergyRepository(AllergyRoomDAO allergyRoomDAO) {
        this.allergyRoomDAO = allergyRoomDAO;
    }

    /**
     * Synchronizes allergies from server.
     *
     * @param patient the patient to get allergies from
     * @return the allergies observable
     */
    public Observable<List<Allergy>> syncAllergies(Patient patient) {
        return AppDatabaseHelper.createObservableIO(() -> {
            String patientId = patient.getId().toString();
            Response<Results<Allergy>> response = restApi.getAllergies(patient.getUuid()).execute();

            if (response.isSuccessful()) {
                // The patient has not been tested for allergies, yet ("Unknown" status in the server):
                if (response.body() == null) return Collections.emptyList();
                // The patient has been tested for allergies:
                List<Allergy> allergies = response.body().getResults();
                allergyRoomDAO.deleteAllPatientAllergy(patientId);
                for (Allergy allergy : allergies) {
                    allergyRoomDAO.saveAllergy(AppDatabaseHelper.convert(allergy, patientId));
                }
                return allergies;
            } else {
                getLogger().e("Error with fetching allergies: " + response.message());
                throw new Exception("Error with fetching allergies: " + response.message());
            }
        });
    }

    /**
     * Gets allergy from database.
     *
     * @param patientID the patient id
     * @return the allergy from database
     */
    public Observable<List<Allergy>> getAllergyFromDatabase(String patientID) {
        return AppDatabaseHelper.createObservableIO(() -> {
            List<AllergyEntity> offlineAllergyEntities = allergyRoomDAO.getAllAllergiesByPatientID(patientID);
            return AppDatabaseHelper.convertTo(offlineAllergyEntities);
        });
    }

    /**
     * Gets allergy by uuid.
     *
     * @param allergyUuid the allergy uuid
     * @return the allergy by uuid
     */
    public Observable<Allergy> getAllergyByUUID(String allergyUuid) {
        return AppDatabaseHelper.createObservableIO(() -> {
            AllergyEntity allergyEntity = allergyRoomDAO.getAllergyByUUID(allergyUuid);
            return AppDatabaseHelper.convert(allergyEntity);
        });
    }

    /**
     * Delete allergy.
     *
     * @param patientUuid the patient uuid
     * @param allergyUuid the allergy uuid
     * @return observable true if allergy is deleted from server, and false if only deleted locally
     */
    public Observable<ResultType> deleteAllergy(String patientUuid, String allergyUuid) {
        return AppDatabaseHelper.createObservableIO(() -> {
            if (NetworkUtils.isOnline()) {
                Response<ResponseBody> response = restApi.deleteAllergy(patientUuid, allergyUuid).execute();
                if (response.isSuccessful()) {
                    allergyRoomDAO.deleteAllergyByUUID(allergyUuid);
                    return ResultType.AllergyDeletionSuccess;
                } else {
                    throw new IOException(response.message());
                }
            } else {
                // offline deletion
                Data data = new Data.Builder()
                        .putString(PATIENT_UUID, patientUuid)
                        .putString(ALLERGY_UUID, allergyUuid)
                        .build();
                allergyRoomDAO.deleteAllergyByUUID(allergyUuid);

                // enqueue the work to workManager
                Constraints constraints = new Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build();
                getWorkManager().enqueue(new OneTimeWorkRequest.Builder(DeleteAllergyWorker.class).setConstraints(constraints).setInputData(data).build());
                return ResultType.AllergyDeletionLocalSuccess;
            }
        });
    }

    /**
     * Creates an allergy for a patient.
     *
     * @param patient       the patient to create an allergy for
     * @param allergyCreate the allergy create object containing allergy properties
     * @return Observable boolean true if successful
     */
    public Observable<Boolean> createAllergy(Patient patient, AllergyCreate allergyCreate) {
        return AppDatabaseHelper.createObservableIO(() -> {
            Response<Allergy> response = restApi.createAllergy(patient.getUuid(), allergyCreate).execute();
            if (response.isSuccessful()) {
                AllergyEntity allergyEntity = AppDatabaseHelper.convert(response.body(), patient.getId().toString());
                allergyRoomDAO.saveAllergy(allergyEntity);
                return true;
            } else {
                throw new Exception("createAllergy error: " + response.message());
            }
        });
    }

    /**
     * Updates an existing allergy of a patient.
     *
     * @param allergyUuid   Uuid of the allergy to be updated
     * @param id            the id
     * @param allergyCreate the allergy create
     * @return observable boolean true if update is successful
     */
    public Observable<Boolean> updateAllergy(Patient patient, String allergyUuid, Long id, AllergyCreate allergyCreate) {
        return AppDatabaseHelper.createObservableIO(() -> {
            Response<Allergy> response = restApi.updateAllergy(patient.getUuid(), allergyUuid, allergyCreate).execute();
            if (response.isSuccessful()) {
                AllergyEntity allergyEntity = AppDatabaseHelper.convert(response.body(), patient.getId().toString());
                allergyEntity.setId(id);
                allergyRoomDAO.updateAllergy(allergyEntity);
                return true;
            } else {
                throw new Exception("updateAllergy error: " + response.message());
            }
        });
    }
}
