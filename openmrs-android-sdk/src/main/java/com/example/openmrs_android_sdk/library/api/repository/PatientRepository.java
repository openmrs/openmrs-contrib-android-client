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

package com.example.openmrs_android_sdk.library.api.repository;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;

import com.example.openmrs_android_sdk.R;
import com.example.openmrs_android_sdk.library.OpenMRSLogger;
import com.example.openmrs_android_sdk.library.OpenmrsAndroid;
import com.example.openmrs_android_sdk.library.api.RestApi;
import com.example.openmrs_android_sdk.library.api.RestServiceBuilder;
import com.example.openmrs_android_sdk.library.api.promise.SimpleDeferredObject;
import com.example.openmrs_android_sdk.library.api.promise.SimplePromise;
import com.example.openmrs_android_sdk.library.api.services.EncounterService;
import com.example.openmrs_android_sdk.library.api.workers.UpdatePatientWorker;
import com.example.openmrs_android_sdk.library.dao.EncounterCreateRoomDAO;
import com.example.openmrs_android_sdk.library.dao.PatientDAO;
import com.example.openmrs_android_sdk.library.databases.entities.LocationEntity;
import com.example.openmrs_android_sdk.library.listeners.retrofitcallbacks.DefaultResponseCallback;
import com.example.openmrs_android_sdk.library.listeners.retrofitcallbacks.DownloadPatientCallback;
import com.example.openmrs_android_sdk.library.listeners.retrofitcallbacks.PatientDeferredResponseCallback;
import com.example.openmrs_android_sdk.library.listeners.retrofitcallbacks.PatientResponseCallback;
import com.example.openmrs_android_sdk.library.listeners.retrofitcallbacks.VisitsResponseCallback;
import com.example.openmrs_android_sdk.library.models.Encountercreate;
import com.example.openmrs_android_sdk.library.models.IdGenPatientIdentifiers;
import com.example.openmrs_android_sdk.library.models.IdentifierType;
import com.example.openmrs_android_sdk.library.models.Patient;
import com.example.openmrs_android_sdk.library.models.PatientDto;
import com.example.openmrs_android_sdk.library.models.PatientDtoUpdate;
import com.example.openmrs_android_sdk.library.models.PatientIdentifier;
import com.example.openmrs_android_sdk.library.models.PatientPhoto;
import com.example.openmrs_android_sdk.library.models.Results;
import com.example.openmrs_android_sdk.library.models.SystemProperty;
import com.example.openmrs_android_sdk.utilities.ApplicationConstants;
import com.example.openmrs_android_sdk.utilities.NetworkUtils;
import com.example.openmrs_android_sdk.utilities.ToastUtil;

import org.jdeferred.android.AndroidDeferredManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.android.schedulers.AndroidSchedulers;

public class PatientRepository extends BaseRepository {
    private PatientDAO patientDAO;
    private LocationRepository locationRepository;

    public PatientRepository() {
        this.patientDAO = new PatientDAO();
        this.locationRepository = new LocationRepository();
    }

    //used in the unit tests
    public PatientRepository(OpenMRSLogger logger, PatientDAO patientDAO, RestApi restApi, LocationRepository locationRepository) {
        super(restApi, logger);
        this.patientDAO = patientDAO;
        this.locationRepository = locationRepository;
    }

    public SimplePromise<Patient> syncPatient(final Patient patient) {
        return syncPatient(patient, null);
    }

