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

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Observable;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;

import com.openmrs.android_sdk.R;
import com.openmrs.android_sdk.library.OpenMRSLogger;
import com.openmrs.android_sdk.library.OpenmrsAndroid;
import com.openmrs.android_sdk.library.api.RestApi;
import com.openmrs.android_sdk.library.api.RestServiceBuilder;
import com.openmrs.android_sdk.library.api.promise.SimpleDeferredObject;
import com.openmrs.android_sdk.library.api.promise.SimplePromise;
import com.openmrs.android_sdk.library.api.services.EncounterService;
import com.openmrs.android_sdk.library.api.workers.UpdatePatientWorker;
import com.openmrs.android_sdk.library.dao.EncounterCreateRoomDAO;
import com.openmrs.android_sdk.library.dao.PatientDAO;
import com.openmrs.android_sdk.library.databases.AppDatabaseHelper;
import com.openmrs.android_sdk.library.listeners.retrofitcallbacks.DefaultResponseCallback;
import com.openmrs.android_sdk.library.listeners.retrofitcallbacks.DownloadPatientCallback;
import com.openmrs.android_sdk.library.listeners.retrofitcallbacks.PatientResponseCallback;
import com.openmrs.android_sdk.library.listeners.retrofitcallbacks.VisitsResponseCallback;
import com.openmrs.android_sdk.library.models.Encountercreate;
import com.openmrs.android_sdk.library.models.IdGenPatientIdentifiers;
import com.openmrs.android_sdk.library.models.IdentifierType;
import com.openmrs.android_sdk.library.models.Patient;
import com.openmrs.android_sdk.library.models.PatientDto;
import com.openmrs.android_sdk.library.models.PatientDtoUpdate;
import com.openmrs.android_sdk.library.models.PatientIdentifier;
import com.openmrs.android_sdk.library.models.PatientPhoto;
import com.openmrs.android_sdk.library.models.Results;
import com.openmrs.android_sdk.library.models.SystemProperty;
import com.openmrs.android_sdk.utilities.ApplicationConstants;
import com.openmrs.android_sdk.utilities.NetworkUtils;
import com.openmrs.android_sdk.utilities.ToastUtil;

import org.jdeferred.android.AndroidDeferredManager;

/**
 * The type Patient repository.
 */
@Singleton
public class PatientRepository extends BaseRepository {
    private PatientDAO patientDAO;
    private LocationRepository locationRepository;

    /**
     * Instantiates a new Patient repository.
     */
    @Inject
    public PatientRepository() {
        this.patientDAO = new PatientDAO();
        this.locationRepository = new LocationRepository();
    }

    /**
     * Instantiates a new Patient repository.
     *
     * @param logger             the logger
     * @param patientDAO         the patient dao
     * @param restApi            the rest api
     * @param locationRepository the location repository
     */
    //used in the unit tests
    public PatientRepository(OpenMRSLogger logger, PatientDAO patientDAO, RestApi restApi, LocationRepository locationRepository) {
        super(restApi, logger);
        this.patientDAO = patientDAO;
        this.locationRepository = locationRepository;
    }

