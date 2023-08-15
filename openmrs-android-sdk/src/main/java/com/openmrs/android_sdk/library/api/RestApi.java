/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package com.openmrs.android_sdk.library.api;

import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;

import com.openmrs.android_sdk.library.databases.entities.ConceptEntity;
import com.openmrs.android_sdk.library.databases.entities.FormResourceEntity;
import com.openmrs.android_sdk.library.databases.entities.LocationEntity;
import com.openmrs.android_sdk.library.models.Allergy;
import com.openmrs.android_sdk.library.models.AllergyCreate;
import com.openmrs.android_sdk.library.models.Appointment;
import com.openmrs.android_sdk.library.models.AppointmentBlock;
import com.openmrs.android_sdk.library.models.AppointmentType;
import com.openmrs.android_sdk.library.models.ConceptAnswers;
import com.openmrs.android_sdk.library.models.ConceptMembers;
import com.openmrs.android_sdk.library.models.Drug;
import com.openmrs.android_sdk.library.models.DrugCreate;
import com.openmrs.android_sdk.library.models.Encounter;
import com.openmrs.android_sdk.library.models.EncounterType;
import com.openmrs.android_sdk.library.models.Encountercreate;
import com.openmrs.android_sdk.library.models.FormCreate;
import com.openmrs.android_sdk.library.models.FormData;
import com.openmrs.android_sdk.library.models.IdGenPatientIdentifiers;
import com.openmrs.android_sdk.library.models.IdentifierType;
import com.openmrs.android_sdk.library.models.Module;
import com.openmrs.android_sdk.library.models.Obscreate;
import com.openmrs.android_sdk.library.models.Observation;
import com.openmrs.android_sdk.library.models.OrderCreate;
import com.openmrs.android_sdk.library.models.OrderGet;
import com.openmrs.android_sdk.library.models.Patient;
import com.openmrs.android_sdk.library.models.PatientDto;
import com.openmrs.android_sdk.library.models.PatientDtoUpdate;
import com.openmrs.android_sdk.library.models.PatientPhoto;
import com.openmrs.android_sdk.library.models.ProgramCreate;
import com.openmrs.android_sdk.library.models.ProgramGet;
import com.openmrs.android_sdk.library.models.Provider;
import com.openmrs.android_sdk.library.models.Resource;
import com.openmrs.android_sdk.library.models.Results;
import com.openmrs.android_sdk.library.models.Session;
import com.openmrs.android_sdk.library.models.SystemProperty;
import com.openmrs.android_sdk.library.models.SystemSetting;
import com.openmrs.android_sdk.library.models.TimeSlot;
import com.openmrs.android_sdk.library.models.User;
import com.openmrs.android_sdk.library.models.Visit;
import com.openmrs.android_sdk.library.models.VisitType;

/**
 * The interface Rest api.
 */
public interface RestApi {
    /**
     * Gets forms.
     *
     * @return the forms
     */
    @GET("form?v=custom:(uuid,name,resources)")
    Call<Results<FormResourceEntity>> getForms();

    /**
     * Gets locations.
     *
     * @param representation the representation
     * @return the locations
     */
    @GET("location?tag=Login%20Location")
    Call<Results<LocationEntity>> getLocations(@Query("v") String representation);

    /**
     * Gets locations.
     *
     * @param url            the url
     * @param tag            the tag
     * @param representation the representation
     * @return the locations
     */
    @GET()
    Call<Results<LocationEntity>> getLocations(@Url String url,
                                               @Query("tag") String tag,
                                               @Query("v") String representation);

    /**
     * Gets system property.
     *
     * @param property       the property
     * @param representation the representation
     * @return the system property
     */
    @GET("systemsetting")
    Call<Results<SystemProperty>> getSystemProperty(@Query("q") String property,
                                                    @Query("v") String representation);

    /**
     * Gets identifier types.
     *
     * @return the identifier types
     */
    @GET("patientidentifiertype")
    Call<Results<IdentifierType>> getIdentifierTypes();

    /**
     * Gets patient identifiers.
     *
     * @param username the username
     * @param password the password
     * @return the patient identifiers
     */
    @GET("module/idgen/generateIdentifier.form?source=1")
    Call<IdGenPatientIdentifiers> getPatientIdentifiers(@Query("username") String username,
                                                        @Query("password") String password);

