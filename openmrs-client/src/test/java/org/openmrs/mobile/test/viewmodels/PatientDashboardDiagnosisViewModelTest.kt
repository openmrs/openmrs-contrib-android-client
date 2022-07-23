package org.openmrs.mobile.test.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import com.openmrs.android_sdk.library.dao.EncounterDAO
import com.openmrs.android_sdk.library.models.Encounter
import com.openmrs.android_sdk.library.models.Observation
import com.openmrs.android_sdk.library.models.Result
import com.openmrs.android_sdk.utilities.ApplicationConstants.BundleKeys.PATIENT_ID_BUNDLE
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertIterableEquals
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mock
import org.mockito.Mockito
import org.openmrs.mobile.activities.patientdashboard.diagnosis.PatientDashboardDiagnosisViewModel
import org.openmrs.mobile.test.ACUnitTestBaseRx
import rx.Observable
import java.util.ArrayList

@RunWith(JUnit4::class)
class PatientDashboardDiagnosisViewModelTest : ACUnitTestBaseRx() {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    lateinit var encounterDAO: EncounterDAO

    lateinit var savedStateHandle: SavedStateHandle

    lateinit var viewModel: PatientDashboardDiagnosisViewModel

    lateinit var diagnosisList: List<String>

    lateinit var observations: MutableList<Observation>

    @Before
    override fun setUp() {
        super.setUp()
        savedStateHandle = SavedStateHandle().apply { set(PATIENT_ID_BUNDLE, PATIENT_ID) }
        viewModel = PatientDashboardDiagnosisViewModel(encounterDAO, savedStateHandle)
        diagnosisList = createDiagnosisList(2)
        observations = createObservations(diagnosisList)
    }

    @Test
    fun fetchDiagnoses_success() {
        val encounters = createEncounters(observations, false)
        Mockito.`when`(encounterDAO.getAllEncountersByType(eq(PATIENT_ID.toLong()), any()))
                .thenReturn(Observable.just(encounters))

        viewModel.fetchDiagnoses()

        val actualResult = (viewModel.result.value as Result.Success).data
        assertIterableEquals(diagnosisList, actualResult)
    }

    @Test
    fun fetchDiagnoses_success_shouldNotShowDuplicates() {
        val encounters = createEncounters(observations, true)
        Mockito.`when`(encounterDAO.getAllEncountersByType(eq(PATIENT_ID.toLong()), any()))
                .thenReturn(Observable.just(encounters))

        viewModel.fetchDiagnoses()

        val actualResult = (viewModel.result.value as Result.Success).data
        assertIterableEquals(diagnosisList, actualResult)
    }


    private fun createEncounters(observations: MutableList<Observation>, withDuplicates: Boolean): List<Encounter> {
        if (withDuplicates) observations.addAll(observations)
        val encounter = Encounter()
        encounter.observations = observations
        return listOf(encounter)
    }

    private fun createObservations(diagnosisList: List<String>): MutableList<Observation> {
        val observations = ArrayList<Observation>()
        for (diag in diagnosisList) {
            val observation = Observation()
            observation.diagnosisList = diag
            observations.add(observation)
        }
        return observations
    }

    private fun createDiagnosisList(diagnosisCount: Int): List<String> {
        val diagnosisList: MutableList<String> = ArrayList()
        for (i in 0 until diagnosisCount) {
            diagnosisList.add("diag$i")
        }
        return diagnosisList
    }

    companion object {
        const val PATIENT_ID = "1"
    }
}
