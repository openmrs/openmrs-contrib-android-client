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

import static com.openmrs.android_sdk.utilities.DateUtils.OPEN_MRS_REQUEST_FORMAT;
import static com.openmrs.android_sdk.utilities.DateUtils.convertTime;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import rx.Observable;

import androidx.annotation.NonNull;

import com.openmrs.android_sdk.library.OpenmrsAndroid;
import com.openmrs.android_sdk.library.dao.EncounterDAO;
import com.openmrs.android_sdk.library.dao.LocationDAO;
import com.openmrs.android_sdk.library.dao.VisitDAO;
import com.openmrs.android_sdk.library.databases.AppDatabaseHelper;
import com.openmrs.android_sdk.library.models.Encounter;
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

    public LocationDAO locationDAO;
    public VisitDAO visitDAO;
    public EncounterDAO encounterDAO;

    String representation = "custom:(uuid,location:ref,visitType:ref,startDatetime,stopDatetime,encounters:full)";

    /**
     * Instantiates a new Visit repository.
     */
    @Inject
    public VisitRepository(VisitDAO visitDAO, EncounterDAO encounterDAO, LocationDAO locationDAO) {
        this.visitDAO = visitDAO;
        this.encounterDAO = encounterDAO;
        this.locationDAO = locationDAO;
    }

    /**
     * Executes a Retrofit request
     *
     * @param call    the interface call
     * @param message the error message to display
     * @param <T>     the response type
     * @return T
     * @throws Exception if an error occurs during the request
     */
    public <T> T executeRequest(Call<T> call, String message) throws Exception {
        Response<T> response = call.execute();

        if (response.isSuccessful() && response.body() != null) {
            return response.body();
        } else {
            logger.e(message + response.message());
            throw new Exception(response.message());
        }
    }

    public List<Visit> fetchVisitsAndSave(Call<Results<Visit>> call, Patient patient) throws IOException {
        Response<Results<Visit>> response = call.execute();

        if (response.isSuccessful()) {
            List<Visit> visits = response.body().getResults();
            for (Visit visit : visits) {
                visitDAO.saveOrUpdate(visit, patient.getId()).toBlocking().subscribe();
            }
            return visits;
        } else {
            throw new IOException("Error with fetching visits by patient UUID: " + response.message());
        }
    }

    /**
     * This method downloads visits data asynchronously from the server.
     *
     * @param patient the patient
     */
    public Observable<List<Visit>> syncVisitsData(@NonNull final Patient patient) {
        return AppDatabaseHelper.createObservableIO(() -> {
            Call<Results<Visit>> call = restApi.findVisitsByPatientUUID(patient.getUuid(), representation);
            return fetchVisitsAndSave(call, patient);
        });
    }

    /**
     * Get a particular Visit of a patient from the server
     *
     * @param visitUuid the UUID of the visit.
     */
    public Observable<Visit> getVisit(String visitUuid){
        return AppDatabaseHelper.createObservableIO(() -> {
            Call<Visit> call = restApi.getVisitFromUuid(visitUuid);
            Response<Visit> response = call.execute();

            if (response.isSuccessful()) {
                Visit visit = response.body();
                return visit;
            } else {
                throw new IOException("Error with fetching visit by visit uuid: " + response.message());
            }
        });
    }

    /**
     * This method is used for fetching VisitType asynchronously.
     *
     * @return Observable VisitType object or null
     * @see VisitType
     */
    public Observable<VisitType> getVisitType() {
        return AppDatabaseHelper.createObservableIO(() -> {
            Response<Results<VisitType>> response = restApi.getVisitType().execute();
            if (response.isSuccessful()) return response.body().getResults().get(0);
            else return null;
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
     * This method ends an active visit of a patient.
     *
     * @param visit visit to be ended
     * @return observable boolean true if operation is successful
     * @see Visit
     */
    public Observable<Boolean> endVisit(Visit visit) {
        return AppDatabaseHelper.createObservableIO(() -> {
            // Don't pass the full visit to the API as it will return an error, instead create an empty visit.
            Visit emptyVisitWithStopDate = new Visit();
            emptyVisitWithStopDate.setStopDatetime(convertTime(System.currentTimeMillis(), OPEN_MRS_REQUEST_FORMAT));

            Response<Visit> response = restApi.endVisitByUUID(visit.getUuid(), emptyVisitWithStopDate).execute();
            if (response.isSuccessful()) {
                visit.setStopDatetime(emptyVisitWithStopDate.getStopDatetime());
                visitDAO.saveOrUpdate(visit, visit.patient.getId()).single().toBlocking().first();
                return true;
            } else {
                throw new Exception("endVisitByUuid error: " + response.message());
            }
        });
    }

    /**
     * Start visit for a patient.
     *
     * @param patient the patient to start a visit for
     * @return observable visit that has been started
     */
    public Observable<Visit> startVisit(final Patient patient) {
        return AppDatabaseHelper.createObservableIO(() -> {
            final Visit visit = new Visit();
            visit.setStartDatetime(DateUtils.convertTime(System.currentTimeMillis(), DateUtils.OPEN_MRS_REQUEST_FORMAT));
            visit.setPatient(patient);
            visit.setLocation(locationDAO.findLocationByName(OpenmrsAndroid.getLocation()));

            VisitType visitType = new VisitType();
            visitType.setUuid(OpenmrsAndroid.getVisitTypeUUID());

            visit.setVisitType(visitType);

            Call<Visit> call = restApi.startVisit(visit);
            Response<Visit> response = call.execute();

            if (response.isSuccessful()) {
                Visit newVisit = response.body();
                long visitId = visitDAO.saveOrUpdate(newVisit, patient.getId()).toBlocking().first();
                newVisit.setId(visitId);
                return newVisit;
            } else {
                getLogger().e("Error starting a visit: " + response.message());
                throw new Exception(response.message());
            }
        });
    }

    /**
     * This method fetches visits for a particular location and saves them locally
     *
     * @param patient
     * @param locationUuid
     *
     * @return the vist list
     */
    public Observable<List<Visit>> getVisitsByLocationAndSaveLocally(@NonNull final Patient patient,
                                                                     String locationUuid) {
        return AppDatabaseHelper.createObservableIO(() -> {
            Call<Results<Visit>> call =
                    restApi.findActiveVisitsByPatientAndLocation(patient.getUuid(), locationUuid,representation);
            return fetchVisitsAndSave(call, patient);
        });
    }

    /**
     * This method fetches visits for a particular location and from a start date
     * and saves them locally
     *
     * @param patient
     * @param locationUuid
     * @param fromStartDate
     *
     * @return the vist list
     */
    public Observable<List<Visit>>
    getVisitsByLocationAndDateAndSaveLocally(@NonNull final Patient patient, String locationUuid, String fromStartDate) {

        return AppDatabaseHelper.createObservableIO(() -> {
            Call<Results<Visit>> call =
                    restApi.findVisitsByPatientAndLocationAndDate(patient.getUuid(), locationUuid, fromStartDate, representation);
            return fetchVisitsAndSave(call, patient);
        });
    }

    /**
     * This method fetches visits from a start date and saves them locally
     *
     * @param patient
     * @param fromStartDate
     *
     * @return the vist list
     */
    public Observable<List<Visit>>
    getVisitsByDateAndSaveLocally(@NonNull final Patient patient, String fromStartDate) {

        return AppDatabaseHelper.createObservableIO(() -> {
            Call<Results<Visit>> call =
                    restApi.findVisitsByPatientAndDate(patient.getUuid(), fromStartDate, representation);
            return fetchVisitsAndSave(call, patient);
        });
    }

    /**
     * This method fetches active visits from a given date and saves them locally
     *
     * @param patient
     * @param fromStartDate
     *
     * @return the active vist list
     */
    public Observable<List<Visit>>
    getActiveVisitsByDateAndSaveLocally(@NonNull final Patient patient, String fromStartDate) {

        return AppDatabaseHelper.createObservableIO(() -> {
            Call<Results<Visit>> call =
                    restApi.findActiveVisitsByPatientAndDate(patient.getUuid(), fromStartDate, representation);
            return fetchVisitsAndSave(call, patient);
        });
    }

    /**
     * This method fetches active visits for a location and saves them locally
     *
     * @param patient
     * @param locationUuid
     *
     * @return the active vist list
     */
    public Observable<List<Visit>>
    getActiveVisitsByLocationAndSaveLocally(@NonNull final Patient patient, String locationUuid) {

        return AppDatabaseHelper.createObservableIO(() -> {
            Call<Results<Visit>> call =
                    restApi.findActiveVisitsByPatientAndLocation(patient.getUuid(), locationUuid, representation);
            return fetchVisitsAndSave(call, patient);
        });
    }

    /**
     * This method fetches active visits for a location and from a start date
     * and saves them locally
     *
     * @param patient
     * @param fromStartDate
     * @param locationUuid
     *
     * @return the active vist list
     */
    public Observable<List<Visit>>
    getActiveVisitsByLocationAndDateAndSaveLocally(@NonNull final Patient patient, String locationUuid, String fromStartDate) {

        return AppDatabaseHelper.createObservableIO(() -> {
            Call<Results<Visit>> call =
                    restApi.findActiveVisitsByPatientAndLocationAndDate(patient.getUuid(), locationUuid, fromStartDate, representation);
            return fetchVisitsAndSave(call, patient);
        });
    }
}