    /**
     * Gets patient by uuid.
     *
     * @param uuid           the uuid
     * @param representation the representation
     * @return the patient by uuid
     */
    @GET("patient/{uuid}")
    Call<PatientDto> getPatientByUUID(@Path("uuid") String uuid,
                                      @Query("v") String representation);

    /**
     * Gets last viewed patients.
     *
     * @param limit      the limit
     * @param startIndex the start index
     * @return the last viewed patients
     */
    @GET("patient?lastviewed&v=full")
    Call<Results<Patient>> getLastViewedPatients(@Query("limit") Integer limit,
                                                 @Query("startIndex") Integer startIndex);

    /**
     * Create patient call.
     *
     * @param patientDto the patient dto
     * @return the call
     */
    @POST("patient")
    Call<PatientDto> createPatient(@Body PatientDto patientDto);

    /**
     * Gets patients.
     *
     * @param searchQuery    the search query
     * @param representation the representation
     * @return the patients
     */
    @GET("patient")
    Call<Results<Patient>> getPatients(@Query("q") String searchQuery,
                                       @Query("v") String representation);

    /**
     * Gets patients.
     *
     * @param searchQuery    the search query
     * @param representation the representation
     * @return the patients
     */

    @GET("patient")
    Call<Results<PatientDto>> getPatientsDto(@Query("q") String searchQuery,
                                             @Query("v") String representation);

    /**
     * Upload patient photo call.
     *
     * @param uuid         the uuid
     * @param patientPhoto the patient photo
     * @return the call
     */
    @POST("personimage/{uuid}")
    Call<PatientPhoto> uploadPatientPhoto(@Path("uuid") String uuid,
                                          @Body PatientPhoto patientPhoto);

    /**
     * Download patient photo call.
     *
     * @param uuid the uuid
     * @return the call
     */
    @GET("personimage/{uuid}")
    Call<ResponseBody> downloadPatientPhoto(@Path("uuid") String uuid);

    /**
     * Gets similar patients.
     *
     * @param patientData the patient data
     * @return the similar patients
     */
    @GET("patient?matchSimilar=true&v=full")
    Call<Results<Patient>> getSimilarPatients(@QueryMap Map<String, String> patientData);

    /**
     * Gets Observation by uuid
     *
     * @param obsUuid the uuid of the observation
     * @return the Call<Observation>
     */
    @GET("obs/{obsUuid}")
    Call<Observation> getObservationByUuid(@Path("obsUuid") String obsUuid);

    /**
     * Create obs call.
     *
     * @param obscreate the obscreate
     * @return the call
     */
    @POST("obs")
    Call<Observation> createObs(@Body Obscreate obscreate);

    /**
     * Create obs on the server.
     *
     * @param observation the Observation to create
     * @return the call
     */
    @POST("obs")
    Call<Observation> createObservation(@Body Observation observation);

    /**
     * Get all observations for a patient
     *
     * @param patientUuid the patient uuid
     * @return Observation Resource List
     */
    @GET("obs")
    Call<Results<Resource>> getObservationsByPatientUuid(@Query("patient") String patientUuid);

    /**
     * Get all observations for a patient
     *
     * @param encounterUuid the encounter uuid
     * @return Observation Resource List
     */
    @GET("obs")
    Call<Results<Resource>> getObservationsByEncounterUuid(@Query("encounter") String encounterUuid);

    /**
     * Get all observations for a patient
     *
     * @param patientUuid patient uuid
     * @param conceptUuid the concept uuid
     * @return Observation Resource List
     */
    @GET("obs")
    Call<Results<Resource>> getObservationsByConceptUuid(@Query("patient") String patientUuid,
                                                         @Query("concept") String conceptUuid);

    /**
     * Delete Observation from server by uuid
     *
     * @param obsUuid the observation uuid
     * @return the response body
     */
    @DELETE("obs/{obsUuid}")
    Call<ResponseBody> deleteObservation(@Path("obsUuid") String obsUuid);

    /**
     * Update Observation object on the server.
     *
     * @param obsUuid the uuid of the observation
     * @param observation the Observation object
     * @return the updated observation
     */
    @POST("obs/{obsUuid}")
    Call<Observation> updateObservation(@Path("obsUuid") String obsUuid,
                                        @Body Observation observation);

    /**
     * Create encounter call.
     *
     * @param encountercreate the encountercreate
     * @return the call
     */
    @POST("encounter")
    Call<Encounter> createEncounter(@Body Encountercreate encountercreate);

