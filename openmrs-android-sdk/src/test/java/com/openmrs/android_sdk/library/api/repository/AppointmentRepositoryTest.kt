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

import com.openmrs.android_sdk.library.api.RestApi
import com.openmrs.android_sdk.library.databases.entities.LocationEntity
import com.openmrs.android_sdk.library.models.*
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
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
class AppointmentRepositoryTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    lateinit var mockWebServer: MockWebServer
    lateinit var appointmentApi: RestApi

    @Inject
    lateinit var appointmentRepository: AppointmentRepository

    val startDate = "2023-07-15T10:00:00Z"
    val endDate = "2023-07-15T11:00:00Z"
    val location = "Sample Location"

    val appointmentTypesList = listOf(
        AppointmentType().apply {
            visitType = VisitType().apply {
                uuid = "12345678-1234-1234-1234-123456789abc"
                display = "Type A"
            }
            description = "Sample description 1"
            duration = "30 minutes"
            confidential = false
        },
        AppointmentType().apply {
            visitType = VisitType().apply {
                uuid = "98765432-5432-5432-5432-987654321fed"
                display = "Type B"
            }
            description = "Sample description 2"
            duration = "60 minutes"
            confidential = true
        }
    )

    val appointmentBlockGet = AppointmentBlock().apply {
        startDate = "2023-07-15T10:00:00Z"
        endDate = "2023-07-15T11:00:00Z"
        provider = Provider().apply {
            identifier = "user1"
            retired = false
        }
        location = LocationEntity("Sample Clinic").apply {
            name = "Sample Clinic"
            address_1 = "123 Main St"
        }
        types = listOf(
            AppointmentType().apply {
                visitType = VisitType().apply {
                    uuid = "12345678-1234-1234-1234-123456789abc"
                    display = "Type A"
                }
                description = "Sample description 1"
                duration = "30 minutes"
                confidential = false
            },
            AppointmentType().apply {
                visitType = VisitType().apply {
                    uuid = "98765432-5432-5432-5432-987654321fed"
                    display = "Type B"
                }
                description = "Sample description 2"
                duration = "60 minutes"
                confidential = true
            }
        )
    }

    val timeSlotGet = TimeSlot().apply {
        startDate = "2023-07-15T10:00:00Z"
        endDate = "2023-07-15T11:00:00Z"
        appointmentBlock = appointmentBlockGet
    }

    val appointmentType = AppointmentType().apply {
        visitType = VisitType().apply {
            uuid = "12345678-1234-1234-1234-123456789abc"
            display = "Type A"
        }
        description = "Sample description 1"
        duration = "30 minutes"
        confidential = false
    }

    @Before
    fun setup() {
        hiltRule.inject()
        mockWebServer = MockWebServer()
        mockWebServer.start()

        val retrofit = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        appointmentApi = retrofit.create(RestApi::class.java)
        appointmentRepository.restApi = appointmentApi
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun createAppointmentBlock_success_returnsAppointmentBlock() {

        enqueueMockResponse("mocked_responses/AppointmentRepository/AppointmentBlockCreate-success.json")
        val result: AppointmentBlock = appointmentRepository.createAppointmentBlock(startDate, endDate, location, appointmentTypesList).toBlocking().first()

        assertEquals(result.endDate, appointmentBlockGet.endDate)

    }

    @Test
    fun createTimeSlot_success_returnsTimeSlot() {

        enqueueMockResponse("mocked_responses/AppointmentRepository/TimeSlotCreate-success.json")
        val result: TimeSlot = appointmentRepository.createTimeSlot(timeSlotGet).toBlocking().first()

        assertEquals(result.endDate, timeSlotGet.endDate)
    }

    @Test
    fun createTimeSlotWithQuery_success_returnsTimeSlot() {
        val startDate = "2023-07-15T10:00:00Z"
        val endDate = "2023-07-15T11:00:00Z"
        val appointmentBlockGet = appointmentBlockGet

        enqueueMockResponse("mocked_responses/AppointmentRepository/TimeSlotCreate-success.json")
        val result: TimeSlot = appointmentRepository.createTimeSlot(startDate, endDate, appointmentBlockGet).toBlocking().first()

        assertEquals(result.endDate, endDate)

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
