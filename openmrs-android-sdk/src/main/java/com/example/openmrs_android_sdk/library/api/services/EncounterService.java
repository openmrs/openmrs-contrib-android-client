/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package com.example.openmrs_android_sdk.library.api.services;

import android.app.IntentService;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.openmrs_android_sdk.R;
import com.example.openmrs_android_sdk.library.OpenmrsAndroid;
import com.example.openmrs_android_sdk.library.api.RestApi;
import com.example.openmrs_android_sdk.library.api.RestServiceBuilder;
import com.example.openmrs_android_sdk.library.api.repository.VisitRepository;
import com.example.openmrs_android_sdk.library.dao.PatientDAO;
import com.example.openmrs_android_sdk.library.dao.VisitDAO;
import com.example.openmrs_android_sdk.library.databases.AppDatabase;
import com.example.openmrs_android_sdk.library.listeners.retrofitcallbacks.DefaultResponseCallback;
import com.example.openmrs_android_sdk.library.listeners.retrofitcallbacks.StartVisitResponseCallback;
import com.example.openmrs_android_sdk.library.models.Encounter;
import com.example.openmrs_android_sdk.library.models.EncounterType;
import com.example.openmrs_android_sdk.library.models.Encountercreate;
import com.example.openmrs_android_sdk.utilities.NetworkUtils;
import com.example.openmrs_android_sdk.utilities.ToastUtil;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.android.schedulers.AndroidSchedulers;

public class EncounterService extends IntentService {
    private final RestApi apiService = RestServiceBuilder.createService(RestApi.class);

    public EncounterService() {
        super("Save Encounter");
    }

    public void addEncounter(final Encountercreate encountercreate, @Nullable DefaultResponseCallback callbackListener) {

        if (NetworkUtils.isOnline()) {
            new VisitDAO().getActiveVisitByPatientId(encountercreate.getPatientId())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(visit -> {
                        if (visit != null) {
                            encountercreate.setVisit(visit.getUuid());
                            if (callbackListener != null) {
                                syncEncounter(encountercreate, callbackListener);
                            } else {
                                syncEncounter(encountercreate);
                            }
                        } else {

                            startNewVisitForEncounter(encountercreate);
                        }
                    });
        } else {
            ToastUtil.error(getString(R.string.form_data_will_be_synced_later_error_message));
        }
    }

    public void addEncounter(final Encountercreate encountercreate) {
        addEncounter(encountercreate, null);
    }

    private void startNewVisitForEncounter(final Encountercreate encountercreate, @Nullable final DefaultResponseCallback callbackListener) {
        new VisitRepository().startVisit(new PatientDAO().findPatientByUUID(encountercreate.getPatient()),
                new StartVisitResponseCallback() {
                    @Override
                    public void onStartVisitResponse(long id) {
                        new VisitDAO().getVisitByID(id)
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(visit -> {
                                    encountercreate.setVisit(visit.getUuid());
                                    if (callbackListener != null) {
                                        syncEncounter(encountercreate, callbackListener);
                                    } else {
                                        syncEncounter(encountercreate);
                                    }
                                });
                    }

                    @Override
                    public void onResponse() {
                        // This method is intentionally empty
                    }

                    @Override
                    public void onErrorResponse(String errorMessage) {
                        ToastUtil.error(errorMessage);
                    }
                });
    }

    public void startNewVisitForEncounter(final Encountercreate encountercreate) {
        startNewVisitForEncounter(encountercreate, null);
    }

    public void syncEncounter(final Encountercreate encountercreate, @Nullable final DefaultResponseCallback callbackListener) {

        if (NetworkUtils.isOnline()) {

            Call<Encounter> call = apiService.createEncounter(encountercreate);
            call.enqueue(new Callback<Encounter>() {
                @Override
                public void onResponse(@NonNull Call<Encounter> call, @NonNull Response<Encounter> response) {
                    if (response.isSuccessful()) {
                        Encounter encounter = response.body();
                        linkvisit(encountercreate.getPatientId(), encountercreate.getFormname(), encounter, encountercreate);
                        encountercreate.setSynced(true);
                        AppDatabase.getDatabase(OpenmrsAndroid.getInstance().getApplicationContext())
                                .encounterCreateRoomDAO()
                                .updateExistingEncounter(encountercreate);
                        new VisitRepository().syncLastVitals(encountercreate.getPatient());
                        if (callbackListener != null) {
                            callbackListener.onResponse();
                        }
                    } else {
                        if (callbackListener != null) {
                            callbackListener.onErrorResponse(response.errorBody().toString());
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Encounter> call, @NonNull Throwable t) {
                    if (callbackListener != null) {
                        callbackListener.onErrorResponse(t.getLocalizedMessage());
                    }
                }
            });
        } else {
            ToastUtil.error(getString(R.string.form_data_sync_is_off_message));
        }
    }

    public void syncEncounter(final Encountercreate encountercreate) {
        syncEncounter(encountercreate, null);
    }

    private void linkvisit(Long patientid, String formname, Encounter encounter, Encountercreate encountercreate) {
        VisitDAO visitDAO = new VisitDAO();
        visitDAO.getVisitByUuid(encounter.getVisit().getUuid())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(visit -> {
                    encounter.setEncounterType(new EncounterType(formname));
                    for (int i = 0; i < encountercreate.getObservations().size(); i++) {
                        encounter.getObservations().get(i).setDisplayValue
                                (encountercreate.getObservations().get(i).getValue());
                    }
                    List<Encounter> encounterList = visit.getEncounters();
                    encounterList.add(encounter);
                    visitDAO.saveOrUpdate(visit, patientid)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe();
                });
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (NetworkUtils.isOnline()) {

            List<Encountercreate> encountercreatelist = AppDatabase.getDatabase(OpenmrsAndroid.getInstance().getApplicationContext())
                    .encounterCreateRoomDAO()
                    .getAllCreatedEncounters();

            for (final Encountercreate encountercreate : encountercreatelist) {
                if (!encountercreate.getSynced() &&
                        new PatientDAO().findPatientByID(Long.toString(encountercreate.getPatientId())).isSynced()) {
                    new VisitDAO().getActiveVisitByPatientId(encountercreate.getPatientId())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(visit -> {
                                if (visit != null) {
                                    encountercreate.setVisit(visit.getUuid());
                                    syncEncounter(encountercreate);
                                } else {
                                    startNewVisitForEncounter(encountercreate);
                                }
                            });
                }
            }
        } else {
            ToastUtil.error(getString(R.string.form_data_will_be_synced_later_error_message));
        }
    }
}