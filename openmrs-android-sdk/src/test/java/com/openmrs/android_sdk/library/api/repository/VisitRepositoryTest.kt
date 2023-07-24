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
import com.openmrs.android_sdk.library.models.Observation
import com.openmrs.android_sdk.library.models.Patient
import com.openmrs.android_sdk.library.models.Resource
import com.openmrs.android_sdk.utilities.ObservationDeserializer
import com.openmrs.android_sdk.utilities.ResourceSerializer
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okio.buffer
import okio.source
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
import javax.inject.Inject

@HiltAndroidTest
@RunWith(RobolectricTestRunner::class)
@Config(application = HiltTestApplication::class)
class VisitRepositoryTest {

    lateinit var mockWebServer: MockWebServer
    lateinit var visitApi: RestApi

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

    @Before
    fun init() {
        every { context.getApplicationContext() } returns appContext
        mockkStatic(OpenmrsAndroid::class)
        every { OpenmrsAndroid.getInstance() } returns context
        mockkStatic(AppDatabase::class)
        every { AppDatabase.getDatabase(appContext) } returns appDatabase
        every { appDatabase.locationRoomDAO() } returns locationRoomDAO
        every { appDatabase.encounterRoomDAO() } returns encounterRoomDAO
        every { appDatabase.visitRoomDAO() } returns visitRoomDAO
        every { appDatabase.encounterTypeRoomDAO() } returns encounterTypeRoomDAO
        every { appDatabase.observationRoomDAO() } returns observationRoomDAO

        mockWebServer = MockWebServer()
        mockWebServer.start()

        val gsonBuilder = GsonBuilder()
        val myGson = gsonBuilder
            .excludeFieldsWithoutExposeAnnotation()
            .registerTypeHierarchyAdapter(Resource::class.java, ResourceSerializer())
            .registerTypeHierarchyAdapter(Observation::class.java, ObservationDeserializer())
            .create()

        val retrofit = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create(myGson))
            .build()

        visitApi = retrofit.create(RestApi::class.java)

        hiltRule.inject()
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Inject
    lateinit var visitRepository: VisitRepository

    @Test
    fun `syncVisitsData success return visits`(){
        enqueueMockResponse("mocked_responses/VisitRepository/VisitsGet-success.json")

        visitRepository.restApi = visitApi
        visitRepository.visitDAO = mockk(relaxed = true)

        val patient: Patient = mockk(relaxed = true)
        every { patient.uuid} returns "c0cbe231-deb8-4cfa-89b4-8fb4570685fc"

        val visit = visitRepository.syncVisitsData(patient).toBlocking().first().get(0)

        assertEquals(visit.uuid, "36475629-6652-44e9-a42b-c2b3b7438f72")
    }

    @Test
    fun `getVisit success return visit`(){
        enqueueMockResponse("mocked_responses/VisitRepository/GetVisit.json")

        visitRepository.restApi = visitApi
        visitRepository.visitDAO = mockk(relaxed = true)

        val visitUuid = "36475629-6652-44e9-a42b-c2b3b7438f72"

        val visit = visitRepository.getVisit(visitUuid).toBlocking().first()

        assertEquals(visit.uuid, "36475629-6652-44e9-a42b-c2b3b7438f72")
    }

    @Test
    fun `getVisitType success return visitTypes`(){
        enqueueMockResponse("mocked_responses/VisitRepository/GetVisitType.json")

        visitRepository.restApi = visitApi
        visitRepository.visitDAO = mockk(relaxed = true)

        val visit = visitRepository.visitType.toBlocking().first()

        assertEquals(visit.uuid, "7b0f5697-27e3-40c4-8bae-f4049abfb4ed")
    }

    @Test
    fun `getVisitsByLocationAndSaveLocally success return visits`(){
        enqueueMockResponse("mocked_responses/VisitRepository/VisitsGet-success.json")

        visitRepository.restApi = visitApi
        visitRepository.visitDAO = mockk(relaxed = true)

        val patient: Patient = mockk(relaxed = true)
        every { patient.uuid} returns "c0cbe231-deb8-4cfa-89b4-8fb4570685fc"
        val locationUuid = "8d6c993e-c2cc-11de-8d13-0010c6dffd0f"

        val visit = visitRepository.getVisitsByLocationAndSaveLocally(patient, locationUuid).toBlocking().first().get(0)

        assertEquals(visit.location.uuid, "8d6c993e-c2cc-11de-8d13-0010c6dffd0f")
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