    /**
     * Updates an encounter.
     *
     * @param uuid the UUID of the encounter
     * @param encountercreate the encountercreate containing the updates
     * @return the call
     */
    @POST("encounter/{uuid}")
    Call<Encounter> updateEncounter(@Path("uuid") String uuid, @Body Encountercreate encountercreate);

    /**
     * Get all encounter resources for a patient.
     *
     * @param uuid the UUID of the patient
     * @return the encounter resource list
     */
    @GET("encounter")
    Call<Results<Resource>> getAllEncountersForPatientByPatientUuid(@Query("patient") String uuid);

    /**
     * Get Encounter from Uuid
     *
     * @param encounterUuid the UUID of the patient
     * @return the encounter
     */
    @GET("encounter/{encounterUuid}")
    Call<Encounter> getEncounterByUuid(@Path("encounterUuid") String encounterUuid);

    /**
     * Get Encounter Resources from Patient uuid and EncounterType uuid
     *
     * @param patient_uuid the UUID of the Patient
     * @param encounterType_uuid the UUID of the Encounter type
     * @return the encounter resource list
     */
    @GET("encounter")
    Call<Results<Resource>> getEncounterResourcesByEncounterType(@Query("patient") String patient_uuid,
                                                  @Query("encounterType") String encounterType_uuid);

    /**
     * Get Encounter Resources from Patient uuid and OrderType uuid
     *
     * @param patient_uuid the UUID of the Patient
     * @param orderType_uuid the UUID of the Order type
     * @return the encounter resource list
     */
    @GET("encounter")
    Call<Results<Resource>> getEncounterResourcesByOrderType(@Query("patient") String patient_uuid,
                                                  @Query("orderType") String orderType_uuid);

    /**
     * Get Encounter Resources from Patient uuid and starting from the given date
     *
     * @param patient_uuid the UUID of the Patient
     * @param fromDate the String representation of Date in 'YYYY-MM-DD' format
     * @return the encounter resource list
     */
    @GET("encounter")
    Call<Results<Resource>> getEncounterResourcesFromDate(@Query("patient") String patient_uuid,
                                                  @Query("fromdate") String fromDate);

    /**
     * Get Encounter Resources from Patient uuid and Visit uuid
     *
     * @param patient_uuid the UUID of the Patient
     * @param visit_uuid the UUID of the visit
     * @return the encounter resource list
     */
    @GET("encounter")
    Call<Results<Resource>> getEncounterResourcesByVisit(@Query("patient") String patient_uuid,
                                                  @Query("fromdate") String visit_uuid);

    /**
     * Gets encounter types.
     *
     * @return the encounter types
     */
    @GET("encountertype")
    Call<Results<EncounterType>> getEncounterTypes();

    /**
     * Gets encounter roles.
     *
     * @return the encounter roles
     */
    @GET("encounterrole")
    Call<Results<Resource>> getEncounterRoles();

    /**
     * Gets session.
     *
     * @return the session
     */
    @GET("session")
    Call<Session> getSession();

    /**
     * Ends a visit by its uuid.
     *
     * @param uuid              the visit uuid to be ended
     * @param visitWithStopDate An empty visit containing the stop date and time
     * @return the call
     */
    @POST("visit/{uuid}")
    Call<Visit> endVisitByUUID(@Path("uuid") String uuid, @Body Visit visitWithStopDate);

    /**
     * Start visit call.
     *
     * @param visit the visit
     * @return the call
     */
    @POST("visit")
    Call<Visit> startVisit(@Body Visit visit);

    /**
     * Find visits by patient uuid call.
     *
     * @param patientUUID    the patient uuid
     * @param representation the representation
     * @return the call
     */
    @GET("visit")
    Call<Results<Visit>> findVisitsByPatientUUID(@Query("patient") String patientUUID,
                                                 @Query("v") String representation);

    /**
     * Get a Visit by visit uuid
     *
     * @param visitUuid the patient uuid
     *
     * @return the Visit
     */

    @GET("visit/{visitUuid}")
    Call<Visit> getVisitFromUuid(@Path("visitUuid") String visitUuid);

    /**
     * Gets visit type.
     *
     * @return the visit type
     */
    @GET("visittype")
    Call<Results<VisitType>> getVisitType();

