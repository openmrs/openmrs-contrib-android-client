package org.openmrs.mobile.test.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.openmrs.android_sdk.library.dao.PatientDAO
import com.openmrs.android_sdk.library.dao.VisitDAO
import com.openmrs.android_sdk.library.models.Patient
import com.openmrs.android_sdk.library.models.Result
import junit.framework.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertIterableEquals
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import org.openmrs.mobile.activities.syncedpatients.SyncedPatientsViewModel
import org.openmrs.mobile.test.ACUnitTestBaseRx
import rx.Observable

@RunWith(JUnit4::class)
class SyncedPatientsViewModelTest : ACUnitTestBaseRx() {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    lateinit var patientDAO: PatientDAO

    @Mock
    lateinit var visitDAO: VisitDAO

    lateinit var patientList: List<Patient>

    lateinit var viewModel: SyncedPatientsViewModel

    @Before
    override fun setUp() {
        super.setUp()
        MockitoAnnotations.initMocks(this)

        patientList = listOf(createPatient(1L), createPatient(2L), createPatient(3L))
        viewModel = SyncedPatientsViewModel(patientDAO, visitDAO)
    }

    @Test
    fun fetchSyncedPatients_success() {
        Mockito.`when`(patientDAO.allPatients).thenReturn(Observable.just(patientList))

        viewModel.fetchSyncedPatients()

        val actualResult = (viewModel.result.value as Result.Success).data

        assertIterableEquals(patientList, actualResult)
    }

    @Test
    fun fetchSyncedPatients_error() {
        val errorMsg = "Error message!"
        val throwable = Throwable(errorMsg)
        Mockito.`when`(patientDAO.allPatients).thenReturn(Observable.error(throwable))

        viewModel.fetchSyncedPatients()

        val actualResult = (viewModel.result.value as Result.Error).throwable.message

        assertEquals(errorMsg, actualResult)
    }

    @Test
    fun fetchSyncedPatientsWithQuery_success() {
        val patient = patientList[0]
        val filteredPatients = listOf(patient)
        Mockito.`when`(patientDAO.allPatients).thenReturn(Observable.just(filteredPatients))

        viewModel.fetchSyncedPatients(patient.display!!)

        val actualResult = (viewModel.result.value as Result.Success).data

        assertIterableEquals(filteredPatients, actualResult)
    }

    @Test
    fun fetchSyncedPatientsWithQuery_noMatchingPatients() {
        val patient = patientList[0]
        val filteredPatients = listOf(patient)
        Mockito.`when`(patientDAO.allPatients).thenReturn(Observable.just(filteredPatients))

        viewModel.fetchSyncedPatients("Patient99")

        val actualResult = (viewModel.result.value as Result.Success).data

        assertIterableEquals(emptyList<Patient>(), actualResult)
    }


    @Test
    fun fetchSyncedPatientsWithQuery_error() {
        val errorMsg = "Error message!"
        val throwable = Throwable(errorMsg)
        Mockito.`when`(patientDAO.allPatients).thenReturn(Observable.error(throwable))

        viewModel.fetchSyncedPatients(patientList[0].display!!)

        val actualResult = (viewModel.result.value as Result.Error).throwable.message

        assertEquals(errorMsg, actualResult)
    }

    @Test
    fun deleteSyncedPatient_success() {
        val patientToDelete = patientList[0]
        val patientId = patientToDelete.id!!
        Mockito.`when`(visitDAO.deleteVisitsByPatientId(patientId)).thenReturn(Observable.just(true))

        viewModel.deleteSyncedPatient(patientToDelete)

        verify(patientDAO).deletePatient(patientId)
        verify(visitDAO).deleteVisitsByPatientId(patientId)
    }
}
