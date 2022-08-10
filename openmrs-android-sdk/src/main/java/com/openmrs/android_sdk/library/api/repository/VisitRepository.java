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
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Observable;

import androidx.annotation.NonNull;

import com.openmrs.android_sdk.library.OpenMRSLogger;
import com.openmrs.android_sdk.library.OpenmrsAndroid;
import com.openmrs.android_sdk.library.api.RestApi;
import com.openmrs.android_sdk.library.dao.EncounterCreateRoomDAO;
import com.openmrs.android_sdk.library.dao.EncounterDAO;
import com.openmrs.android_sdk.library.dao.LocationDAO;
import com.openmrs.android_sdk.library.dao.VisitDAO;
import com.openmrs.android_sdk.library.databases.AppDatabaseHelper;
import com.openmrs.android_sdk.library.listeners.retrofitcallbacks.GetVisitTypeCallback;
import com.openmrs.android_sdk.library.listeners.retrofitcallbacks.VisitsResponseCallback;
import com.openmrs.android_sdk.library.models.Encounter;
import com.openmrs.android_sdk.library.models.Encountercreate;
import com.openmrs.android_sdk.library.models.Patient;
import com.openmrs.android_sdk.library.models.Results;
import com.openmrs.android_sdk.library.models.Visit;
import com.openmrs.android_sdk.library.models.VisitType;
import com.openmrs.android_sdk.utilities.ApplicationConstants;
import com.openmrs.android_sdk.utilities.DateUtils;


/**
 * The type Visit repository.
 */
@Singleton
public class VisitRepository extends BaseRepository {

    private LocationDAO locationDAO;
    private VisitDAO visitDAO;
    private EncounterDAO encounterDAO;
    private EncounterCreateRoomDAO encounterCreateRoomDAO;

    /**
     * Instantiates a new Visit repository.
     */
    @Inject
    public VisitRepository() {
        visitDAO = new VisitDAO();
        encounterDAO = new EncounterDAO();
        encounterCreateRoomDAO = db.encounterCreateRoomDAO();
        locationDAO = new LocationDAO();
    }

    /**
     * used in Unit tests with mockUp objects
     *
     * @param restApi
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

    /**
     * This method downloads visits data asynchronously from the server.
     *
     * @param patient
     */
    public Observable<List<Visit>> syncVisitsData(@NonNull final Patient patient) {
        return AppDatabaseHelper.createObservableIO(() -> {
            Call<Results<Visit>> call = restApi.findVisitsByPatientUUID(patient.getUuid(), "custom:(uuid,location:ref,visitType:ref,startDatetime,stopDatetime,encounters:full)");
            Response<Results<Visit>> response = call.execute();

            if (response.isSuccessful()) {
                List<Visit> visits = response.body().getResults();
                visitDAO.deleteVisitsByPatientId(patient.getId()).toBlocking().subscribe();
                for (Visit visit : visits) {
                    visitDAO.saveOrUpdate(visit, patient.getId()).toBlocking().subscribe();
                }
                return visits;
            } else {
                throw new IOException("Error with fetching visits by patient uuid: " + response.message());
            }
        });
    }

    /**
     * This method is used for getting visitType asynchronously .
     *
     * @param callbackListener
     * @see VisitType
     * @see GetVisitTypeCallback
     */
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


    /**
     * This method is used to sync Vitals of a patient in a visit
     *
     * @param patientUuid Patient UUID to get vitals from
     * @return Encounter observable containing last vitals
     */
    public Observable<Encounter> syncLastVitals(final String patientUuid) {
        return AppDatabaseHelper.createObservableIO(() -> {
            Call<Results<Encounter>> call = restApi.getLastVitals(patientUuid, ApplicationConstants.EncounterTypes.VITALS, "full", 1, "desc");
            Response<Results<Encounter>> response = call.execute();

            if (response.isSuccessful()) {
                if (!response.body().getResults().isEmpty()) {
                    Encounter encounter = response.body().getResults().get(0);
                    encounterDAO.saveLastVitalsEncounter(encounter, patientUuid);
                    return encounter;
                }
                return new Encounter();
            } else {
                throw new IOException("Error with fetching last vitals: " + response.message());
            }
        });
    }

    /**
     * This method is used to end an active visit of a patient.
     *
     * @param uuid
     * @param visit
     * @param callbackListener
     * @see Visit
     * @see VisitsResponseCallback
     */
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

    /**
     * Start visit for a patient.
     *
     * @param patient the patient to start a visit for
     */
    public Observable<Visit> startVisit(final Patient patient) {
        return AppDatabaseHelper.createObservableIO(() -> {
            final Visit visit = new Visit();
            visit.setStartDatetime(DateUtils.convertTime(System.currentTimeMillis(), DateUtils.OPEN_MRS_REQUEST_FORMAT));
            visit.setPatient(patient);
            visit.setLocation(locationDAO.findLocationByName(OpenmrsAndroid.getLocation()));

            VisitType visitType = new VisitType("Outpatient", OpenmrsAndroid.getVisitTypeUUID());
            visit.setVisitType(visitType);

            Call<Visit> call = restApi.startVisit(visit);
            Response<Visit> response = call.execute();

            if (response.isSuccessful()) {
                Visit newVisit = response.body(); // The VisitType in response contains null display string. Needs a fix (AC-1030)
                newVisit.visitType = visitType; // Temporary workaround

                visitDAO.saveOrUpdate(newVisit, patient.getId()).toBlocking().subscribe(newVisit::setId);

                return newVisit;
            } else {
                throw new IOException(response.message());
            }
        });
    }

    /**
     * Add encounter created long.
     *
     * @param encountercreate the encountercreate
     * @return the long
     */
    public long addEncounterCreated(final Encountercreate encountercreate) {
        return encounterCreateRoomDAO.addEncounterCreated(encountercreate);
    }
}