    /**
     * Fetch visits by patient uuid and location
     *
     * @param patientUUID    the patient uuid
     * @param locationUUID    the loation uuid
     * @param representation the required representation
     * @return the list of visits
     */
    @GET("visit")
    Call<Results<Visit>> findVisitsByPatientAndLocation(@Query("patient") String patientUUID,
                                                        @Query("location") String locationUUID,
                                                        @Query("v") String representation);

    /**
     * Fetch visits by patient uuid and start date
     *
     * @param patientUUID    the patient uuid
     * @param fromStartDate  the start date
     * @param representation the required representation
     * @return the list of visits
     */
    @GET("visit")
    Call<Results<Visit>> findVisitsByPatientAndDate(@Query("patient") String patientUUID,
                                                    @Query("fromStartDate") String fromStartDate,
                                                    @Query("v") String representation);

    /**
     * Fetch visits by patient uuid, location and fromStartDate
     *
     * @param patientUUID    the patient uuid
     * @param locationUUID    the loation uuid
     * @param fromStartDate    the start date
     * @param representation the required representation
     * @return the list of visits
     */
    @GET("visit")
    Call<Results<Visit>> findVisitsByPatientAndLocationAndDate(@Query("patient") String patientUUID,
                                                               @Query("location") String locationUUID,
                                                               @Query("fromStartDate") String fromStartDate,
                                                               @Query("v") String representation);


    /**
     * Fetch visit resources by patient uuid
     *
     * @param patientUUID    the patient uuid
     * @return the list of visit resources
     */
    @GET("visit")
    Call<Results<Resource>> findVisitResourcesByPatientUUID(@Query("patient") String patientUUID);

    /**
     * Fetch visit resources by patient uuid and location uuid
     *
     * @param patientUUID    the patient uuid
     * @param locationUUID   the location uuid
     *
     * @return the list of visit resources
     */
    @GET("visit")
    Call<Results<Resource>> findVisitResourcesByPatientAndLocation(@Query("patient") String patientUUID,
                                                                   @Query("location") String locationUUID);

    /**
     * Fetch visit resources by patient uuid, location uuid and fromStartDate
     *
     * @param patientUUID    the patient uuid
     * @param locationUUID   the location uuid
     * @param fromStartDate  starting date of the visit
     *
     * @return the list of visit resources
     */
    @GET("visit")
    Call<Results<Resource>> findVisitResourcesByPatientAndLocationAndDate(@Query("patient") String patientUUID,
                                                                          @Query("location") String locationUUID,
                                                                          @Query("fromStartDate") String fromStartDate);

    /**
     * Fetch visit resources by patient uuid and fromStartDate
     *
     * @param patientUUID    the patient uuid
     * @param fromStartDate  starting date of the visit
     *
     * @return the list of visit resources
     */
    @GET("visit")
    Call<Results<Resource>> findVisitResourcesByPatientAndDate(@Query("patient") String patientUUID,
                                                               @Query("fromStartDate") String fromStartDate);

    /**
     * Fetch active visits by patient uuid
     *
     * @param patientUUID    the patient uuid
     * @param representation the required representation
     * @return the list of visits
     */
    @GET("visit?includeInactive=false")
    Call<Results<Visit>> findActiveVisitsByPatientUuid(@Query("patient") String patientUUID,
                                                       @Query("v") String representation);

    /**
     * Fetch active visits by patient uuid and location
     *
     * @param patientUUID    the patient uuid
     * @param locationUUID    the loation uuid
     * @param representation the required representation
     * @return the list of visits
     */
    @GET("visit?includeInactive=false")
    Call<Results<Visit>> findActiveVisitsByPatientAndLocation(@Query("patient") String patientUUID,
                                                              @Query("location") String locationUUID,
                                                              @Query("v") String representation);

    /**
     * Fetch active visits by patient uuid, location and fromStartDate
     *
     * @param patientUUID    the patient uuid
     * @param locationUUID    the loation uuid
     * @param fromStartDate    the start date
     * @param representation the required representation
     * @return the list of visits
     */
    @GET("visit?includeInactive=false")
    Call<Results<Visit>> findActiveVisitsByPatientAndLocationAndDate(@Query("patient") String patientUUID,
                                                                     @Query("location") String locationUUID,
                                                                     @Query("fromStartDate") String fromStartDate,
                                                                     @Query("v") String representation);

