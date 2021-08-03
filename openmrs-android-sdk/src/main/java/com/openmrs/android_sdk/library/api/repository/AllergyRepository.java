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

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.openmrs.android_sdk.R;
import com.openmrs.android_sdk.library.OpenmrsAndroid;
import com.openmrs.android_sdk.library.api.RestApi;
import com.openmrs.android_sdk.library.api.RestServiceBuilder;
import com.openmrs.android_sdk.library.api.workers.allergy.DeleteAllergyWorker;
import com.openmrs.android_sdk.library.dao.AllergyRoomDAO;
import com.openmrs.android_sdk.library.databases.AppDatabase;
import com.openmrs.android_sdk.library.databases.AppDatabaseHelper;
import com.openmrs.android_sdk.library.databases.entities.AllergyEntity;
import com.openmrs.android_sdk.library.listeners.retrofitcallbacks.DefaultResponseCallback;
import com.openmrs.android_sdk.library.models.Allergy;
import com.openmrs.android_sdk.library.models.AllergyCreate;
import com.openmrs.android_sdk.library.models.ConceptMembers;
import com.openmrs.android_sdk.library.models.Results;
import com.openmrs.android_sdk.library.models.SystemProperty;
import com.openmrs.android_sdk.utilities.NetworkUtils;
import com.openmrs.android_sdk.utilities.ToastUtil;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.openmrs.android_sdk.utilities.ApplicationConstants.API.FULL;
import static com.openmrs.android_sdk.utilities.ApplicationConstants.BundleKeys.ALLERGY_UUID;
import static com.openmrs.android_sdk.utilities.ApplicationConstants.BundleKeys.PATIENT_UUID;

/**
 * The type Allergy repository.
 */
public class AllergyRepository {
    AllergyRoomDAO allergyRoomDAO;
    WorkManager workManager;
    String patientID;
    RestApi restApi;
    List<Allergy> allergyList = new ArrayList<>();
    List<AllergyEntity> allergyEntitiesOffline = new ArrayList<>();

    /**
     * Instantiates a new Allergy repository.
     *
     * @param patientID the patient id
     */
    public AllergyRepository(String patientID) {
        this.patientID = patientID;
        allergyRoomDAO = AppDatabase.getDatabase(OpenmrsAndroid.getInstance()).allergyRoomDAO();
        workManager = WorkManager.getInstance(OpenmrsAndroid.getInstance());
        restApi = RestServiceBuilder.createService(RestApi.class);
    }

    /**
     * Instantiates a new Allergy repository.
     *
     * @param id             the id
     * @param allergyRoomDAO the allergy room dao
     */
    public AllergyRepository(String id, AllergyRoomDAO allergyRoomDAO) {
        this.patientID = id;
        this.allergyRoomDAO = allergyRoomDAO;
    }

    /**
     * Gets allergies.
     *
     * @param restApi the rest api
     * @param uuid    the uuid
     * @return the allergies
     */
    public LiveData<List<Allergy>> getAllergies(RestApi restApi, String uuid) {
        MutableLiveData<List<Allergy>> allergyLiveData = new MutableLiveData<>();
        allergyEntitiesOffline = allergyRoomDAO.getAllAllergiesByPatientID(patientID);
        allergyLiveData.setValue(AppDatabaseHelper.convertTo(allergyEntitiesOffline));

        if (NetworkUtils.isOnline()) {
            restApi.getAllergies(uuid).enqueue(new Callback<Results<Allergy>>() {
                @Override
                public void onResponse(@NotNull Call<Results<Allergy>> call, @NotNull Response<Results<Allergy>> response) {
                    if (response.isSuccessful()) {
                        allergyRoomDAO.deleteAllPatientAllergy(patientID);
                        allergyList = response.body().getResults();
                        for (Allergy allergy : allergyList) {
                            allergyRoomDAO.saveAllergy(AppDatabaseHelper.convert(allergy, patientID));
                        }
                        allergyLiveData.setValue(allergyList);
                    } else {
                        ToastUtil.error(OpenmrsAndroid.getInstance().getString(R.string.unable_to_fetch_allergies));
                    }
                }

                @Override
                public void onFailure(@NotNull Call<Results<Allergy>> call, @NotNull Throwable t) {
                    ToastUtil.error(OpenmrsAndroid.getInstance().getString(R.string.unable_to_fetch_allergies));
                }
            });
        }
        return allergyLiveData;
    }

    /**
     * Gets allergy from database.
     *
     * @param patientID the patient id
     * @return the allergy from database
     */
    public List<Allergy> getAllergyFromDatabase(String patientID) {
        allergyEntitiesOffline = allergyRoomDAO.getAllAllergiesByPatientID(patientID);
        allergyList = AppDatabaseHelper.convertTo(allergyEntitiesOffline);
        return allergyList;
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
     * @param restApi     the rest api
     * @param patientUuid the patient uuid
     * @param allergyUuid the allergy uuid
     * @param callback    the callback
     */
    public void deleteAllergy(RestApi restApi, String patientUuid, String allergyUuid, DefaultResponseCallback callback) {
        if (NetworkUtils.isOnline()) {
            restApi.deleteAllergy(patientUuid, allergyUuid).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        allergyRoomDAO.deleteAllergyByUUID(allergyUuid);
                        ToastUtil.success(OpenmrsAndroid.getInstance().getString(R.string.delete_allergy_success));
                        callback.onResponse();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    callback.onErrorResponse(OpenmrsAndroid.getInstance().getString(R.string.delete_allergy_failure));
                }
            });
        } else {
            // offline deletion
            Data data = new Data.Builder()
                .putString(PATIENT_UUID, patientUuid)
                .putString(ALLERGY_UUID, allergyUuid)
                .build();
            allergyRoomDAO.deleteAllergyByUUID(allergyUuid);
            callback.onResponse();

            // enqueue the work to workManager
            Constraints constraints = new Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build();
            workManager.enqueue(new OneTimeWorkRequest.Builder(DeleteAllergyWorker.class).setConstraints(constraints).setInputData(data).build());

            ToastUtil.notify(OpenmrsAndroid.getInstance().getString(R.string.delete_allergy_offline));
        }
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
     * @param patientUuid   the patient uuid
     * @param allergyCreate the allergy create
     * @param callback      the callback
     */
    public void createAllergy(String patientUuid, AllergyCreate allergyCreate, DefaultResponseCallback callback) {
        restApi.createAllergy(patientUuid, allergyCreate).enqueue(new Callback<Allergy>() {
            @Override
            public void onResponse(Call<Allergy> call, Response<Allergy> response) {
                if (response.isSuccessful()) {
                    callback.onResponse();
                    allergyRoomDAO.saveAllergy(AppDatabaseHelper.convert(response.body(), patientID));
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
     * @param patientUuid   the patient uuid
     * @param allergyUuid   the allergy uuid
     * @param id            the id
     * @param allergyCreate the allergy create
     * @param callback      the callback
     */
    public void updateAllergy(String patientUuid, String allergyUuid, Long id, AllergyCreate allergyCreate, DefaultResponseCallback callback) {
        restApi.updateAllergy(patientUuid, allergyUuid, allergyCreate).enqueue(new Callback<Allergy>() {
            @Override
            public void onResponse(Call<Allergy> call, Response<Allergy> response) {
                if (response.isSuccessful()) {
                    callback.onResponse();
                    AllergyEntity allergyEntity = AppDatabaseHelper.convert(response.body(), patientID);
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
