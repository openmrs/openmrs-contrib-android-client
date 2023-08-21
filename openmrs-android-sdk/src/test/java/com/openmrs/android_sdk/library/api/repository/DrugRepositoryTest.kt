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
import com.openmrs.android_sdk.library.dao.DrugRoomDAO
import com.openmrs.android_sdk.library.databases.AppDatabase
import com.openmrs.android_sdk.library.models.Drug
import com.openmrs.android_sdk.library.models.DrugCreate
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
class DrugRepositoryTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    val context: Context = mockk(relaxed = true)
    val appContext: Context = mockk(relaxed = true)
    val appDatabase: AppDatabase = mockk()
    val drugRoomDAO: DrugRoomDAO = mockk()

    lateinit var mockWebServer: MockWebServer
    lateinit var drugApi: RestApi

    @Inject
    lateinit var drugRepository: DrugRepository

    @Before
    fun setup() {

        mockkStatic(OpenmrsAndroid::class)
        every { OpenmrsAndroid.getInstance() } returns context
        every { context.getApplicationContext() } returns appContext

        mockkStatic(AppDatabase::class)
        every { AppDatabase.getDatabase(appContext) } returns appDatabase
        every { appDatabase.drugRoomDAO() } returns drugRoomDAO

        mockkStatic(NetworkUtils::class)
        every { NetworkUtils.isOnline() } returns true

        mockWebServer = MockWebServer()
        mockWebServer.start()

        val retrofit = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        drugApi = retrofit.create(RestApi::class.java)
        hiltRule.inject()
    }

    @After
    fun tearDown() {
        clearAllMocks()
        mockWebServer.shutdown()
    }

    @Test
    fun createDrug_success_returnsDrug() {

        drugRepository.restApi = drugApi
        val drugCreate = DrugCreate(
            true,
            "c9c0c627-7ba6-4880-a702-cfc985a296fa",
            "1513AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA",
            6,
            2,
            "Test Drug"
        )

        enqueueMockResponse("mocked_responses/DrugRepository/DrugCreate-success.json")
        val result: Drug = drugRepository.createDrug(drugCreate).toBlocking().first()

        Assert.assertEquals(result.concept!!.uuid, drugCreate.concept)
    }

    @Test
    fun getAllDrugs_success_returnsListOfDrugs(){
        drugRepository.restApi = drugApi
        enqueueMockResponse("mocked_responses/DrugRepository/DrugGetAll-success.json")

        val result: List<Drug> = drugRepository.getAllDrugs().toBlocking().first()

        Assert.assertEquals(result[2].uuid, "dd87f758-a49b-44ac-841b-16b61c176244")
        Assert.assertEquals(result[2].concept!!.display, "Azithromycin")
    }

    @Test
    fun getDrugByUuid_success_returnsCorrectDrug(){

        drugRepository.restApi = drugApi
        enqueueMockResponse("mocked_responses/DrugRepository/DrugGet-success.json")

        val result: Drug = drugRepository.getDrugByUuid("e6b91fc3-8961-493e-9acb-5853162ffec7").toBlocking().first()

        Assert.assertEquals(result.uuid, "e6b91fc3-8961-493e-9acb-5853162ffec7")
        Assert.assertEquals(result.concept!!.display, "Famotidine")
    }

    @Test
    fun getAllDrugsAndSaveLocally_success(){

        every { drugRoomDAO.insertOrUpdateDrugs(any()) } returns listOf(1L,2L,3L)
        drugRepository.restApi = drugApi
        enqueueMockResponse("mocked_responses/DrugRepository/DrugGetAll-success.json")

        val result: List<Drug> = drugRepository.getAllDrugsAndSaveLocally().toBlocking().first()

        Assert.assertEquals(result[1].uuid, "e6b91fc3-8961-493e-9acb-5853162ffec7")
        Assert.assertEquals(result[1].concept!!.display, "Famotidine")
    }

    @Test
    fun getDrugByUuidAndSaveLocally_success(){

        every { drugRoomDAO.createDrug(any()) } returns 1L
        drugRepository.restApi = drugApi
        enqueueMockResponse("mocked_responses/DrugRepository/DrugGet-success.json")

        val result: Drug = drugRepository.getDrugByUuidAndSaveLocally("e6b91fc3-8961-493e-9acb-5853162ffec7").toBlocking().first()

        Assert.assertEquals(result.uuid, "e6b91fc3-8961-493e-9acb-5853162ffec7")
        Assert.assertEquals(result.concept!!.display, "Famotidine")
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