    /**
     * Fetch active visits by patient uuid and fromStartDate
     *
     * @param patientUUID    the patient uuid
     * @param fromStartDate    the start date
     * @param representation the required representation
     * @return the list of visits
     */
    @GET("visit?includeInactive=false")
    Call<Results<Visit>> findActiveVisitsByPatientAndDate(@Query("patient") String patientUUID,
                                                          @Query("fromStartDate") String fromStartDate,
                                                          @Query("v") String representation);

    /**
     * Create Visit
     *
     * @param visit the Visit
     * @return the call
     */
    @POST("visit")
    Call<Visit> createVisit(@Body Visit visit);

    /**
     * Update Visit
     *
     * @param uuid the uuid of the visit
     * @param visit the Visit
     * @return the call
     */
    @POST("visit/{uuid}")
    Call<Visit> createVisit(@Path("uuid") String uuid, @Body Visit visit);

    /**
     * Delete Visit
     *
     * @param uuid the uuid of the visit
     * @return the call
     */
    @DELETE("visit/{uuid}")
    Call<ResponseBody> deleteVisit(@Path("uuid") String uuid);

    /**
     * Gets last vitals.
     *
     * @param patientUUID    the patient uuid
     * @param encounterType  the encounter type
     * @param representation the representation
     * @param limit          the limit
     * @param order          the order
     * @return the last vitals
     */
    @GET("encounter")
    Call<Results<Encounter>> getLastVitals(@Query("patient") String patientUUID,
                                           @Query("encounterType") String encounterType,
                                           @Query("v") String representation,
                                           @Query("limit") int limit,
                                           @Query("order") String order);

    /**
     * Update patient call.
     *
     * @param patientDto     the patient dto
     * @param uuid           the uuid
     * @param representation the representation
     * @return the call
     */
    @POST("patient/{uuid}")
    Call<PatientDto> updatePatient(@Body PatientDtoUpdate patientDto, @Path("uuid") String uuid,
                                   @Query("v") String representation);

    /**
     * Gets modules.
     *
     * @param representation the representation
     * @return the modules
     */
    @GET("module")
    Call<Results<Module>> getModules(@Query("v") String representation);

    /**
     * Gets user info.
     *
     * @param username the username
     * @return the user info
     */
    @GET("user")
    Call<Results<User>> getUserInfo(@Query("q") String username);

    /**
     * Gets full user info.
     *
     * @param uuid the uuid
     * @return the full user info
     */
    @GET("user/{uuid}")
    Call<User> getFullUserInfo(@Path("uuid") String uuid);

    /**
     * Gets concepts.
     *
     * @param limit      the limit
     * @param startIndex the start index
     * @return the concepts
     */
    @GET("concept")
    Call<Results<ConceptEntity>> getConcepts(@Query("limit") int limit, @Query("startIndex") int startIndex);

    /**
     * Gets concept from uuid.
     *
     * @param uuid the uuid
     * @return the concept from uuid
     */
    @GET("concept/{uuid}")
    Call<ConceptAnswers> getConceptFromUUID(@Path("uuid") String uuid);

    /**
     * Gets concept members from uuid.
     *
     * @param uuid the uuid
     * @return the concept members from uuid
     */
    @GET("concept/{uuid}")
    Call<ConceptMembers> getConceptMembersFromUUID(@Path("uuid") String uuid);

    /**
     * Gets system settings by query.
     *
     * @param query          the query
     * @param representation the representation
     * @return the system settings by query
     */
    @GET("systemsetting")
    Call<Results<SystemSetting>> getSystemSettingsByQuery(@Query("q") String query,
                                                          @Query("v") String representation);

    /**
     * Form create call.
     *
     * @param uuid the uuid
     * @param obj  the obj
     * @return the call
     */
    @POST("form/{uuid}/resource")
    Call<FormCreate> formCreate(@Path("uuid") String uuid,
                                @Body FormData obj);

    /**
     * Gets provider list.
     *
     * @return the provider list
     */
    @GET("provider?v=default")
    Call<Results<Provider>> getProviderList();

    /**
     * Delete provider call.
     *
     * @param uuid the uuid
     * @return the call
     */
    @DELETE("provider/{uuid}?!purge")
    Call<ResponseBody> deleteProvider(@Path("uuid") String uuid);

    /**
     * Add provider call.
     *
     * @param provider the provider
     * @return the call
     */
    @POST("provider")
    Call<Provider> addProvider(@Body Provider provider);

