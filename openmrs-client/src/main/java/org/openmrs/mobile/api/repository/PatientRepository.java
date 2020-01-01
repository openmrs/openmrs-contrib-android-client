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

package org.openmrs.mobile.api.repository;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;


import org.jdeferred.android.AndroidDeferredManager;
import org.openmrs.mobile.api.EncounterService;
import org.openmrs.mobile.api.RestApi;
import org.openmrs.mobile.api.RestServiceBuilder;
import org.openmrs.mobile.api.promise.SimpleDeferredObject;
import org.openmrs.mobile.api.promise.SimplePromise;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.application.OpenMRSLogger;
import org.openmrs.mobile.dao.PatientDAO;
import org.openmrs.mobile.listeners.retrofit.DefaultResponseCallbackListener;
import org.openmrs.mobile.listeners.retrofit.DownloadPatientCallbackListener;
import org.openmrs.mobile.models.Encountercreate;
import org.openmrs.mobile.models.IdGenPatientIdentifiers;
import org.openmrs.mobile.models.IdentifierType;
import org.openmrs.mobile.models.Location;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.models.PatientDto;
import org.openmrs.mobile.models.PatientIdentifier;
import org.openmrs.mobile.models.PatientPhoto;
import org.openmrs.mobile.models.Results;
import org.openmrs.mobile.utilities.ActiveAndroid.query.Select;
import org.openmrs.mobile.utilities.NetworkUtils;
import org.openmrs.mobile.utilities.ToastUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.android.schedulers.AndroidSchedulers;

public class PatientRepository extends RetrofitRepository {

    private OpenMRSLogger logger;
    private PatientDAO patientDao;
    private LocationRepository locationRepository;
    private RestApi restApi;

    public PatientRepository(){
        this.logger = new OpenMRSLogger();
        this.patientDao = new PatientDAO();
        this.locationRepository = new LocationRepository();
        this.restApi = RestServiceBuilder.createService(RestApi.class);
    }

    public PatientRepository(OpenMRS openMRS, OpenMRSLogger logger, PatientDAO patientDao, RestApi restApi, LocationRepository locationRepository) {
        this.logger = logger;
        this.patientDao = patientDao;
        this.restApi = restApi;
        this.locationRepository = locationRepository;
        this.openMrs = openMRS;
    }

    /**
     * Sync Patient
     */
    public SimplePromise<Patient> syncPatient(final Patient patient) {
        return syncPatient(patient, null);
    }

