package com.openmrs.android_sdk.library.api.repository

import android.content.Context
import com.openmrs.android_sdk.library.OpenmrsAndroid
import com.openmrs.android_sdk.library.dao.*
import com.openmrs.android_sdk.library.databases.AppDatabase
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import javax.inject.Inject

@HiltAndroidTest
@RunWith(RobolectricTestRunner::class)
@Config(application = HiltTestApplication::class)
class VisitRepositoryTest {

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
        hiltRule.inject()
    }

    @Inject
    lateinit var visitRepository: VisitRepository

    @Test
    fun `testing`(){
        println(OpenmrsAndroid.getInstance().toString())
        println(visitRepository)
    }
}