    /**
     * Sync patient.
     *
     * @param patient the patient
     */
    public Observable<Patient> syncPatient(final Patient patient) {
        return AppDatabaseHelper.createObservableIO(() -> {
            if (NetworkUtils.isOnline()) {
                final List<PatientIdentifier> identifiers = new ArrayList<>();
                final PatientIdentifier identifier = new PatientIdentifier();
                identifier.setLocation(locationRepository.getLocation());
                identifier.setIdentifier(getIdGenPatientIdentifier());
                identifier.setIdentifierType(getPatientIdentifierType());
                identifiers.add(identifier);

                patient.setIdentifiers(identifiers);
                patient.setUuid(null);

                PatientDto patientDto = patient.getPatientDto();

                Response<PatientDto> response = restApi.createPatient(patientDto).execute();
                if (response.isSuccessful()) {
                    PatientDto returnedPatientDto = response.body();

                    patient.setUuid(returnedPatientDto.getUuid());
                    if (patient.getPhoto() != null) {
                        uploadPatientPhoto(patient);
                    }

                    patientDAO.updatePatient(patient.getId(), patient);
                    if (!patient.getEncounters().equals("")) {
                        addEncounters(patient);
                    }

                    return patient;
                } else {
                    throw new RuntimeException("Patient[ " + patient.getId() + "] cannot be synced due to request error");
                }
            } else {
                throw new IOException(context.getString(R.string.offline_mode_patient_data_saved_locally_notification_message));
            }
        });
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

    /**
     * Register patient.
     *
     * @param patient the patient
     */
    public Observable<Patient> registerPatient(final Patient patient) {
        return patientDAO.savePatient(patient)
                .switchMap(id -> {
                    patient.setId(id);
                    return syncPatient(patient);
                });
    }

    /**
     * Update patient.
     *
     * @param patient          the patient
     * @param callbackListener the callback listener
     */
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

    /**
     * Update matching patient.
     *
     * @param patient the locally merged patient
     */
    public Observable<Patient> updateMatchingPatient(final Patient patient){
        return AppDatabaseHelper.createObservableIO(() -> {

            PatientDtoUpdate patientDto = patient.getUpdatedPatientDto();

            Call<PatientDto> call = restApi.updatePatient(patientDto, patient.getUuid(), ApplicationConstants.API.FULL);
            Response<PatientDto> response = call.execute();

            if (response.isSuccessful()) return patient;
            else throw new IOException(response.message());
        });
    }

    /**
     * Download patient by uuid.
     *
     * @param uuid             the uuid
     * @param callbackListener the callback listener
     */
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

    /**
     * Download patient photo by uuid simple promise.
     *
     * @param uuid the uuid
     * @return the simple promise
     */
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

    /**
     * Add encounters.
     *
     * @param patient the patient
     */
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

    /**
     * Gets id gen patient identifier.
     *
     * @return the id gen patient identifier
     */
    public String getIdGenPatientIdentifier() throws IOException {
        IdGenPatientIdentifiers idList = null;

        RestApi patientIdentifierService = RestServiceBuilder.createServiceForPatientIdentifier(RestApi.class);
        Call<IdGenPatientIdentifiers> call = patientIdentifierService.getPatientIdentifiers(OpenmrsAndroid.getUsername(), OpenmrsAndroid.getPassword());

        Response<IdGenPatientIdentifiers> response = call.execute();
        if (response.isSuccessful()) {
            idList = response.body();
        }

        return idList.getIdentifiers().get(0);
    }

    /**
     * Gets patient identifier type (only has uuid).
     *
     * @return the patient identifier type
     */
    public IdentifierType getPatientIdentifierType() throws IOException {
        Call<Results<IdentifierType>> call = restApi.getIdentifierTypes();
        Response<Results<IdentifierType>> response = call.execute();
        if (response.isSuccessful()) {
            Results<IdentifierType> idResList = response.body();
            for (IdentifierType result : idResList.getResults()) {
                if (result.getDisplay().equals("OpenMRS ID")) {
                    return result;
                }
            }
        }
        return null;
    }

    /**
     * Update last viewed list.
     *
     * @param limit      the limit
     * @param startIndex the start index
     * @param callback   the callback
     */
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

    /**
     * Find patients.
     *
     * @param query    the query
     * @param callback the callback
     */
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

    /**
     * Load more patients.
     *
     * @param limit      the limit
     * @param startIndex the start index
     * @param callback   the callback
     */
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

    /**
     * Gets cause of death global id.
     *
     * @param callback the callback
     */
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

    /**
     * Fetch similar patient and calculate locally.
     *
     * @param patient  the patient
     * @param callback the callback
     */
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

    /**
     * Fetch similar patients from server.
     *
     * @param patient  the patient
     * @param callback the callback
     */
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
