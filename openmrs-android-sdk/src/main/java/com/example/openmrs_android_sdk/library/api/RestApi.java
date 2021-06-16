/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package com.example.openmrs_android_sdk.library.api;

import com.example.openmrs_android_sdk.library.databases.entities.ConceptEntity;
import com.example.openmrs_android_sdk.library.databases.entities.FormResourceEntity;
import com.example.openmrs_android_sdk.library.databases.entities.LocationEntity;
import com.example.openmrs_android_sdk.library.models.Allergy;
import com.example.openmrs_android_sdk.library.models.AllergyCreate;
import com.example.openmrs_android_sdk.library.models.ConceptAnswers;
import com.example.openmrs_android_sdk.library.models.ConceptMembers;
import com.example.openmrs_android_sdk.library.models.Encounter;
import com.example.openmrs_android_sdk.library.models.EncounterType;
import com.example.openmrs_android_sdk.library.models.Encountercreate;
import com.example.openmrs_android_sdk.library.models.FormCreate;
import com.example.openmrs_android_sdk.library.models.FormData;
import com.example.openmrs_android_sdk.library.models.IdGenPatientIdentifiers;
import com.example.openmrs_android_sdk.library.models.IdentifierType;
import com.example.openmrs_android_sdk.library.models.Module;
import com.example.openmrs_android_sdk.library.models.Obscreate;
import com.example.openmrs_android_sdk.library.models.Observation;
import com.example.openmrs_android_sdk.library.models.Patient;
import com.example.openmrs_android_sdk.library.models.PatientDto;
import com.example.openmrs_android_sdk.library.models.PatientDtoUpdate;
import com.example.openmrs_android_sdk.library.models.PatientPhoto;
import com.example.openmrs_android_sdk.library.models.Provider;
import com.example.openmrs_android_sdk.library.models.Resource;
import com.example.openmrs_android_sdk.library.models.Results;
import com.example.openmrs_android_sdk.library.models.Session;
import com.example.openmrs_android_sdk.library.models.SystemProperty;
import com.example.openmrs_android_sdk.library.models.SystemSetting;
import com.example.openmrs_android_sdk.library.models.User;
import com.example.openmrs_android_sdk.library.models.Visit;
import com.example.openmrs_android_sdk.library.models.VisitType;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;

public interface RestApi {
    @GET("form?v=custom:(uuid,name,resources)")
    Call<Results<FormResourceEntity>> getForms();

    @GET("location?tag=Login%20Location")
    Call<Results<LocationEntity>> getLocations(@Query("v") String representation);

    @GET()
    Call<Results<LocationEntity>> getLocations(@Url String url,
                                               @Query("tag") String tag,
                                               @Query("v") String representation);

    @GET("systemsetting")
    Call<Results<SystemProperty>> getSystemProperty(@Query("q") String property,
                                                    @Query("v") String representation);

    @GET("patientidentifiertype")
    Call<Results<IdentifierType>> getIdentifierTypes();

    @GET("module/idgen/generateIdentifier.form?source=1")
    Call<IdGenPatientIdentifiers> getPatientIdentifiers(@Query("username") String username,
                                                        @Query("password") String password);

    @GET("patient/{uuid}")
    Call<PatientDto> getPatientByUUID(@Path("uuid") String uuid,
                                      @Query("v") String representation);

    @GET("patient?lastviewed&v=full")
    Call<Results<Patient>> getLastViewedPatients(@Query("limit") Integer limit,
                                                 @Query("startIndex") Integer startIndex);

    @POST("patient")
    Call<PatientDto> createPatient(
            @Body PatientDto patientDto);

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

    @GET("encounterrole")
    Call<Results<Resource>> getEncounterRoles();

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
    Call<PatientDto> updatePatient(@Body PatientDtoUpdate patientDto, @Path("uuid") String uuid,
                                   @Query("v") String representation);

    @GET("module")
    Call<Results<Module>> getModules(@Query("v") String representation);

    @GET("user")
    Call<Results<User>> getUserInfo(@Query("q") String username);

    @GET("user/{uuid}")
    Call<User> getFullUserInfo(@Path("uuid") String uuid);

    @GET("concept")
    Call<Results<ConceptEntity>> getConcepts(@Query("limit") int limit, @Query("startIndex") int startIndex);

    @GET("concept/{uuid}")
    Call<ConceptAnswers> getConceptFromUUID(@Path("uuid") String uuid);

    @GET("concept/{uuid}")
    Call<ConceptMembers> getConceptMembersFromUUID(@Path("uuid") String uuid);

    @GET("systemsetting")
    Call<Results<SystemSetting>> getSystemSettingsByQuery(@Query("q") String query,
                                                          @Query("v") String representation);

    @POST("form/{uuid}/resource")
    Call<FormCreate> formCreate(@Path("uuid") String uuid,
                                @Body FormData obj);

    @GET("provider?v=default")
    Call<Results<Provider>> getProviderList();

    @DELETE("provider/{uuid}?!purge")
    Call<ResponseBody> deleteProvider(@Path("uuid") String uuid);

    @POST("provider")
    Call<Provider> addProvider(@Body Provider provider);

    @POST("provider/{uuid}")
    Call<Provider> UpdateProvider(@Path("uuid") String uuid,
                                  @Body Provider provider);

    @GET("patient/{uuid}/allergy")
    Call<Results<Allergy>> getAllergies(@Path("uuid") String uuid);

    @DELETE("patient/{patientUuid}/allergy/{allergyUuid}")
    Call<ResponseBody> deleteAllergy(@Path("patientUuid") String patientUuid,
                                     @Path("allergyUuid") String allergyUuid);

    @POST("patient/{uuid}/allergy")
    Call<Allergy> createAllergy(@Path("uuid") String uuid,
                                @Body AllergyCreate allergyCreate);

    @POST("patient/{patientUuid}/allergy/{allergyUuid}")
    Call<Allergy> updateAllergy(@Path("patientUuid") String patientUuid,
                                @Path("allergyUuid") String allergyUuid,
                                @Body AllergyCreate allergyCreate);
}
