/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package com.openmrs.android_sdk.library.api.repository

import android.util.Log
import com.openmrs.android_sdk.library.dao.ObservationDAO
import com.openmrs.android_sdk.library.databases.AppDatabaseHelper
import com.openmrs.android_sdk.library.databases.entities.ObservationEntity
import com.openmrs.android_sdk.library.models.Observation
import com.openmrs.android_sdk.library.models.Resource
import com.openmrs.android_sdk.utilities.NetworkUtils
import com.openmrs.android_sdk.utilities.execute
import rx.Observable
import java.util.concurrent.Callable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ObservationRepository @Inject constructor(
    private val observationDAO: ObservationDAO
) : BaseRepository() {

    /**
     * Create an Observation record on the server from the locally
     * stored observation entity record
     *
     * @param observationEntity the locally stored observation
     * @return Observable<Observation>
     */
    fun createObservationFromLocal(observationEntity: ObservationEntity): Observable<Observation> {
        val observation = AppDatabaseHelper.convert(observationEntity)

        return AppDatabaseHelper.createObservableIO(Callable {
            if (!NetworkUtils.isOnline())
                throw Exception("To retrieve observations, an internet connection is required.")

            restApi.createObservation(observation).execute().run{
                if (isSuccessful && body() != null) {
                    return@Callable this.body()!!
                } else {
                    throw Exception("Get Observations error: ${message()}")
                }
            }
        })
    }

    /**
     * Get an Observation from Observation Uuid from the server
     *
     * @param uuid the UUID of the observation
     * @return Observable<Observation>
     */
    fun getObservationByUuid(uuid: String): Observable<Observation> {
        return AppDatabaseHelper.createObservableIO(Callable {
            if (!NetworkUtils.isOnline())
                throw Exception("To retrieve observations, an internet connection is required.")

            restApi.getObservationByUuid(uuid).execute().run {
                if (isSuccessful && body() != null) {
                    return@Callable this.body()!!
                } else {
                    throw Exception("Get Observations error: ${message()}")
                }
            }
        })
    }

    /**
     * Get all observation resources of a patient from the server.
     *
     * @param uuid the UUID of the patient
     * @return Observable<List<Resource>> the resource list of observations
     */
    fun getAllObservationResourcesByPatientUuid(uuid: String): Observable<List<Resource>> {
        return AppDatabaseHelper.createObservableIO(Callable {
            if (!NetworkUtils.isOnline())
                throw Exception("To retrieve observations, an internet connection is required")

            restApi.getObservationsByPatientUuid(uuid).execute().run {
                if (isSuccessful) {
                    return@Callable this.body()?.results!!
                } else {
                    throw Exception("Get Observations error: ${message()}")
                }
            }
        })
    }

    /**
     * Get all observation resources of a patient from the server.
     *
     * @param encounterUuid the UUID of the encounter
     * @return the resource list of observations
     */
    fun getAllObservationResourcesByEncounterUuid(encounterUuid: String): Observable<List<Resource>> {
        return AppDatabaseHelper.createObservableIO(Callable {
            if (!NetworkUtils.isOnline())
                throw Exception("To retrieve observations, an internet connection is required")

            restApi.getObservationsByEncounterUuid(encounterUuid).execute().run {
                if (isSuccessful) {
                    return@Callable this.body()?.results!!
                } else {
                    throw Exception("Get Observations error: ${message()}")
                }
            }
        })
    }

    /**
     * Get all observations of a patient from the server
     * and save to local database.
     *
     * @param uuid the UUID of the patient
     * @return the list of observations
     */
    fun getAllObservationsByPatientUuidAndSaveLocally(uuid: String): Observable<List<Observation>> {
        val observationList: MutableList<Observation> = mutableListOf()
        if (!NetworkUtils.isOnline())
            throw Exception("To retrieve observations, an internet connection is required")

        restApi.getObservationsByPatientUuid(uuid).execute().run {
            if (isSuccessful && this.body() != null) {

                val observationResources: List<Resource> = this.body()!!.results
                for (observationResource in observationResources) {
                    val observation = getObservationByUuid(observationResource.uuid!!)
                    observationList.add(observation.execute())
                }
                observationDAO.deleteAllStandaloneObservations(uuid)      //delete previous list
                observationDAO.saveStandaloneObservations(observationList) //save latest list
                return Observable.just(observationList.toList())
            } else {
                throw Exception("Get Observations error: ${message()}")
            }
        }
    }

    /**
     * Get all observations of an encounter from the server
     * and save to local database.
     *
     * @param encounterUuid the UUID of the encounter
     * @return the list of observations
     */
    fun getAllObservationsByEncounterUuidAndSaveLocally(encounterUuid: String): Observable<List<Observation>> {
        val observationList: MutableList<Observation> = mutableListOf()
        if (!NetworkUtils.isOnline())
            throw Exception("To retrieve observations, an internet connection is required")

        restApi.getObservationsByEncounterUuid(encounterUuid).execute().run {
            if (isSuccessful && this.body() != null) {

                val observationResources: List<Resource> = this.body()!!.results
                for (observationResource in observationResources) {
                    val observation = getObservationByUuid(observationResource.uuid!!)
                    observationList.add(observation.execute())
                }
                observationDAO.saveStandaloneObservations(observationList) //save latest list
                return Observable.just(observationList.toList())
            } else {
                throw Exception("Get Observations error: ${message()}")
            }
        }
    }

    /**
     * Get all observations of a patient with a given concept from the server
     * and save to local database.
     *
     * @param patientUuid the UUID of the patient
     * @param conceptUuid the UUID of the concept
     *
     * @return the list of observations
     */
    fun getAllObservationsByConceptUuidSaveLocally(patientUuid: String, conceptUuid: String): Observable<List<Observation>> {
        val observationList: MutableList<Observation> = mutableListOf()
        if (!NetworkUtils.isOnline())
            throw Exception("To retrieve observations, an internet connection is required")

        restApi.getObservationsByConceptUuid(patientUuid, conceptUuid).execute().run {
            if (isSuccessful && this.body() != null) {

                val observationResources: List<Resource> = this.body()!!.results
                for (observationResource in observationResources) {
                    val observation = getObservationByUuid(observationResource.uuid!!)
                    observationList.add(observation.execute())
                }
                observationDAO.saveStandaloneObservations(observationList) //save latest list
                return Observable.just(observationList.toList())
            } else {
                throw Exception("Get Observations error: ${message()}")
            }
        }
    }

}