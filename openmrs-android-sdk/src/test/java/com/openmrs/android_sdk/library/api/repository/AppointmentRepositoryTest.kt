package com.openmrs.android_sdk.library.api.repository

import com.openmrs.android_sdk.library.OpenmrsAndroid
import com.openmrs.android_sdk.library.api.RestApi
import com.openmrs.android_sdk.library.databases.AppDatabase
import com.openmrs.android_sdk.library.databases.AppDatabaseHelper
import com.openmrs.android_sdk.library.databases.entities.LocationEntity
import com.openmrs.android_sdk.library.models.AppointmentBlock
import com.openmrs.android_sdk.library.models.AppointmentType
import com.openmrs.android_sdk.library.models.Provider
import com.openmrs.android_sdk.library.models.VisitType
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import io.mockk.every
import io.mockk.mockk
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
import rx.schedulers.Schedulers
import java.util.concurrent.Callable
import javax.inject.Inject

@HiltAndroidTest
@RunWith(RobolectricTestRunner::class)
@Config(application = HiltTestApplication::class)
class AppointmentRepositoryTest {

    fun createObservableIOForTest(func: Callable<AppointmentBlock>): Observable<AppointmentBlock> {
        return Observable.fromCallable(func)
            .subscribeOn(Schedulers.io())
    }

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
        //every { AppDatabase.getDatabase(any()) } returns mockk(relaxed = true)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun createAppointmentBlock_success_returnsAppointmentBlock() {

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

        enqueueMockResponse("mocked_responses/AppointmentRepository/AppointmentCreateBlock-success.json")
        val result: AppointmentBlock = appointmentRepository.createAppointmentBlock(startDate, endDate, location, appointmentTypesList).toBlocking().first()

        assertEquals(result.endDate, appointmentBlockGet.endDate)

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