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

import org.openmrs.mobile.models.Encounter;
import org.openmrs.mobile.models.EncounterType;
import org.openmrs.mobile.models.Encountercreate;
import org.openmrs.mobile.models.FormResource;
import org.openmrs.mobile.models.IdGenPatientIdentifiers;
import org.openmrs.mobile.models.IdentifierType;
import org.openmrs.mobile.models.Location;
import org.openmrs.mobile.models.Module;
import org.openmrs.mobile.models.Obscreate;
import org.openmrs.mobile.models.Observation;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.models.PatientPhoto;
import org.openmrs.mobile.models.Results;
import org.openmrs.mobile.models.Session;
import org.openmrs.mobile.models.User;
import org.openmrs.mobile.models.Visit;
import org.openmrs.mobile.models.VisitType;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;

public interface RestApi {


    @GET("form?v=custom:(uuid,name,resources)")
    Call<Results<FormResource>> getForms();

    @GET("location?tag=Login%20Location")
    Call<Results<Location>> getLocations(@Query("v") String representation);


    @GET()
    Call<Results<Location>> getLocations(@Url String url,
                                         @Query("tag") String tag,
                                         @Query("v") String representation);

    @GET("patientidentifiertype")
    Call<Results<IdentifierType>> getIdentifierTypes();

    @GET("module/idgen/generateIdentifier.form?source=1")
    Call<IdGenPatientIdentifiers> getPatientIdentifiers(@Query("username") String username,
                                                        @Query("password") String password);

    @GET("patient/{uuid}")
    Call<Patient> getPatientByUUID(@Path("uuid") String uuid,
                                   @Query("v") String representation);

    @GET("patient?lastviewed&v=full")
    Call<Results<Patient>> getLastViewedPatients();

    @POST("patient")
    Call<Patient> createPatient(
            @Body Patient patient);

    @GET("patient")
    Call<Results<Patient>> getPatients(@Query("q") String searchQuery,
                                       @Query("v") String representation);

    @POST("personimage/{uuid}")
    Call<PatientPhoto> uploadPatientPhoto(@Path("uuid") String uuid,
                                          @Body PatientPhoto patientPhoto);

    @GET("personimage/{uuid}")
    Call<ResponseBody> downloadPatientPhoto(@Path("uuid") String uuid);

    @GET("patient?matchSimilar=true&v=full")
    Call<Results<Patient>> getSimilarPatients(@QueryMap Map<String, String> patientData);

    @POST("obs")
    Call<Observation> createObs(@Body Obscreate obscreate);

    @POST("encounter")
    Call<Encounter> createEncounter(@Body Encountercreate encountercreate);

    @GET("encountertype")
    Call<Results<EncounterType>> getEncounterTypes();

    @GET("session")
    Call<Session> getSession();

    @POST("visit/{uuid}")
    Call<Visit> endVisitByUUID(@Path("uuid") String uuid, @Body Visit stopDatetime);

    @POST("visit")
    Call<Visit> startVisit(@Body Visit visit);

    @GET("visit")
    Call<Results<Visit>> findVisitsByPatientUUID(@Query("patient") String patientUUID,
                                        @Query("v") String representation);

    @GET("visittype")
    Call<Results<VisitType>> getVisitType();

    @GET("encounter")
    Call<Results<Encounter>> getLastVitals(@Query("patient") String patientUUID,
                                  @Query("encounterType") String encounterType,
                                  @Query("v") String representation,
                                  @Query("limit") int limit,
                                  @Query("order") String order);

    @POST("patient/{uuid}")
    Call<Patient> updatePatient(@Body Patient patient, @Path("uuid") String uuid,
                                @Query("v") String representation);

    @GET("module")
    Call<Results<Module>> getModules(@Query("v") String representation);

    @GET("user")
    Call<Results<User>> getUserInfo(@Query("q") String username);

    @GET("user/{uuid}")
    Call<User> getFullUserInfo(@Path("uuid") String uuid);

}
