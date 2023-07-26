package com.openmrs.android_sdk.library.api.repository

import android.content.Context
import com.google.gson.GsonBuilder
import com.openmrs.android_sdk.library.OpenmrsAndroid
import com.openmrs.android_sdk.library.api.RestApi
import com.openmrs.android_sdk.library.dao.*
import com.openmrs.android_sdk.library.databases.AppDatabase
import com.openmrs.android_sdk.library.databases.AppDatabaseHelper
import com.openmrs.android_sdk.library.databases.entities.ObservationEntity
import com.openmrs.android_sdk.library.models.Observation
import com.openmrs.android_sdk.utilities.NetworkUtils
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
class ObservationRepositoryTest {
    lateinit var mockWebServer: MockWebServer
    lateinit var observationApi: RestApi

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    val context: Context = mockk(relaxed = true)
    val appContext: Context = mockk(relaxed = true)
    val appDatabase: AppDatabase = mockk()
    val observationRoomDAO: ObservationRoomDAO = mockk()

    @Before
    fun init() {
        every { context.getApplicationContext() } returns appContext
        mockkStatic(OpenmrsAndroid::class)
        every { OpenmrsAndroid.getInstance() } returns context
        mockkStatic(AppDatabase::class)
        every { AppDatabase.getDatabase(appContext) } returns appDatabase
        every { appDatabase.observationRoomDAO() } returns observationRoomDAO

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

        observationApi = retrofit.create(RestApi::class.java)

        hiltRule.inject()
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Inject
    lateinit var observationRepository: ObservationRepository

    @Test
    fun `createObservationFromLocal success return Observation`(){
        enqueueMockResponse("mocked_responses/ObservationRepository/ObservationPost-success.json")

        observationRepository.restApi = observationApi

        val observationEntity: ObservationEntity = mockk(relaxed = true)
        val observationToPost = Observation()

        mockkStatic(AppDatabaseHelper::class)
        every { AppDatabaseHelper.convert(observationEntity) } returns observationToPost

        val observation = observationRepository.createObservationFromLocal(observationEntity).toBlocking().first()

        assertEquals(observation.uuid, "12345")
        assertEquals(observation.display, "obsDisplay")
        assertEquals(observation.value, "120/80")
        assertEquals(observation.obsDatetime, "2023-07-27 12:34:56")
        assertEquals(observation.accessionNumber, 987)
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