    public SimplePromise<Patient> syncPatient(final Patient patient, @Nullable final PatientDeferredResponseCallback callback) {
        final SimpleDeferredObject<Patient> deferred = new SimpleDeferredObject<>();

        if (NetworkUtils.isOnline()) {
            AndroidDeferredManager dm = new AndroidDeferredManager();
            dm.when(locationRepository.getLocationUuid(), getIdGenPatientIdentifier(), getPatientIdentifierTypeUuid())
                    .done(results -> {
                        final List<PatientIdentifier> identifiers = new ArrayList<>();
                        final PatientIdentifier identifier = new PatientIdentifier();
                        identifier.setLocation((LocationEntity) results.get(0).getResult());
                        identifier.setIdentifier((String) results.get(1).getResult());
                        identifier.setIdentifierType((IdentifierType) results.get(2).getResult());
                        identifiers.add(identifier);

                        patient.setIdentifiers(identifiers);
                        patient.setUuid(null);

                        PatientDto patientDto = patient.getPatientDto();

                        Call<PatientDto> call = restApi.createPatient(patientDto);
                        call.enqueue(new Callback<PatientDto>() {
                            @Override
                            public void onResponse(@NonNull Call<PatientDto> call, @NonNull Response<PatientDto> response) {
                                if (response.isSuccessful()) {
                                    PatientDto newPatient = response.body();

                                    patient.setUuid(newPatient.getUuid());
                                    if (patient.getPhoto() != null) {
                                        uploadPatientPhoto(patient);
                                    }

                                    patientDAO.updatePatient(patient.getId(), patient);
                                    if (!patient.getEncounters().equals("")) {
                                        addEncounters(patient);
                                    }

                                    deferred.resolve(patient);

                                    if (callback != null) {
                                        callback.onResponse();
                                    }

                                    ToastUtil.success(context.getString(R.string.patent_data_synced_successfully));

                                } else {
                                    if (callback != null) {
                                        callback.onErrorResponse(context.getString(R.string.patient_cannot_be_synced_due_to_server_error_message, patient.getId(), response.message()));
                                    }

                                    ToastUtil.error("Patient[" + patient.getId() + "] cannot be synced due to server error" + response.message());
                                    deferred.reject(new RuntimeException("Patient cannot be synced due to server error: " + response.errorBody().toString()));

                                }
                            }

                            @Override
                            public void onFailure(@NonNull Call<PatientDto> call, @NonNull Throwable t) {
                                if (callback != null) {
                                    callback.onErrorResponse(context.getString(R.string.patient_cannot_be_synced_due_to_server_error_message, patient.getId(), t.getMessage()));
                                }

                                ToastUtil.notify("Patient[ " + patient.getId() + "] cannot be synced due to request error " + t.toString());
                                deferred.reject(t);
                            }
                        });
                    });
        } else {

            if (callback != null) {
                callback.onNotifyResponse(context.getString(R.string.offline_mode_patient_data_saved_locally_notification_message));
            }
        }

        return deferred.promise();
    }

