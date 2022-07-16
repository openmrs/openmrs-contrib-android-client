package org.openmrs.mobile.test.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.openmrs.android_sdk.library.api.repository.PatientRepository
import com.openmrs.android_sdk.library.dao.PatientDAO
import com.openmrs.android_sdk.library.models.Patient
import com.openmrs.android_sdk.library.models.Result
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import org.openmrs.mobile.activities.matchingpatients.MatchingPatientsViewModel
import org.openmrs.mobile.test.ACUnitTestBaseRx
import rx.Observable

@RunWith(JUnit4::class)
class MatchingPatientsViewModelTest : ACUnitTestBaseRx() {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    lateinit var patientDAO: PatientDAO

    @Mock
    lateinit var patientRepository: PatientRepository

    lateinit var patient: Patient

    lateinit var viewModel: MatchingPatientsViewModel

    @Before
    override fun setUp() {
        super.setUp()
        MockitoAnnotations.initMocks(this)
        viewModel = MatchingPatientsViewModel(patientDAO, patientRepository)
        patient = createPatient(1L)
    }

    @Test
    fun registerNewPatient() {
        Mockito.`when`(patientRepository.syncPatient(patient)).thenReturn(Observable.just(patient))

        viewModel.registerNewPatient(patient)

        verify(patientRepository).syncPatient(patient)
        assert(viewModel.result.value is Result.Success)
    }

    @Test
    fun mergePatients() {
        val similarPatient = createPatient(2L)
        Mockito.`when`(patientRepository.updateMatchingPatient(patient)).thenReturn(Observable.just(patient))

        viewModel.mergePatients(patient, similarPatient)

        verify(patientRepository).updateMatchingPatient(patient)
        assert(viewModel.result.value is Result.Success)
    }
}
