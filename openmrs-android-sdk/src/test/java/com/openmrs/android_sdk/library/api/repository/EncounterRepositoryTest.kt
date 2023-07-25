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

import android.content.Context
import com.google.gson.GsonBuilder
import com.openmrs.android_sdk.library.OpenmrsAndroid
import com.openmrs.android_sdk.library.api.RestApi
import com.openmrs.android_sdk.library.dao.*
import com.openmrs.android_sdk.library.databases.AppDatabase
import com.openmrs.android_sdk.library.models.*
import com.openmrs.android_sdk.utilities.NetworkUtils
import com.openmrs.android_sdk.utilities.ObservationDeserializer
import com.openmrs.android_sdk.utilities.ResourceSerializer
import com.openmrs.android_sdk.utilities.execute
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import io.mockk.*
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okio.buffer
import okio.source
import org.checkerframework.common.reflection.qual.NewInstance
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import rx.Observable
import javax.inject.Inject

@HiltAndroidTest
@RunWith(RobolectricTestRunner::class)
@Config(application = HiltTestApplication::class)
class EncounterRepositoryTest {
    lateinit var mockWebServer: MockWebServer
    lateinit var encounterApi: RestApi

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    val context: Context = mockk(relaxed = true)
    val appContext: Context = mockk(relaxed = true)
    val appDatabase: AppDatabase = mockk()
    val locationRoomDAO: LocationRoomDAO = mockk()
    val encounterRoomDAO: EncounterRoomDAO = mockk()
    val visitRoomDAO: VisitRoomDAO = mockk()
    val encounterTypeRoomDAO: EncounterTypeRoomDAO = mockk()
    val observationRoomDAO: ObservationRoomDAO = mockk()
    val patientRoomDAO: PatientRoomDAO = mockk()
    val encounterCreateRoomDAO: EncounterCreateRoomDAO = mockk()

    @Before
    fun init() {
        every { context.getApplicationContext() } returns appContext
        mockkStatic(OpenmrsAndroid::class)
        every { OpenmrsAndroid.getInstance() } returns context
        mockkStatic(AppDatabase::class)
        every { AppDatabase.getDatabase(appContext) } returns appDatabase
        every { appDatabase.locationRoomDAO() } returns locationRoomDAO
        every { appDatabase.patientRoomDAO() } returns patientRoomDAO
        every { appDatabase.encounterRoomDAO() } returns encounterRoomDAO
        every { appDatabase.visitRoomDAO() } returns visitRoomDAO
        every { appDatabase.encounterTypeRoomDAO() } returns encounterTypeRoomDAO
        every { appDatabase.observationRoomDAO() } returns observationRoomDAO

        val patient: Patient = mockk(relaxed = true)
        every { patient.isSynced } returns true

        val visit: Visit = mockk(relaxed = true)
        every { visit.uuid } returns "fakeUuid"

        mockkConstructor(PatientDAO::class)
        every { anyConstructed<PatientDAO>().findPatientByID("1234") } returns patient

        val fakeObservable: Observable<Visit> = mockk(relaxed = true)
        every { fakeObservable.execute() } returns visit

        mockkConstructor(VisitDAO::class)
        every { anyConstructed<VisitDAO>().getActiveVisitByPatientId(1234) } returns fakeObservable

        mockkStatic(NetworkUtils::class)
        every { NetworkUtils.isOnline() } returns true

        mockWebServer = MockWebServer()
        mockWebServer.start()

        val gsonBuilder = GsonBuilder()
        val myGson = gsonBuilder
            .excludeFieldsWithoutExposeAnnotation()
            .create()

        val retrofit = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create(myGson))
            .build()

        encounterApi = retrofit.create(RestApi::class.java)

        hiltRule.inject()
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Inject
    lateinit var encounterRepository: EncounterRepository

    @Test
    fun `saveEncounter success return Success`(){
        enqueueMockResponse("mocked_responses/EncounterRepository/EncounterPost-success.json")

        encounterRepository.restApi = encounterApi

        val encounterCreate = Encountercreate()
        encounterCreate.id = 1
        encounterCreate.visit = "Some visit"
        encounterCreate.patient = "Some patient"
        encounterCreate.patientId = 1234
        encounterCreate.encounterType = "Some encounter type"
        encounterCreate.formname = "Some form name"
        encounterCreate.synced = false
        val obs1 = Obscreate()
        obs1.value = "101"
        obs1.concept = "Observation1"
        val obs2 = Obscreate()
        obs2.value = "102"
        obs2.concept = "Observation2"
        encounterCreate.observations = listOf(obs1, obs2)
        encounterCreate.formUuid = "Some form UUID"
        encounterCreate.location = "Some location"
        val provider1 = EncounterProviderCreate("12345", "67890")
        val provider2 = EncounterProviderCreate("abcd", "efgh")
        encounterCreate.encounterProvider = listOf(provider1, provider2)

        every { encounterCreateRoomDAO.updateExistingEncounter(encounterCreate) } just Runs

        val resultType = encounterRepository.saveEncounter(encounterCreate).toBlocking().first()

        assertEquals(ResultType.EncounterSubmissionSuccess, resultType)
    }

    fun enqueueMockResponse(fileName: String) {
        javaClass.classLoader?.let {
            val inputStream = it.getResourceAsStream(fileName)
            val source = inputStream.source().buffer()
            val mockResponse = MockResponse()
            mockResponse.setBody(source.readString(Charsets.UTF_8))
            mockResponse.setResponseCode(200)
            mockWebServer.enqueue(mockResponse)
        }
    }
}