    /**
     * Update provider call.
     *
     * @param uuid     the uuid
     * @param provider the provider
     * @return the call
     */
    @POST("provider/{uuid}")
    Call<Provider> updateProvider(@Path("uuid") String uuid,
                                  @Body Provider provider);

    /**
     * Gets allergies.
     *
     * @param uuid the uuid
     * @return the allergies
     */
    @GET("patient/{uuid}/allergy")
    Call<Results<Allergy>> getAllergies(@Path("uuid") String uuid);

    /**
     * Delete allergy call.
     *
     * @param patientUuid the patient uuid
     * @param allergyUuid the allergy uuid
     * @return the call
     */
    @DELETE("patient/{patientUuid}/allergy/{allergyUuid}")
    Call<ResponseBody> deleteAllergy(@Path("patientUuid") String patientUuid,
                                     @Path("allergyUuid") String allergyUuid);

    /**
     * Create allergy call.
     *
     * @param uuid          the uuid
     * @param allergyCreate the allergy create
     * @return the call
     */
    @POST("patient/{uuid}/allergy")
    Call<Allergy> createAllergy(@Path("uuid") String uuid,
                                @Body AllergyCreate allergyCreate);

    /**
     * Update allergy call.
     *
     * @param patientUuid   the patient uuid
     * @param allergyUuid   the allergy uuid
     * @param allergyCreate the allergy create
     * @return the call
     */
    @POST("patient/{patientUuid}/allergy/{allergyUuid}")
    Call<Allergy> updateAllergy(@Path("patientUuid") String patientUuid,
                                @Path("allergyUuid") String allergyUuid,
                                @Body AllergyCreate allergyCreate);

    /**
     * Create Appointment
     *
     * @param appointment the appointment object
     * @return the call
     */
    @POST("appointmentscheduling/appointment")
    Call<Appointment> createAppointment(@Body Appointment appointment);

    /**
     * Create Appointment
     *
     * @param patientUUID the patient uuid
     * @param status the appointment status
     * @param typeUUID the appointment type uuid
     * @param timeslot the appointment timeslot
     * @return the call
     */
    @POST("appointmentscheduling/appointment")
    Call<Appointment> createAppointment(@Query("patient") String patientUUID,
                                        @Query("status") String status,
                                        @Query("appointmentType") String typeUUID,
                                        @Query("timeSlot") TimeSlot timeslot);

    /**
     * Get an Appointment
     *
     * @param uuid the uuid of the appointment
     * @return the Appointment
     */
    @GET("appointmentscheduling/appointment/{uuid}")
    Call<Results<Appointment>> getAppointment(@Path("uuid") String uuid);

    /**
     * Delete Appointment
     *
     * @param uuid the appointment uuid
     * @return the call
     */
    @DELETE("appointmentscheduling/appointment/{uuid}")
    Call<ResponseBody> deleteAppointment(@Path("uuid") String uuid);

    /**
     * Get Appointment(s) for a patient
     *
     * @param patientUuid the uuid of the patient
     * @param representation the representation to return
     *
     * @return the Appointment(s)
     */
    @GET("appointmentscheduling/appointment")
    Call<Results<Appointment>> getAppointmentsForPatient(@Query("patient") String patientUuid,
                                                      @Query("v") String representation);

    /**
     * Get all available TimeSlot(s)
     *
     * @return the TimeSlot(s)
     */
    @GET("appointmentscheduling/timeslot")
    Call<Results<TimeSlot>> getTimeslots(@Query("v") String representation);

    /**
     * Create a TimeSlot
     *
     * @param timeSlot the TimeSlot object
     * @return the call
     */
    @POST("appointmentscheduling/timeslot")
    Call<TimeSlot> createTimeslot(@Body TimeSlot timeSlot);

    /**
     * Create a TimeSlot
     *
     * @param startDate the start date
     * @param endDate the end date
     * @param appointmentBlock the Appointment Block object
     *
     * @return the call
     */
    @POST("appointmentscheduling/timeslot")
    Call<TimeSlot> createTimeslot(@Query("startDate") String startDate,
                                  @Query("endDate") String endDate,
                                  @Query("location") AppointmentBlock appointmentBlock);

    /**
     * Get a TimeSlot
     *
     * @param timeslotUuid the uuid of the TimeSlot
     * @param representation the representation to return
     *
     * @return the TimeSlot
     */
    @GET("appointmentscheduling/timeslot/{uuid}")
    Call<TimeSlot> getTimeslotByUuid(@Path("uuid") String timeslotUuid,
                                     @Query("v") String representation);