    private void uploadPatientPhoto(final Patient patient) {
        PatientPhoto patientPhoto = new PatientPhoto();
        patientPhoto.setPhoto(patient.getPhoto());
        patientPhoto.setPerson(patient);
        Call<PatientPhoto> personPhotoCall =
                restApi.uploadPatientPhoto(patient.getUuid(), patientPhoto);
        personPhotoCall.enqueue(new Callback<PatientPhoto>() {
            @Override
            public void onResponse(@NonNull Call<PatientPhoto> call, @NonNull Response<PatientPhoto> response) {
                logger.i(response.message());
                if (!response.isSuccessful()) {

                    //string resource added "patient_photo_update_unsuccessful"
                    ToastUtil.error("Patient photo cannot be synced due to server error: " + response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<PatientPhoto> call, @NonNull Throwable t) {
                //string resource added "patient_photo_update_unsuccessful"
                ToastUtil.notify("Patient photo cannot be synced due to server error: " + t.toString());
            }
        });
    }

    public void registerPatient(final Patient patient, @Nullable final PatientDeferredResponseCallback callbackListener) {
        patientDAO.savePatient(patient)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(id -> {
                    patient.setId(id);
                    if (callbackListener != null) {
                        syncPatient(patient, callbackListener);
                    } else {
                        syncPatient(patient);
                    }
                });
    }

    public void updatePatient(final Patient patient, @Nullable final DefaultResponseCallback callbackListener) {
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

                        patientDAO.updatePatient(patient.getId(), patient);

                        ToastUtil.success("Patient " + patient.getPerson().getName().getNameString() + " Updated");
                        if (callbackListener != null) {
                            callbackListener.onResponse();
                        }
                    } else {

                        ToastUtil.error(
                                "Patient " + patient.getPerson().getName().getNameString() + " cannot be updated due to server error will retry sync " + response.message());
                        if (callbackListener != null) {
                            callbackListener.onErrorResponse(response.message());
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<PatientDto> call, @NonNull Throwable t) {
                    ToastUtil.notify("Patient[ " + patient.getId() + " ] cannot be synced due to request error" + t.toString());
                    if (callbackListener != null) {
                        callbackListener.onErrorResponse(t.getMessage());
                    }
                }
            });
        } else {
            patientDAO.updatePatient(patient.getId(), patient);

            Data data = new Data.Builder().putString("_id", patient.getId().toString()).build();
            Constraints constraints = new Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build();
            workManager.enqueue(new OneTimeWorkRequest.Builder(UpdatePatientWorker.class).setConstraints(constraints).setInputData(data).build());

            ToastUtil.notify(context.getString(R.string.offline_mode_patient_data_saved_locally_notification_message));
            if (callbackListener != null) {
                callbackListener.onResponse();
            }
        }
    }

    public void updateMatchingPatient(final Patient patient, DefaultResponseCallback callback) {
        PatientDtoUpdate patientDto = patient.getUpdatedPatientDto();
        patient.setUuid(null);
        Call<PatientDto> call = restApi.updatePatient(patientDto, patient.getUuid(), ApplicationConstants.API.FULL);
        call.enqueue(new Callback<PatientDto>() {
            @Override
            public void onResponse(@NonNull Call<PatientDto> call, @NonNull Response<PatientDto> response) {
                if (callback != null) {
                    if (response.isSuccessful()) {
                        callback.onResponse();
                    } else {
                        callback.onErrorResponse(response.message());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<PatientDto> call, @NonNull Throwable t) {
                if (callback != null) {
                    callback.onErrorResponse(t.getMessage());
                }
            }
        });
    }

    public void downloadPatientByUuid(@NonNull final String uuid, @NonNull final DownloadPatientCallback callbackListener) {
        Call<PatientDto> call = restApi.getPatientByUUID(uuid, "full");
        call.enqueue(new Callback<PatientDto>() {
            @Override
            public void onResponse(@NonNull Call<PatientDto> call, @NonNull Response<PatientDto> response) {
                if (response.isSuccessful()) {
                    final PatientDto newPatientDto = response.body();
                    AndroidDeferredManager dm = new AndroidDeferredManager();
                    dm.when(downloadPatientPhotoByUuid(newPatientDto.getUuid())).done(result -> {
                        if (result != null) {
                            newPatientDto.getPerson().setPhoto(result);
                            callbackListener.onPatientPhotoDownloaded(newPatientDto.getPatient());
                        }
                    });
                    callbackListener.onPatientDownloaded(newPatientDto.getPatient());
                } else {
                    callbackListener.onErrorResponse(response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<PatientDto> call, @NonNull Throwable t) {
                callbackListener.onErrorResponse(t.getMessage());
            }
        });
    }

    public SimplePromise<Bitmap> downloadPatientPhotoByUuid(String uuid) {
        final SimpleDeferredObject<Bitmap> deferredObject = new SimpleDeferredObject<>();
        Call<ResponseBody> call = restApi.downloadPatientPhoto(uuid);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                InputStream inputStream;
                if (response.isSuccessful()) {
                    inputStream = response.body().byteStream();
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        logger.e(e.getMessage());
                    }
                    deferredObject.resolve(bitmap);
                } else {
                    Throwable throwable = new Throwable(response.message());
                    deferredObject.reject(throwable);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                deferredObject.reject(t);
            }
        });
        return deferredObject.promise();
    }

    public void addEncounters(Patient patient) {
        EncounterCreateRoomDAO dao = db.encounterCreateRoomDAO();
        String enc = patient.getEncounters();
        List<Long> list = new ArrayList<>();
        for (String s : enc.split(","))
            list.add(Long.parseLong(s));

        for (long id : list) {
            Encountercreate encountercreate = dao.getCreatedEncountersByID(id);
            encountercreate.setPatient(patient.getUuid());
            dao.updateExistingEncounter(encountercreate);
            new EncounterService().addEncounter(encountercreate);
        }
    }

    public SimplePromise<String> getIdGenPatientIdentifier() {
        final SimpleDeferredObject<String> deferred = new SimpleDeferredObject<>();

        RestApi patientIdentifierService = RestServiceBuilder.createServiceForPatientIdentifier(RestApi.class);
        Call<IdGenPatientIdentifiers> call = patientIdentifierService.getPatientIdentifiers(OpenmrsAndroid.getUsername(), OpenmrsAndroid.getPassword());
        call.enqueue(new Callback<IdGenPatientIdentifiers>() {
            @Override
            public void onResponse(@NonNull Call<IdGenPatientIdentifiers> call, @NonNull Response<IdGenPatientIdentifiers> response) {
                IdGenPatientIdentifiers idList = response.body();
                deferred.resolve(idList.getIdentifiers().get(0));
            }

            @Override
            public void onFailure(@NonNull Call<IdGenPatientIdentifiers> call, @NonNull Throwable t) {
                ToastUtil.notify(t.toString());
                deferred.reject(t);
            }
        });

        return deferred.promise();
    }

    public SimplePromise<IdentifierType> getPatientIdentifierTypeUuid() {
        final SimpleDeferredObject<IdentifierType> deferred = new SimpleDeferredObject<>();

        Call<Results<IdentifierType>> call = restApi.getIdentifierTypes();
        call.enqueue(new Callback<Results<IdentifierType>>() {
            @Override
            public void onResponse(@NonNull Call<Results<IdentifierType>> call, @NonNull Response<Results<IdentifierType>> response) {
                Results<IdentifierType> idresList = response.body();
                for (IdentifierType result : idresList.getResults()) {
                    if (result.getDisplay().equals("OpenMRS ID")) {
                        deferred.resolve(result);
                        return;
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Results<IdentifierType>> call, @NonNull Throwable t) {
                ToastUtil.notify(t.toString());
                deferred.reject(t);
            }
        });
        return deferred.promise();
    }

    public void updateLastViewedList(int limit, int startIndex, PatientResponseCallback callback) {
        Call<Results<Patient>> call = restApi.getLastViewedPatients(limit, startIndex);
        call.enqueue(new Callback<Results<Patient>>() {
            @Override
            public void onResponse(@NonNull Call<Results<Patient>> call, @NonNull Response<Results<Patient>> response) {
                if (callback != null) {
                    if (response.isSuccessful()) {
                        callback.onResponse(response.body());
                    } else {
                        callback.onErrorResponse(response.message());
                    }
                }
            }

            @Override
            public void onFailure(Call<Results<Patient>> call, Throwable t) {
                callback.onErrorResponse(t.getMessage());
            }
        });
    }

    public void findPatients(String query, PatientResponseCallback callback) {
        Call<Results<Patient>> call = restApi.getPatients(query, ApplicationConstants.API.FULL);
        call.enqueue(new Callback<Results<Patient>>() {
            @Override
            public void onResponse(@NonNull Call<Results<Patient>> call, @NonNull Response<Results<Patient>> response) {
                if (callback != null) {
                    if (response.isSuccessful()) {
                        callback.onResponse(response.body());
                    } else {
                        callback.onErrorResponse(response.message());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Results<Patient>> call, @NonNull Throwable t) {
                callback.onErrorResponse(t.getMessage());
            }
        });
    }

    public void loadMorePatients(int limit, int startIndex, PatientResponseCallback callback) {
        Call<Results<Patient>> call = restApi.getLastViewedPatients(limit, startIndex);
        call.enqueue(new Callback<Results<Patient>>() {
            @Override
            public void onResponse(@NonNull Call<Results<Patient>> call, @NonNull Response<Results<Patient>> response) {
                if (callback != null) {
                    if (response.isSuccessful()) {
                        callback.onResponse(response.body());
                    }
                } else {
                    callback.onErrorResponse(response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Results<Patient>> call, @NonNull Throwable t) {
                callback.onErrorResponse(t.getMessage());
            }
        });
    }

    public void getCauseOfDeathGlobalID(VisitsResponseCallback callback) {
        restApi.getSystemProperty(ApplicationConstants.CAUSE_OF_DEATH, ApplicationConstants.API.FULL).enqueue(new Callback<Results<SystemProperty>>() {
            @Override
            public void onResponse(Call<Results<SystemProperty>> call, Response<Results<SystemProperty>> response) {
                if (response.isSuccessful()) {
                    String uuid = response.body().getResults().get(0).getConceptUUID();
                    callback.onSuccess(uuid);
                } else {
                    callback.onFailure(ApplicationConstants.EMPTY_STRING);
                }
            }

            @Override
            public void onFailure(Call<Results<SystemProperty>> call, Throwable t) {
                callback.onFailure(t.getMessage());
            }
        });
    }

    public void fetchSimilarPatientAndCalculateLocally(final Patient patient, PatientResponseCallback callback) {
        Call<Results<Patient>> call = restApi.getPatients(patient.getName().getGivenName(), ApplicationConstants.API.FULL);
        call.enqueue(new Callback<Results<Patient>>() {
            @Override
            public void onResponse(@NonNull Call<Results<Patient>> call, @NonNull Response<Results<Patient>> response) {
                if (response.isSuccessful()) {
                    callback.onResponse(response.body());
                } else {
                    callback.onErrorResponse(response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Results<Patient>> call, @NonNull Throwable t) {
                callback.onErrorResponse(t.getMessage());
            }
        });
    }

    public void fetchSimilarPatientsFromServer(final Patient patient, PatientResponseCallback callback) {
        Call<Results<Patient>> call = restApi.getSimilarPatients(patient.toMap());
        call.enqueue(new Callback<Results<Patient>>() {
            @Override
            public void onResponse(@NonNull Call<Results<Patient>> call, @NonNull Response<Results<Patient>> response) {
                if (response.isSuccessful()) {
                    callback.onResponse(response.body());
                } else {
                    callback.onErrorResponse(response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Results<Patient>> call, @NonNull Throwable t) {
                callback.onErrorResponse(t.getMessage());
            }
        });
    }
}
