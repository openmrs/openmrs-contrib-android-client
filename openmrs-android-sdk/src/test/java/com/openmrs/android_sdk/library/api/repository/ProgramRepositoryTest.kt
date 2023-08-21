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
package com.openmrs.android_sdk.library.api.repository

import android.content.Context
import com.openmrs.android_sdk.library.OpenmrsAndroid
import com.openmrs.android_sdk.library.api.RestApi
import com.openmrs.android_sdk.library.dao.ProgramRoomDAO
import com.openmrs.android_sdk.library.databases.AppDatabase
import com.openmrs.android_sdk.library.models.ProgramCreate
import com.openmrs.android_sdk.library.models.ProgramGet
import com.openmrs.android_sdk.utilities.NetworkUtils
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okio.buffer
import okio.source
import org.junit.After
import org.junit.Assert
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
class ProgramRepositoryTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    val context: Context = mockk(relaxed = true)
    val appContext: Context = mockk(relaxed = true)
    val appDatabase: AppDatabase = mockk()
    val programRoomDAO: ProgramRoomDAO = mockk()

    lateinit var mockWebServer: MockWebServer
    lateinit var programApi: RestApi

    @Inject
    lateinit var programRepository: ProgramRepository

    @Before
    fun setup() {

        mockkStatic(OpenmrsAndroid::class)
        every { OpenmrsAndroid.getInstance() } returns context
        every { context.getApplicationContext() } returns appContext

        mockkStatic(AppDatabase::class)
        every { AppDatabase.getDatabase(appContext) } returns appDatabase
        every { appDatabase.programRoomDAO() } returns programRoomDAO

        mockkStatic(NetworkUtils::class)
        every { NetworkUtils.isOnline() } returns true

        mockWebServer = MockWebServer()
        mockWebServer.start()

        val retrofit = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        programApi = retrofit.create(RestApi::class.java)
        hiltRule.inject()
    }

    @After
    fun tearDown() {
        clearAllMocks()
        mockWebServer.shutdown()
    }

    @Test
    fun createProgram_success_returnsProgram() {
        programRepository.restApi = programApi
        val programCreate = ProgramCreate(
            "HIV Care and Treatment",
            "HIV Care and Treatment",
            false,
            "138405AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA",
            null
        )

        enqueueMockResponse("mocked_responses/ProgramRepository/ProgramGet-success.json")
        val result: ProgramGet = programRepository.createProgram(programCreate).toBlocking().first()

        Assert.assertEquals(result.name, programCreate.name)
    }

    @Test
    fun getAllPrograms_success_returnsListOfPrograms(){
        programRepository.restApi = programApi

        enqueueMockResponse("mocked_responses/ProgramRepository/ProgramGetAll-success.json")
        val result: List<ProgramGet> = programRepository.getAllPrograms().toBlocking().first()

        Assert.assertEquals(result[1].uuid, "d1b6cd43-8ac7-4cdd-8fb4-fe51635c82b4")
        Assert.assertEquals(result[1].name, "PMTCT")
    }

    @Test
    fun getProgramByUuid_success_returnsProgram(){
        programRepository.restApi = programApi

        enqueueMockResponse("mocked_responses/ProgramRepository/ProgramGet-success.json")
        val result: ProgramGet = programRepository.getProgramByUuid("64f950e6-1b07-4ac0-8e7e-f3e148f3463f").toBlocking().first()

        Assert.assertEquals(result.name, "HIV Care and Treatment")
    }

    @Test
    fun getAllProgramsAndSaveLocally_success(){

        every { programRoomDAO.insertOrUpdatePrograms(any()) } returns listOf(1L,2L,3L)
        programRepository.restApi = programApi
        enqueueMockResponse("mocked_responses/ProgramRepository/ProgramGetAll-success.json")

        val result: List<ProgramGet> = programRepository.getAllProgramsAndSaveLocally().toBlocking().first()

        Assert.assertEquals(result[2].uuid, "ac1bbc45-8c35-49ff-a574-9553ff789527")
        Assert.assertEquals(result[2].name, "HIV Preventative Services (PEP/PrEP)")
    }

    @Test
    fun getProgramByUuidAndSaveLocally_success(){

        every { programRoomDAO.insertProgram(any()) } returns 1L
        programRepository.restApi = programApi
        enqueueMockResponse("mocked_responses/ProgramRepository/ProgramGet-success.json")

        val result: ProgramGet = programRepository.getProgramByUuidAndSaveLocally("e6b91fc3-8961-493e-9acb-5853162ffec7").toBlocking().first()

        Assert.assertEquals(result.uuid, "64f950e6-1b07-4ac0-8e7e-f3e148f3463f")
        Assert.assertEquals(result.name, "HIV Care and Treatment")
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