    public SimplePromise<Patient> syncPatient(final Patient patient, @Nullable final DefaultResponseCallbackListener callbackListener) {
        final SimpleDeferredObject<Patient> deferred = new SimpleDeferredObject<>();

        if (NetworkUtils.isOnline()) {
            AndroidDeferredManager dm = new AndroidDeferredManager();
            dm.when(locationRepository.getLocationUuid(), getIdGenPatientIdentifier(), getPatientIdentifierTypeUuid())
                    .done(results -> {
                        final List<PatientIdentifier> identifiers = new ArrayList<>();

                        final PatientIdentifier identifier = new PatientIdentifier();
                        identifier.setLocation((Location) results.get(0).getResult());
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
                                    if (patient.getPhoto() != null)
                                        uploadPatientPhoto(patient);

                                    new PatientDAO().updatePatient(patient.getId(), patient);
                                    if(!patient.getEncounters().equals(""))
                                        addEncounters(patient);

                                    deferred.resolve(patient);

                                    if (callbackListener != null) {
                                        callbackListener.onResponse();
                                    }

                                } else {
                                    ToastUtil.error("Patient[" + patient.getId() + "] cannot be synced due to server error"+ response.message());
                                    deferred.reject(new RuntimeException("Patient cannot be synced due to server error: " + response.errorBody().toString()));
                                    if (callbackListener != null) {
                                        callbackListener.onErrorResponse(response.message());
                                    }
                                }
                            }

                            @Override
                            public void onFailure(@NonNull Call<PatientDto> call, @NonNull Throwable t) {
                                ToastUtil.notify("Patient[" + patient.getId() + "] cannot be synced due to request error: " + t.toString());
                                deferred.reject(t);
                                if (callbackListener != null) {
                                    callbackListener.onErrorResponse(t.getMessage());
                                }
                            }
                        });
                    });
        } else {
            ToastUtil.notify("Sync is off. Patient Registration data is saved locally " +
                    "and will sync when online mode is restored. ");
            if (callbackListener != null) {
                callbackListener.onResponse();
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
                    ToastUtil.error("Patient photo cannot be synced due to server error: "+ response.message());
                }
            }
            @Override
            public void onFailure(@NonNull Call<PatientPhoto> call, @NonNull Throwable t) {
                ToastUtil.notify("Patient photo cannot be synced due to error: " + t.toString() );
            }
        });
    }

    public void registerPatient(final Patient patient, @Nullable final DefaultResponseCallbackListener callbackListener) {
        patientDao.savePatient(patient)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(id -> {
                    if (callbackListener != null) {
                        syncPatient(patient, callbackListener);
                    } else {
                        syncPatient(patient);
                    }
                });
    }

    /**
     * Update Patient
     */
    public void updatePatient(final Patient patient, @Nullable final DefaultResponseCallbackListener callbackListener) {
        PatientDto patientDto = patient.getPatientDto();
        if (NetworkUtils.isOnline()) {
            Call<PatientDto> call = restApi.updatePatient(patientDto, patient.getUuid(), "full");
            call.enqueue(new Callback<PatientDto>() {
                @Override
                public void onResponse(@NonNull Call<PatientDto> call, @NonNull Response<PatientDto> response) {
                    if (response.isSuccessful()) {
                        PatientDto patientDto = response.body();
                        patient.setBirthdate(patientDto.getPerson().getBirthdate());

                        patient.setUuid(patient.getUuid());
                        if (patient.getPhoto() != null)
                            uploadPatientPhoto(patient);

                        patientDao.updatePatient(patient.getId(), patient);

                        ToastUtil.success("Patient " + patient.getPerson().getName().getNameString()
                                + " updated");
                        if (callbackListener != null) {
                            callbackListener.onResponse();
                        }
                    } else {
                        ToastUtil.error("Patient " + patient.getPerson().getName().getNameString()
                                + " cannot be updated due to server error" + response.message());
                        if (callbackListener != null) {
                            callbackListener.onErrorResponse(response.message());
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<PatientDto> call, @NonNull Throwable t) {
                    ToastUtil.notify("Patient " + patient.getPerson().getName().getNameString()
                            + " cannot be updated due to request error: " + t.toString());
                    if (callbackListener != null) {
                        callbackListener.onErrorResponse(t.getMessage());
                    }
                }
            });
        } else {
            ToastUtil.notify("Sync is off. Patient Update data is saved locally " +
                    "and will sync when online mode is restored. ");
            if (callbackListener != null) {
                callbackListener.onResponse();
            }
        }
    }

    /**
     * Download Patient by UUID
     */
    public void downloadPatientByUuid(@NonNull final String uuid, @NonNull final DownloadPatientCallbackListener callbackListener) {
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
                }
                else {
                    callbackListener.onErrorResponse(response.message());
                }
            }
            @Override
            public void onFailure(@NonNull Call<PatientDto> call, @NonNull Throwable t) {
                callbackListener.onErrorResponse(t.getMessage());
            }
        });
    }
    
    public SimplePromise<Bitmap> downloadPatientPhotoByUuid(String  uuid) {
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

    private void addEncounters(Patient patient) {
        String enc=patient.getEncounters();
        List<Long> list = new ArrayList<>();
        for (String s : enc.split(","))
            list.add(Long.parseLong(s));


        for(long id:list)
        {
            Encountercreate encountercreate = new Select()
                    .from(Encountercreate.class)
                    .where("id = ?",id)
                    .executeSingle();
            encountercreate.setPatient(patient.getUuid());
            encountercreate.save();
            new EncounterService().addEncounter(encountercreate);
        }
    }

    private SimplePromise<String> getIdGenPatientIdentifier() {
        final SimpleDeferredObject<String> deferred = new SimpleDeferredObject<>();

        RestApi apiService = RestServiceBuilder.createServiceForPatientIdentifier(RestApi.class);
        Call<IdGenPatientIdentifiers> call = apiService.getPatientIdentifiers(openMrs.getUsername(), openMrs.getPassword());
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

    private SimplePromise<IdentifierType> getPatientIdentifierTypeUuid() {
        final SimpleDeferredObject<IdentifierType> deferred = new SimpleDeferredObject<>();

        Call<Results<IdentifierType>> call = restApi.getIdentifierTypes();
        call.enqueue(new Callback<Results<IdentifierType>>() {
            @Override
            public void onResponse(@NonNull Call<Results<IdentifierType>> call, @NonNull Response<Results<IdentifierType>> response) {
                Results<IdentifierType> idresList = response.body();
                for (IdentifierType result : idresList.getResults()) {
                    if(result.getDisplay().equals("OpenMRS ID")) {
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

}
