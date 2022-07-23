package org.openmrs.mobile.test.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import com.openmrs.android_sdk.library.dao.EncounterDAO
import com.openmrs.android_sdk.library.dao.PatientDAO
import com.openmrs.android_sdk.library.models.Encounter
import com.openmrs.android_sdk.library.models.Patient
import com.openmrs.android_sdk.library.models.Result
import com.openmrs.android_sdk.utilities.ApplicationConstants.BundleKeys.PATIENT_ID_BUNDLE
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mock
import org.mockito.Mockito
import org.openmrs.mobile.activities.patientdashboard.vitals.PatientDashboardVitalsViewModel
import org.openmrs.mobile.test.ACUnitTestBaseRx
import rx.Observable

@RunWith(JUnit4::class)
class PatientDashboardVitalsViewModelTest : ACUnitTestBaseRx() {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    lateinit var patientDAO: PatientDAO

    @Mock
    lateinit var encounterDAO: EncounterDAO

    lateinit var savedStateHandle: SavedStateHandle

    lateinit var viewModel: PatientDashboardVitalsViewModel

    lateinit var patient: Patient

    @Before
    override fun setUp() {
        super.setUp()
        savedStateHandle = SavedStateHandle().apply { set(PATIENT_ID_BUNDLE, ID) }
        viewModel = PatientDashboardVitalsViewModel(patientDAO, encounterDAO, savedStateHandle)
        patient = createPatient(ID.toLong())
        Mockito.`when`(patientDAO.findPatientByID(ID)).thenReturn(patient)
    }

    @Test
    fun fetchLastVitals_success() {
        val encounter = Encounter()
        Mockito.`when`(encounterDAO.getLastVitalsEncounter(patient.uuid)).thenReturn(Observable.just(encounter))

        viewModel.fetchLastVitals()

        val actualResult = (viewModel.result.value as Result.Success).data

        assertEquals(encounter, actualResult)
    }

    @Test
    fun fetchLastVitals_error() {
        val errorMsg = "Error message!"
        val throwable = Throwable(errorMsg)
        Mockito.`when`(encounterDAO.getLastVitalsEncounter(patient.uuid)).thenReturn(Observable.error(throwable))

        viewModel.fetchLastVitals()

        val actualResult = (viewModel.result.value as Result.Error).throwable.message

        assertEquals(errorMsg, actualResult)
    }

    @Test
    fun fetchLastVitalsEncounter_success() {
        val encounter = Encounter()
        Mockito.`when`(encounterDAO.getLastVitalsEncounter(patient.uuid)).thenReturn(Observable.just(encounter))

        viewModel.fetchLastVitalsEncounter().observeForever { actualResult ->
            assertEquals(encounter, actualResult)
        }
    }

    companion object {
        const val ID = "1"
    }

}
