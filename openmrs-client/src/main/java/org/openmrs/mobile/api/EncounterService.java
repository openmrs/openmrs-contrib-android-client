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
import android.support.annotation.Nullable;

import com.activeandroid.query.Select;

import org.openmrs.mobile.api.retrofit.VisitApi;
import org.openmrs.mobile.dao.PatientDAO;
import org.openmrs.mobile.dao.VisitDAO;
import org.openmrs.mobile.listeners.retrofit.DefaultResponseCallbackListener;
import org.openmrs.mobile.listeners.retrofit.StartVisitResponseListenerCallback;
import org.openmrs.mobile.models.Encounter;
import org.openmrs.mobile.models.EncounterType;
import org.openmrs.mobile.models.Encountercreate;
import org.openmrs.mobile.models.Visit;
import org.openmrs.mobile.utilities.NetworkUtils;
import org.openmrs.mobile.utilities.ToastUtil;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EncounterService extends IntentService {
    final RestApi apiService =
            RestServiceBuilder.createService(RestApi.class);

    public EncounterService() {
        super("Save Encounter");
    }

    public void addEncounter(final Encountercreate encountercreate, @Nullable DefaultResponseCallbackListener callbackListener) {

        if(NetworkUtils.isOnline()) {
            if (new VisitDAO().isPatientNowOnVisit(encountercreate.getPatientId())) {
                Visit visit = new VisitDAO().getPatientCurrentVisit(encountercreate.getPatientId());
                encountercreate.setVisit(visit.getUuid());
                if (callbackListener != null) {
                    syncEncounter(encountercreate, callbackListener);
                }
                else {
                    syncEncounter(encountercreate);
                }
            }
            else {
                startNewVisitForEncounter(encountercreate);
            }
        }
        else
            ToastUtil.error("No internet connection. Form data is saved locally " +
                    "and will sync when internet connection is restored. ");
    }

    public void addEncounter(final Encountercreate encountercreate) {
        addEncounter(encountercreate, null);
    }

        private void startNewVisitForEncounter(final Encountercreate encountercreate, @Nullable final DefaultResponseCallbackListener callbackListener) {
        new VisitApi().startVisit(new PatientDAO().findPatientByUUID(encountercreate.getPatient()),
                new StartVisitResponseListenerCallback() {
                    @Override
                    public void onStartVisitResponse(long id) {
                        encountercreate.setVisit(new VisitDAO().getVisitsByID(id).getUuid());
                        if (callbackListener != null) {
                            syncEncounter(encountercreate, callbackListener);
                        }
                        else {
                            syncEncounter(encountercreate);
                        }
                    }
                    @Override
                    public void onResponse() {}
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
                public void onResponse(Call<Encounter> call, Response<Encounter> response) {
                    if (response.isSuccessful()) {
                        Encounter encounter = response.body();
                        linkvisit(encountercreate.getPatientId(),encountercreate.getFormname(), encounter, encountercreate);
                        encountercreate.setSynced(true);
                        encountercreate.save();
                        new VisitApi().syncLastVitals(encountercreate.getPatient());
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
                public void onFailure(Call<Encounter> call, Throwable t) {
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

    void linkvisit(Long patientid, String formname, Encounter encounter, Encountercreate encountercreate)
    {
        Long visitid=new VisitDAO().getVisitsIDByUUID(encounter.getVisit().getUuid());
        Visit visit=new VisitDAO().getVisitsByID(visitid);
        encounter.setEncounterType(new EncounterType(formname));
        for (int i=0;i<encountercreate.getObservations().size();i++)
        {
            encounter.getObservations().get(i).setDisplayValue
                    (encountercreate.getObservations().get(i).getValue());
        }
        List<Encounter> encounterList=visit.getEncounters();
        encounterList.add(encounter);
        new VisitDAO().updateVisit(visit,visit.getId(),patientid);
        ToastUtil.success(formname+" data saved successfully");
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
                    if (new VisitDAO().isPatientNowOnVisit(encountercreate.getPatientId())) {
                        Visit visit = new VisitDAO().getPatientCurrentVisit(encountercreate.getPatientId());
                        encountercreate.setVisit(visit.getUuid());
                        syncEncounter(encountercreate);

                    } else {
                        startNewVisitForEncounter(encountercreate);
                    }
                }
            }


        } else {
            ToastUtil.error("No internet connection. Form data is saved locally " +
                    "and will sync when internet connection is restored. ");
        }
    }

}