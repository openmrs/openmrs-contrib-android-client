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

package org.openmrs.mobile.api.retrofit;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.activeandroid.query.Select;

import org.jdeferred.DoneCallback;
import org.jdeferred.android.AndroidDeferredManager;
import org.jdeferred.multiple.MultipleResults;
import org.openmrs.mobile.api.EncounterService;
import org.openmrs.mobile.api.RestApi;
import org.openmrs.mobile.api.RestServiceBuilder;
import org.openmrs.mobile.api.promise.SimpleDeferredObject;
import org.openmrs.mobile.api.promise.SimplePromise;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.dao.PatientDAO;
import org.openmrs.mobile.listeners.retrofit.DefaultResponseCallbackListener;
import org.openmrs.mobile.listeners.retrofit.DownloadPatientCallbackListener;
import org.openmrs.mobile.models.Location;
import org.openmrs.mobile.models.retrofit.Encountercreate;
import org.openmrs.mobile.models.retrofit.IdGenPatientIdentifiers;
import org.openmrs.mobile.models.retrofit.IdentifierType;
import org.openmrs.mobile.models.retrofit.Patient;
import org.openmrs.mobile.models.retrofit.PatientIdentifier;
import org.openmrs.mobile.models.retrofit.Results;
import org.openmrs.mobile.utilities.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PatientApi extends RetrofitApi {

    public static final String FULL_REPRESENTATION = "full";

    private PatientDAO patientDao = new PatientDAO();

    /**
     * Sync Patient
     */
    public SimplePromise<Patient> syncPatient(final Patient patient) {
        return syncPatient(patient, null);
    }

    public SimplePromise<Patient> syncPatient(final Patient patient, @Nullable final DefaultResponseCallbackListener callbackListener) {
        final SimpleDeferredObject<Patient> deferred = new SimpleDeferredObject<>();

        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(OpenMRS.getInstance());
        Boolean syncstate = prefs.getBoolean("sync", true);

        if (syncstate) {
            AndroidDeferredManager dm = new AndroidDeferredManager();
            dm.when(new LocationApi().getLocationUuid(), getIdGenPatientIdentifier(), getPatientIdentifierTypeUuid())
                    .done(new DoneCallback<MultipleResults>() {
                        @Override
                        public void onDone(final MultipleResults results) {
                            final List<PatientIdentifier> identifiers = new ArrayList<>();

                            final PatientIdentifier identifier = new PatientIdentifier();
                            identifier.setLocation((Location) results.get(0).getResult());
                            identifier.setIdentifier((String) results.get(1).getResult());
                            identifier.setIdentifierType((IdentifierType) results.get(2).getResult());
                            identifiers.add(identifier);

                            patient.setIdentifiers(identifiers);
                            patient.setUuid(null);

                            final RestApi apiService =
                                    RestServiceBuilder.createService(RestApi.class);
                            Call<Patient> call = apiService.createPatient(patient);
                            call.enqueue(new Callback<Patient>() {
                                @Override
                                public void onResponse(Call<Patient> call, Response<Patient> response) {
                                    if (response.isSuccessful()) {
                                        Patient newPatient = response.body();

                                        ToastUtil.success("Patient " +patient.getPerson().getName().getNameString()
                                                +" created with UUID " + newPatient.getUuid());

                                        patient.setSynced(true);
                                        patient.setUuid(newPatient.getUuid());

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
                                            callbackListener.onErrorResponse();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<Patient> call, Throwable t) {
                                    ToastUtil.notify("Patient[" + patient.getId() + "] cannot be synced due to request error: " + t.toString());
                                    deferred.reject(t);
                                    if (callbackListener != null) {
                                        callbackListener.onErrorResponse();
                                    }
                                }
                            });
                        }
                    });
        } else {
            ToastUtil.notify("Sync is off. Patient Registration data is saved locally " +
                    "and will sync when online mode is restored. ");
            if (callbackListener != null) {
                callbackListener.onErrorResponse();
            }
        }

        return deferred.promise();
    }

    /**
     * Register Patient
     */
    public SimplePromise<Patient> registerPatient(final Patient patient) {
        return registerPatient(patient, null);
    }

    public SimplePromise<Patient> registerPatient(final Patient patient, @Nullable final DefaultResponseCallbackListener callbackListener) {
        patient.setSynced(false);
        patientDao.savePatient(patient);
        if (callbackListener != null) {
            return syncPatient(patient, callbackListener);
        }
        else {
            return syncPatient(patient);
        }
    }

    /**
     * Download Patient by UUID
     */
    public void downloadPatientByUuid(@NonNull final String uuid, @NonNull final DownloadPatientCallbackListener callbackListener) {
        RestApi restApi = RestServiceBuilder.createService(RestApi.class);
        Call<Patient> call = restApi.getPatientByUUID(uuid, "full");
        call.enqueue(new Callback<Patient>() {
            @Override
            public void onResponse(Call<Patient> call, Response<Patient> response) {
                if (response.isSuccessful()) {
                    callbackListener.onPatientDownloaded(response.body());
                }
                else {
                    callbackListener.onErrorResponse();
                }
            }
            @Override
            public void onFailure(Call<Patient> call, Throwable t) {
                callbackListener.onErrorResponse();
            }
        });
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

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(openMrs.getServerUrl() + '/')
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RestApi apiService =
                retrofit.create(RestApi.class);
        Call<IdGenPatientIdentifiers> call = apiService.getPatientIdentifiers(openMrs.getUsername(), openMrs.getPassword());
        call.enqueue(new Callback<IdGenPatientIdentifiers>() {
            @Override
            public void onResponse(Call<IdGenPatientIdentifiers> call, Response<IdGenPatientIdentifiers> response) {
                IdGenPatientIdentifiers idList = response.body();
                deferred.resolve(idList.getIdentifiers().get(0));
            }

            @Override
            public void onFailure(Call<IdGenPatientIdentifiers> call, Throwable t) {
                ToastUtil.notify(t.toString());
                deferred.reject(t);
            }

        });

        return deferred.promise();
    }

    private SimplePromise<IdentifierType> getPatientIdentifierTypeUuid() {
        final SimpleDeferredObject<IdentifierType> deferred = new SimpleDeferredObject<>();

        RestApi apiService =
                RestServiceBuilder.createService(RestApi.class);
        Call<Results<IdentifierType>> call = apiService.getIdentifierTypes();
        call.enqueue(new Callback<Results<IdentifierType>>() {
            @Override
            public void onResponse(Call<Results<IdentifierType>> call, Response<Results<IdentifierType>> response) {
                Results<IdentifierType> idresList = response.body();
                for (IdentifierType result : idresList.getResults()) {
                    if(result.getDisplay().equals("OpenMRS ID")) {
                        deferred.resolve(result);
                        return;
                    }
                }
            }

            @Override
            public void onFailure(Call<Results<IdentifierType>> call, Throwable t) {
                ToastUtil.notify(t.toString());
                deferred.reject(t);
            }

        });
        return deferred.promise();
    }

}