    /**
     * Delete TimeSlot
     *
     * @param timeslotUuid the TimeSlot uuid
     * @return the call
     */
    @DELETE("appointmentscheduling/timeslot/{uuid}")
    Call<ResponseBody> deleteTimeslot(@Path("uuid") String timeslotUuid);

    /**
     * Get all available Appointment Block(s)
     *
     * @return the Appointment Block(s)
     */
    @GET("appointmentscheduling/appointmentblock")
    Call<Results<AppointmentBlock>> getAppointmentBlocks(@Query("v") String representation);

    /**
     * Create an Appointment Block
     *
     * @param startDate the start date of the block
     * @param endDate the end date of the block
     * @param location the location name of the block
     * @param types the list of appointment types
     *
     * @return the call
     */
    @POST("appointmentscheduling/appointmentblock")
    Call<AppointmentBlock> createAppointmentBlock(@Query("startDate") String startDate,
                                                  @Query("endDate") String endDate,
                                                  @Query("location") String location,
                                                  @Query("types") List<AppointmentType> types);

    /**
     * Delete Appointment Block
     *
     * @param appointmentBlockUuid the Appointment Block uuid
     * @return the call
     */
    @DELETE("appointmentscheduling/appointmentblock/{uuid}")
    Call<ResponseBody> deleteAppointmentBlock(@Path("uuid") String appointmentBlockUuid);

    /**
     * Get an Appointment Block
     *
     * @param appointmentBlockUuid the uuid of the Appointment Block
     * @param representation the representation to return
     *
     * @return the Appointment Block
     */
    @GET("appointmentscheduling/appointmentblock/{uuid}")
    Call<AppointmentBlock> getAppointmentBlockByUuid(@Path("uuid") String appointmentBlockUuid,
                                                     @Query("v") String representation);

    /**
     * Get all available Appointment Type(s)
     *
     * @return the Appointment Type(s)
     */
    @GET("appointmentscheduling/appointmenttype")
    Call<Results<AppointmentType>> getAppointmentTypes(@Query("v") String representation);

    /**
     * Create an Appointment Type
     *
     * @param appointmentType the Appointment Type object
     * @return the call
     */
    @POST("appointmentscheduling/appointmenttype")
    Call<AppointmentType> createAppointmentType(@Body AppointmentType appointmentType);

    /**
     * Delete Appointment Type
     *
     * @param appointmentTypeUuid the Appointment Type uuid
     * @return the call
     */
    @DELETE("appointmentscheduling/appointmenttype/{uuid}")
    Call<ResponseBody> deleteAppointmentType(@Path("uuid") String appointmentTypeUuid);

    /**
     * Get an Appointment Type
     *
     * @param appointmentTypeUuid the uuid of the Appointment Type
     * @param representation the representation to return
     *
     * @return the Appointment Type
     */
    @GET("appointmentscheduling/appointmenttype/{uuid}")
    Call<AppointmentType> getAppointmentTypeByUuid(@Path("uuid") String appointmentTypeUuid,
                                                   @Query("v") String representation);

    /**
     * Create an Order
     *
     * @param orderCreate the orderCreate type object
     * @return the call
     */
    @POST("order")
    Call<OrderGet> createOrder(@Body OrderCreate orderCreate);

    /**
     * Get all orders for a patient
     *
     * @param patientUuid the patient uuid
     * @param representation the response representation
     *
     * @return the call
     */
    @GET("order")
    Call<Results<OrderGet>> getOrdersForPatient(@Query("patient") String patientUuid,
                                       @Query("v") String representation);

    /**
     * Get all orders for a patient and caresetting
     *
     * @param patientUuid the patient uuid
     * @param careSetting the caresetting string
     * @param representation the response representation
     *
     * @return the call
     */
    @GET("order")
    Call<Results<OrderGet>> getOrdersForPatient(@Query("patient") String patientUuid,
                                       @Query("careSetting") String careSetting,
                                       @Query("v") String representation);

    /**
     * Get all orders for a patient and ordertype
     *
     * @param patientUuid the patient uuid
     * @param ordertype the theordertype string
     * @param representation the response representation
     *
     * @return the call
     */
    @GET("order")
    Call<Results<OrderGet>> getOrdersForPatientWithOrderType(@Query("patient") String patientUuid,
                                                    @Query("ordertype") String ordertype,
                                                    @Query("v") String representation);

