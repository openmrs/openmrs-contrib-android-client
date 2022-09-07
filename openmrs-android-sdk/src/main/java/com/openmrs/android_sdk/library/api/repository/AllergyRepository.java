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
    private AllergyRoomDAO allergyRoomDAO;

    @Inject
    public AllergyRepository() {
        allergyRoomDAO = db.allergyRoomDAO();
    }

    /**
     * Instantiates a new Allergy repository.
     *
     * @param allergyRoomDAO the allergy room dao
     */
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
                allergyRoomDAO.deleteAllPatientAllergy(patientId);
                List<Allergy> allergies = response.body().getResults();
                for (Allergy allergy : allergies) {
                    allergyRoomDAO.saveAllergy(AppDatabaseHelper.convert(allergy, patientId));
                }
                return allergies;
            } else {
                /*
                 * WARNING: The server returns an exception when allergies haven't been checked
                 * for the patient yet. We don't receive a distinction between a patient being
                 * checked for allergies that results in (No allergies), and a patient NOT being
                 * checked for allergies so that the allergies are (UNKNOWN) yet.
                 *
                 * Until the server side fixes this issue:
                 * Don't update ROOM DB (if it already has allergy records)
                 * Don't throw an exception, as it is a wrong response
                 * Just return an empty list so that the observer behaves correctly
                 * IN THE OBSERVER RESPONSE, DO NOT ASSUME THAT (UNKNOWN) ALLERGIES ARE (NO ALLERGIES)
                 * AS THIS COULD BE UNSAFE TO THE PATIENT!
                 * */
                return Collections.emptyList();
                // Uncomment this when the server fixes the issue:
                //throw new IOException("Error with fetching allergies: " + response.message());
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
                workManager.enqueue(new OneTimeWorkRequest.Builder(DeleteAllergyWorker.class).setConstraints(constraints).setInputData(data).build());
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
