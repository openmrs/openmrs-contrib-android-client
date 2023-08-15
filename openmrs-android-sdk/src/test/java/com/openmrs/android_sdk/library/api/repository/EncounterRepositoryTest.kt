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
import com.openmrs.android_sdk.library.dao.LocationRoomDAO
import com.openmrs.android_sdk.library.dao.VisitRoomDAO
import com.openmrs.android_sdk.library.dao.EncounterRoomDAO
import com.openmrs.android_sdk.library.dao.EncounterTypeRoomDAO
import com.openmrs.android_sdk.library.dao.ObservationRoomDAO
import com.openmrs.android_sdk.library.dao.EncounterDAO
import com.openmrs.android_sdk.library.dao.PatientDAO
import com.openmrs.android_sdk.library.dao.PatientRoomDAO
import com.openmrs.android_sdk.library.dao.VisitDAO
import com.openmrs.android_sdk.library.databases.AppDatabase
import com.openmrs.android_sdk.library.databases.entities.StandaloneEncounterEntity
import com.openmrs.android_sdk.library.models.Patient
import com.openmrs.android_sdk.library.models.Obscreate
import com.openmrs.android_sdk.library.models.Encountercreate
import com.openmrs.android_sdk.library.models.EncounterProviderCreate
import com.openmrs.android_sdk.library.models.Visit
import com.openmrs.android_sdk.utilities.NetworkUtils
import com.openmrs.android_sdk.utilities.execute
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import io.mockk.*
import io.reactivex.Single
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
    val encounterRoomDAO: EncounterRoomDAO = mockk(relaxed = true)
    val visitRoomDAO: VisitRoomDAO = mockk()
    val encounterDAO: EncounterDAO = mockk(relaxed = true)
    val encounterTypeRoomDAO: EncounterTypeRoomDAO = mockk()
    val observationRoomDAO: ObservationRoomDAO = mockk()
    val patientRoomDAO: PatientRoomDAO = mockk()
    val encounterCreate = Encountercreate()

    @Before
    fun init() {
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

        val standaloneEncounterEntity: StandaloneEncounterEntity = mockk(relaxed = true)
        every { standaloneEncounterEntity.uuid } returns "d3360c01-9813-4ff8-bd81-909af6612632"
        val listToReturn: List<StandaloneEncounterEntity> = listOf(standaloneEncounterEntity)
        every { encounterRoomDAO.getAllStandAloneEncounters() } returns Single.just(listToReturn)

        every { encounterDAO.saveStandaloneEncounters(any()) } returns listOf(1L,2L,3L)

        mockkStatic(OpenmrsAndroid::class)
        every { OpenmrsAndroid.getInstance() } returns context
        every { context.getApplicationContext() } returns appContext
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
        clearAllMocks()
        mockWebServer.shutdown()
    }

    @Inject
    lateinit var encounterRepository: EncounterRepository

    @Test
    fun `getAllEncounterResourcesByPatientUuid success return List of resources`(){
        enqueueMockResponse("mocked_responses/EncounterRepository/EncounterResourcesGet-success.json")

        encounterRepository.restApi = encounterApi
        val patientUuid = "96be32d2-9367-4d1d-a285-79a5e5db12b8"
        val resourceList = encounterRepository.getAllEncounterResourcesByPatientUuid(patientUuid).toBlocking().first()
        val testResource = resourceList.get(2)

        assertEquals(testResource.uuid, "0cc4a9e5-d44a-461e-ad64-0f04817c5bd0")
        assertEquals(testResource.display, "Vitals 19/04/2015")
    }

    @Test
    fun `getAllEncountersByPatientUuidAndSaveLocally success returns List of Encounters`(){
        enqueueMockResponse("mocked_responses/EncounterRepository/EncounterResourcesGet-success.json")
        enqueueMockResponse("mocked_responses/EncounterRepository/EncounterGet-success.json")
        enqueueMockResponse("mocked_responses/EncounterRepository/EncounterGet-success.json")
        enqueueMockResponse("mocked_responses/EncounterRepository/EncounterGet-success.json")

        encounterRepository.restApi = encounterApi
        val patientUuid = "96be32d2-9367-4d1d-a285-79a5e5db12b8"
        val resourceList = encounterRepository.getAllEncountersByPatientUuidAndSaveLocally(patientUuid).toBlocking().first()
        val testResource = resourceList.get(1)

        assertEquals(testResource.uuid, "d3360c01-9813-4ff8-bd81-909af6612632")
        assertEquals(testResource.display, "Vitals 24/02/2015")
    }

    @Test
    fun `getAllEncounterResourcesByVisitUuid success returns encounters`(){
        enqueueMockResponse("mocked_responses/EncounterRepository/VisitGet-success.json")

        encounterRepository.visitRepository.restApi = encounterApi
        val visitUuid = "4d67b954-216a-4b19-9cab-94c59cc5b705"
        val encounter = encounterRepository.getAllEncounterResourcesByVisitUuid(visitUuid)!!.toBlocking().first()

        assertEquals(encounter.uuid, "1ef9e5ee-ca7a-4a4b-852c-42c107526f81")
        assertEquals(encounter.display, "Discharge 11/12/2016")
    }

    @Test
    fun `getAllEncountersByPatientUuidAndEncounterTypeAndSaveLocally success returns List of encounters`(){
        enqueueMockResponse("mocked_responses/EncounterRepository/EncounterResourcesGet-success.json")
        enqueueMockResponse("mocked_responses/EncounterRepository/EncounterGet-success.json")
        enqueueMockResponse("mocked_responses/EncounterRepository/EncounterGet-success.json")
        enqueueMockResponse("mocked_responses/EncounterRepository/EncounterGet-success.json")

        encounterRepository.restApi = encounterApi
        val patientUuid = "96be32d2-9367-4d1d-a285-79a5e5db12b8"
        val encounterTypeUuid = "67a71486-1a54-468f-ac3e-7091a9a79584"

        val resourceList = encounterRepository.getAllEncountersByPatientUuidAndEncounterTypeAndSaveLocally(patientUuid, encounterTypeUuid).toBlocking().first()
        val testResource = resourceList.get(1)

        assertEquals(testResource.uuid, "d3360c01-9813-4ff8-bd81-909af6612632")
        assertEquals(testResource.display, "Vitals 24/02/2015")
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