    /**
     * Get all orders for a patient and ordertype and caresetting
     *
     * @param patientUuid the patient uuid
     * @param ordertype the theordertype string
     * @param careSetting the caresetting string
     * @param representation the response representation
     *
     * @return the call
     */
    @GET("order")
    Call<Results<OrderGet>> getOrdersForPatient(@Query("patient") String patientUuid,
                                       @Query("ordertype") String ordertype,
                                       @Query("careSetting") String careSetting,
                                       @Query("v") String representation);

    /**
     * Get all orders for a patient from a given date
     *
     * @param patientUuid the patient uuid
     * @param activatedOnOrAfterDate the starting date
     * @param representation the response representation
     *
     * @return the call
     */
    @GET("order")
    Call<Results<OrderGet>> getOrdersForPatientFromDate(@Query("patient") String patientUuid,
                                               @Query("activatedOnOrAfterDate") String activatedOnOrAfterDate,
                                               @Query("v") String representation);

    /**
     * Get all orders for a patient and ordertype and caresetting and from a given date
     *
     * @param patientUuid the patient uuid
     * @param ordertype the theordertype string
     * @param careSetting the caresetting string
     * @param activatedOnOrAfterDate the starting date
     * @param representation the response representation
     *
     * @return the call
     */
    @GET("order")
    Call<Results<OrderGet>> getOrdersForPatient(@Query("patient") String patientUuid,
                                       @Query("ordertype") String ordertype,
                                       @Query("careSetting") String careSetting,
                                       @Query("activatedOnOrAfterDate") String activatedOnOrAfterDate,
                                       @Query("v") String representation);

    /**
     * Delete an order
     *
     * @param orderUuid the Order uuid
     * @return the call
     */
    @DELETE("order/{uuid}")
    Call<ResponseBody> deleteOrder(@Path("uuid") String orderUuid);
  
    /**
     * Get all the available Drugs
     *
     * @param representation the representation to return
     * @return the call
     */
    @GET("drug")
    Call<Results<Drug>> getAllDrugs(@Query("v") String representation);

    /**
     * Get a Drug by UUID
     *
     * @param uuid the uuid of the drug
     * @param representaion the representation to return
     * @return the call
     */
    @GET("drug/{uuid}")
    Call<Drug> getDrugByUuid(@Path("uuid") String uuid, @Query("v") String representaion);

    /**
     * Create a Drug by UUID
     *
     * @param drug the object of type DrugCreate object to create
     * @return the call
     */
    @POST("drug")
    Call<Drug> createDrug(@Body DrugCreate drug);

    /**
     * Update a Drug by UUID
     *
     * @param uuid the uuid of the Drug
     * @param drug the object of type DrugCreate object to create
     * @return the call
     */
    @POST("drug/{uuid}")
    Call<Drug> updateDrug(@Path("uuid") String uuid, @Body DrugCreate drug);

    /**
     * Delete a Drug
     *
     * @param uuid the uuid of the Drug
     * @return the call
     */
    @DELETE("drug/{uuid}")
    Call<Drug> deleteDrug(@Path("uuid") String uuid);
  
    /**
     * Get all the available Programs
     *
     * @param representation the representation to return
     * @return the call
     */
    @GET("program")
    Call<Results<ProgramGet>> getAllPrograms(@Query("v") String representation);

    /**
     * Get a Program by UUID
     *
     * @param uuid the uuid of the Program
     * @param representaion the representation to return
     * @return the call
     */
    @GET("program/{uuid}")
    Call<ProgramGet> getProgramByUuid(@Path("uuid") String uuid, @Query("v") String representaion);

    /**
     * Create a new Program
     *
     * @param program the object of type ProgramCreate
     * @return the call
     */
    @POST("program")
    Call<ProgramGet> createProgram(@Body ProgramCreate program);

    /**
     * Update a Program by UUID
     *
     * @param uuid the uuid of the Program
     * @param updatedProgram the object of type ProgramCreate
     * @return the call
     */
    @POST("program/{uuid}")
    Call<ProgramGet> updateProgram(@Path("uuid") String uuid, @Body ProgramCreate updatedProgram);

    /**
     * Delete a Program
     *
     * @param uuid the uuid of the Program to delete
     * @return the call
     */
    @DELETE("program/{uuid}")
    Call<ProgramGet> deleteProgram(@Path("uuid") String uuid);
}
