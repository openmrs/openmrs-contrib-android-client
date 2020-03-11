/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.mobile.api;

import android.app.IntentService;
import android.content.Intent;

import org.openmrs.mobile.api.repository.VisitRepository;
import org.openmrs.mobile.dao.PatientDAO;
import org.openmrs.mobile.dao.VisitDAO;
import org.openmrs.mobile.listeners.retrofit.DefaultResponseCallbackListener;
import org.openmrs.mobile.listeners.retrofit.StartVisitResponseListenerCallback;
import org.openmrs.mobile.models.Encounter;
import org.openmrs.mobile.models.EncounterType;
import org.openmrs.mobile.models.Encountercreate;
import org.openmrs.mobile.utilities.ActiveAndroid.query.Select;
import org.openmrs.mobile.utilities.NetworkUtils;
import org.openmrs.mobile.utilities.ToastUtil;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.android.schedulers.AndroidSchedulers;

public class EncounterService extends IntentService {

    private final RestApi apiService = RestServiceBuilder.createService(RestApi.class);

    public EncounterService() {
        super("Save Encounter");
    }

    public void addEncounter(final Encountercreate encountercreate, @Nullable DefaultResponseCallbackListener callbackListener) {

        if(NetworkUtils.isOnline()) {
            new VisitDAO().getActiveVisitByPatientId(encountercreate.getPatientId())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(visit -> {
                        if (visit != null) {
                            encountercreate.setVisit(visit.getUuid());
                            if (callbackListener != null) {
                                syncEncounter(encountercreate, callbackListener);
                            }
                            else {
                                syncEncounter(encountercreate);
                            }
                        } else {

                            startNewVisitForEncounter(encountercreate);
                        }
                    });
        }
        else
            ToastUtil.error("No internet connection. Form data is saved locally " +
                    "and will sync when internet connection is restored. ");
    }

    public void addEncounter(final Encountercreate encountercreate) {
        addEncounter(encountercreate, null);
    }

        private void startNewVisitForEncounter(final Encountercreate encountercreate, @Nullable final DefaultResponseCallbackListener callbackListener) {
        new VisitRepository().startVisit(new PatientDAO().findPatientByUUID(encountercreate.getPatient()),
                new StartVisitResponseListenerCallback() {
                    @Override
                    public void onStartVisitResponse(long id) {
                        new VisitDAO().getVisitByID(id)
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(visit -> {
                                    encountercreate.setVisit(visit.getUuid());
                                    if (callbackListener != null) {
                                        syncEncounter(encountercreate, callbackListener);
                                    }
                                    else {
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

    public void syncEncounter(final Encountercreate encountercreate, @Nullable final DefaultResponseCallbackListener callbackListener) {

        if (NetworkUtils.isOnline()) {

            encountercreate.pullObslist();
            Call<Encounter> call = apiService.createEncounter(encountercreate);
            call.enqueue(new Callback<Encounter>() {
                @Override
                public void onResponse(@NonNull Call<Encounter> call, @NonNull Response<Encounter> response) {
                    if (response.isSuccessful()) {
                        Encounter encounter = response.body();
                        linkvisit(encountercreate.getPatientId(),encountercreate.getFormname(), encounter, encountercreate);
                        encountercreate.setSynced(true);
                        encountercreate.save();
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
            ToastUtil.error("Sync is off. Turn on sync to save form data.");
        }

    }

    public void syncEncounter(final Encountercreate encountercreate) {
        syncEncounter(encountercreate, null);
    }

    private void linkvisit(Long patientid, String formname, Encounter encounter, Encountercreate encountercreate)
    {
        VisitDAO visitDAO = new VisitDAO();
        visitDAO.getVisitByUuid(encounter.getVisit().getUuid())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(visit -> {
                    encounter.setEncounterType(new EncounterType(formname));
                    for (int i=0;i<encountercreate.getObservations().size();i++)
                    {
                        encounter.getObservations().get(i).setDisplayValue
                                (encountercreate.getObservations().get(i).getValue());
                    }
                    List<Encounter> encounterList=visit.getEncounters();
                    encounterList.add(encounter);
                    visitDAO.saveOrUpdate(visit, patientid)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(id ->
                                    ToastUtil.success(formname+" data saved successfully"));
                });
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if(NetworkUtils.isOnline()) {

            List<Encountercreate> encountercreatelist = new Select()
                    .from(Encountercreate.class)
                    .execute();

            for(final Encountercreate encountercreate:encountercreatelist)
            {
                if(!encountercreate.getSynced() &&
                        new PatientDAO().findPatientByID(Long.toString(encountercreate.getPatientId())).isSynced())
                {
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
            ToastUtil.error("No internet connection. Form data is saved locally " +
                    "and will sync when internet connection is restored. ");
        }
    }

}