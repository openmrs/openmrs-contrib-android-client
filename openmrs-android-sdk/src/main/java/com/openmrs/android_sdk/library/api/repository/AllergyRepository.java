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

import static com.openmrs.android_sdk.utilities.ApplicationConstants.API.FULL;
import static com.openmrs.android_sdk.utilities.ApplicationConstants.BundleKeys.ALLERGY_UUID;
import static com.openmrs.android_sdk.utilities.ApplicationConstants.BundleKeys.PATIENT_UUID;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Observable;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;

import com.openmrs.android_sdk.R;
import com.openmrs.android_sdk.library.OpenmrsAndroid;
import com.openmrs.android_sdk.library.api.workers.allergy.DeleteAllergyWorker;
import com.openmrs.android_sdk.library.dao.AllergyRoomDAO;
import com.openmrs.android_sdk.library.databases.AppDatabaseHelper;
import com.openmrs.android_sdk.library.databases.entities.AllergyEntity;
import com.openmrs.android_sdk.library.listeners.retrofitcallbacks.DefaultResponseCallback;
import com.openmrs.android_sdk.library.models.Allergy;
import com.openmrs.android_sdk.library.models.AllergyCreate;
import com.openmrs.android_sdk.library.models.ConceptMembers;
import com.openmrs.android_sdk.library.models.Patient;
import com.openmrs.android_sdk.library.models.ResultType;
import com.openmrs.android_sdk.library.models.Results;
import com.openmrs.android_sdk.library.models.SystemProperty;
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
    public Allergy getAllergyByUUID(String allergyUuid) {
        AllergyEntity allergyEntity = allergyRoomDAO.getAllergyByUUID(allergyUuid);
        if (allergyEntity != null) {
            return AppDatabaseHelper.convert(allergyEntity);
        } else {
            return null;
        }
    }

    /**
     * Delete allergy.
     *
     * @param patientUuid the patient uuid
     * @param allergyUuid the allergy uuid
     *
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
     * Gets system property.
     *
     * @param systemProperty the system property
     * @return the system property
     */
    public LiveData<SystemProperty> getSystemProperty(String systemProperty) {
        MutableLiveData<SystemProperty> mutableLiveData = new MutableLiveData<>();
        restApi.getSystemProperty(systemProperty, FULL).enqueue(new Callback<Results<SystemProperty>>() {
            @Override
            public void onResponse(Call<Results<SystemProperty>> call, Response<Results<SystemProperty>> response) {
                if (response.isSuccessful()) {
                    mutableLiveData.setValue(response.body().getResults().get(0));
                } else {
                    mutableLiveData.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<Results<SystemProperty>> call, Throwable t) {
                mutableLiveData.setValue(null);
            }
        });
        return mutableLiveData;
    }

    /**
     * Gets concept members.
     *
     * @param uuid the uuid
     * @return the concept members
     */
    public LiveData<ConceptMembers> getConceptMembers(String uuid) {
        MutableLiveData<ConceptMembers> mutableLiveData = new MutableLiveData<>();
        restApi.getConceptMembersFromUUID(uuid).enqueue(new Callback<ConceptMembers>() {
            @Override
            public void onResponse(Call<ConceptMembers> call, Response<ConceptMembers> response) {
                if (response.isSuccessful()) {
                    mutableLiveData.setValue(response.body());
                } else {
                    mutableLiveData.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<ConceptMembers> call, Throwable t) {
                mutableLiveData.setValue(null);
            }
        });
        return mutableLiveData;
    }

    /**
     * Create allergy.
     *
     * @param patient       the patient
     * @param allergyCreate the allergy create
     * @param callback      the callback
     */
    public void createAllergy(Patient patient, AllergyCreate allergyCreate, DefaultResponseCallback callback) {
        restApi.createAllergy(patient.getUuid(), allergyCreate).enqueue(new Callback<Allergy>() {
            @Override
            public void onResponse(Call<Allergy> call, Response<Allergy> response) {
                if (response.isSuccessful()) {
                    callback.onResponse();
                    allergyRoomDAO.saveAllergy(AppDatabaseHelper.convert(response.body(), patient.getId().toString()));
                } else {
                    callback.onErrorResponse(OpenmrsAndroid.getInstance().getString(R.string.error_creating_allergy));
                }
            }

            @Override
            public void onFailure(Call<Allergy> call, Throwable t) {
                callback.onErrorResponse(t.getMessage());
            }
        });
    }

    /**
     * Update allergy.
     *
     * @param allergyUuid   the allergy uuid
     * @param id            the id
     * @param allergyCreate the allergy create
     * @param callback      the callback
     */
    public void updateAllergy(Patient patient, String allergyUuid, Long id, AllergyCreate allergyCreate, DefaultResponseCallback callback) {
        restApi.updateAllergy(patient.getUuid(), allergyUuid, allergyCreate).enqueue(new Callback<Allergy>() {
            @Override
            public void onResponse(Call<Allergy> call, Response<Allergy> response) {
                if (response.isSuccessful()) {
                    callback.onResponse();
                    AllergyEntity allergyEntity = AppDatabaseHelper.convert(response.body(), patient.getId().toString());
                    allergyEntity.setId(id);
                    allergyRoomDAO.updateAllergy(allergyEntity);
                } else {
                    callback.onErrorResponse(OpenmrsAndroid.getInstance().getString(R.string.error_creating_allergy));
                }
            }

            @Override
            public void onFailure(Call<Allergy> call, Throwable t) {
                callback.onErrorResponse(t.getMessage());
            }
        });
    }
}
