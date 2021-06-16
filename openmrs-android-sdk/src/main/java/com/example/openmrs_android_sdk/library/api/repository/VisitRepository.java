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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.openmrs_android_sdk.library.OpenMRSLogger;
import com.example.openmrs_android_sdk.library.OpenmrsAndroid;
import com.example.openmrs_android_sdk.library.dao.EncounterCreateRoomDAO;
import com.example.openmrs_android_sdk.library.dao.EncounterDAO;
import com.example.openmrs_android_sdk.library.dao.LocationDAO;
import com.example.openmrs_android_sdk.library.dao.VisitDAO;
import com.example.openmrs_android_sdk.library.models.Encounter;
import com.example.openmrs_android_sdk.library.models.Encountercreate;
import com.example.openmrs_android_sdk.library.models.Patient;
import com.example.openmrs_android_sdk.library.models.Results;
import com.example.openmrs_android_sdk.library.models.Visit;
import com.example.openmrs_android_sdk.library.models.VisitType;
import com.example.openmrs_android_sdk.utilities.ApplicationConstants;
import com.example.openmrs_android_sdk.utilities.DateUtils;

import com.example.openmrs_android_sdk.library.api.RestApi;
import com.example.openmrs_android_sdk.library.listeners.retrofitcallbacks.DefaultResponseCallback;
import com.example.openmrs_android_sdk.library.listeners.retrofitcallbacks.GetVisitTypeCallback;
import com.example.openmrs_android_sdk.library.listeners.retrofitcallbacks.StartVisitResponseCallback;
import com.example.openmrs_android_sdk.library.listeners.retrofitcallbacks.VisitsResponseCallback;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

public class VisitRepository extends BaseRepository {
    LocationDAO locationDAO;
    private VisitDAO visitDAO;
    private EncounterDAO encounterDAO;
    private EncounterCreateRoomDAO encounterCreateRoomDAO;

    public VisitRepository() {
        visitDAO = new VisitDAO();
        encounterDAO = new EncounterDAO();
        encounterCreateRoomDAO = db.encounterCreateRoomDAO();
        locationDAO = new LocationDAO();
    }

    /**
     * used in Unit tests with mockUp objects
     *  @param restApi
     * @param visitDAO
     * @param locationDAO
     * @param encounterDAO
     */
    public VisitRepository(OpenMRSLogger logger, RestApi restApi, VisitDAO visitDAO, LocationDAO locationDAO, EncounterDAO encounterDAO) {
        super(restApi, logger);
        this.visitDAO = visitDAO;
        this.encounterDAO = encounterDAO;
        this.locationDAO = locationDAO;
    }

    public void syncVisitsData(@NonNull Patient patient) {
        syncVisitsData(patient, null);
    }

    public void syncVisitsData(@NonNull final Patient patient, @Nullable final DefaultResponseCallback callbackListener) {
        Call<Results<Visit>> call = restApi.findVisitsByPatientUUID(patient.getUuid(), "custom:(uuid,location:ref,visitType:ref,startDatetime,stopDatetime,encounters:full)");
        call.enqueue(new Callback<Results<Visit>>() {
            @Override
            public void onResponse(@NonNull Call<Results<Visit>> call, @NonNull Response<Results<Visit>> response) {
                if (response.isSuccessful()) {
                    List<Visit> visits = response.body().getResults();
                    Observable.just(visits)
                        .flatMap(Observable::from)
                        .forEach(visit ->
                                visitDAO.saveOrUpdate(visit, patient.getId())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(),
                            error -> error.printStackTrace()
                        );
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
            public void onFailure(@NonNull Call<Results<Visit>> call, @NonNull Throwable t) {
                if (callbackListener != null) {
                    callbackListener.onErrorResponse(t.getMessage());
                }
            }
        });
    }

    public void getVisitType(final GetVisitTypeCallback callbackListener) {
        Call<Results<VisitType>> call = restApi.getVisitType();
        call.enqueue(new Callback<Results<VisitType>>() {
            @Override
            public void onResponse(@NonNull Call<Results<VisitType>> call, @NonNull Response<Results<VisitType>> response) {
                if (response.isSuccessful()) {
                    callbackListener.onGetVisitTypeResponse(response.body().getResults().get(0));
                } else {
                    callbackListener.onErrorResponse(response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Results<VisitType>> call, @NonNull Throwable t) {
                callbackListener.onErrorResponse(t.getMessage());
            }
        });
    }

    public void syncLastVitals(final String patientUuid) {
        syncLastVitals(patientUuid, null);
    }

    public void syncLastVitals(final String patientUuid, @Nullable final DefaultResponseCallback callbackListener) {
        Call<Results<Encounter>> call = restApi.getLastVitals(patientUuid, ApplicationConstants.EncounterTypes.VITALS, "full", 1, "desc");
        call.enqueue(new Callback<Results<Encounter>>() {
            @Override
            public void onResponse(@NonNull Call<Results<Encounter>> call, @NonNull Response<Results<Encounter>> response) {
                if (response.isSuccessful()) {
                    if (!response.body().getResults().isEmpty()) {
                        encounterDAO.saveLastVitalsEncounter(response.body().getResults().get(0), patientUuid);
                    }
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
            public void onFailure(@NonNull Call<Results<Encounter>> call, @NonNull Throwable t) {
                if (callbackListener != null) {
                    callbackListener.onErrorResponse(t.getMessage());
                }
            }
        });
    }

    public void endVisitByUuid(String uuid, Visit visit, VisitsResponseCallback callbackListener) {
        restApi.endVisitByUUID(uuid, visit).enqueue(new Callback<Visit>() {
            @Override
            public void onResponse(@NonNull Call<Visit> call, @NonNull Response<Visit> response) {
                if (callbackListener != null) {
                    if (response.isSuccessful()) {
                        callbackListener.onSuccess(response.body().getStopDatetime());
                    } else {
                        callbackListener.onFailure(response.message());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Visit> call, @NonNull Throwable t) {
                if (callbackListener != null) {
                    callbackListener.onFailure(t.getMessage());
                }
            }
        });
    }

    public void startVisit(final Patient patient) {
        startVisit(patient, null);
    }

    public void startVisit(final Patient patient, @Nullable final StartVisitResponseCallback callbackListener) {
        final Visit visit = new Visit();
        visit.setStartDatetime(DateUtils.convertTime(System.currentTimeMillis(), DateUtils.OPEN_MRS_REQUEST_FORMAT));
        visit.setPatient(patient);
        visit.setLocation(locationDAO.findLocationByName(OpenmrsAndroid.getLocation()));

        visit.setVisitType(new VisitType("", OpenmrsAndroid.getVisitTypeUUID()));

        Call<Visit> call = restApi.startVisit(visit);
        call.enqueue(new Callback<Visit>() {
            @Override
            public void onResponse(@NonNull Call<Visit> call, @NonNull Response<Visit> response) {
                if (response.isSuccessful()) {
                    Visit newVisit = response.body();
                    visitDAO.saveOrUpdate(newVisit, patient.getId())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(id -> {
                            if (callbackListener != null) {
                                callbackListener.onStartVisitResponse(id);
                            }
                        });
                } else {
                    if (callbackListener != null) {
                        callbackListener.onErrorResponse(response.message());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Visit> call, @NonNull Throwable t) {
                if (callbackListener != null) {
                    callbackListener.onErrorResponse(t.getMessage());
                }
            }
        });
    }

    public long addEncounterCreated(final Encountercreate encountercreate) {
        return encounterCreateRoomDAO.addEncounterCreated(encountercreate);
    }
}
