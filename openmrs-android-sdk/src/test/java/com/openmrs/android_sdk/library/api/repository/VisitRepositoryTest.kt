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
        mockWebServer.start(8080)

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