package org.openmrs.mobile.test.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.openmrs.android_sdk.library.dao.VisitDAO
import com.openmrs.android_sdk.library.models.Patient
import com.openmrs.android_sdk.library.models.Result
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertIterableEquals
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.openmrs.mobile.activities.formentrypatientlist.FormEntryPatientListViewModel
import org.openmrs.mobile.test.ACUnitTestBaseRx
import rx.Observable

@RunWith(JUnit4::class)
class FormEntryPatientListViewModelTest : ACUnitTestBaseRx() {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    lateinit var visitDAO: VisitDAO

    lateinit var viewModel: FormEntryPatientListViewModel

    @Before
    override fun setUp() {
        super.setUp()
        viewModel = FormEntryPatientListViewModel(visitDAO)
    }

    @Test
    fun `fetch all saved patients with active visits should succeed`() {
        val visits = createVisitList()
        val patients = mutableListOf<Patient>().apply { visits.forEach { this += it.patient } }
        `when`(visitDAO.getActiveVisits()).thenReturn(Observable.just(visits))

        viewModel.fetchSavedPatientsWithActiveVisits()
        val result = viewModel.result.value as Result.Success<List<Patient>>

        assertIterableEquals(patients, result.data)
    }

    @Test
    fun `fetch all saved patients with active visits should return error`() {
        val errorMsg = "Error message!"
        val throwable = Throwable(errorMsg)
        `when`(visitDAO.getActiveVisits()).thenReturn(Observable.error(throwable))

        viewModel.fetchSavedPatientsWithActiveVisits()
        val result = (viewModel.result.value as Result.Error).throwable.message

        assertEquals(errorMsg, result)
    }

    @Test
    fun `fetch all saved patients with active visits by query name should succeed`() {
        val visits = createVisitList()
        val patients = mutableListOf<Patient>().apply { visits.forEach { this += it.patient } }
        patients[0].name.givenName = "Alex"
        val filteredPatients = listOf(patients[0])

        `when`(visitDAO.getActiveVisits()).thenReturn(Observable.just(visits))

        viewModel.fetchSavedPatientsWithActiveVisits("Alex")

        val result = viewModel.result.value as Result.Success<List<Patient>>
        assertIterableEquals(filteredPatients, result.data)
    }
}
