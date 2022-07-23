package org.openmrs.mobile.test.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import com.openmrs.android_sdk.library.dao.PatientDAO
import com.openmrs.android_sdk.library.models.Patient
import com.openmrs.android_sdk.library.models.Result
import com.openmrs.android_sdk.utilities.ApplicationConstants.BundleKeys.PATIENT_ID_BUNDLE
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mock
import org.mockito.Mockito
import org.openmrs.mobile.activities.patientdashboard.details.PatientDashboardDetailsViewModel
import org.openmrs.mobile.test.ACUnitTestBaseRx

@RunWith(JUnit4::class)
class PatientDashboardDetailsViewModelTest : ACUnitTestBaseRx() {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    lateinit var patientDAO: PatientDAO

    lateinit var savedStateHandle: SavedStateHandle

    lateinit var viewModel: PatientDashboardDetailsViewModel

    lateinit var patient: Patient

    @Before
    override fun setUp() {
        super.setUp()
        savedStateHandle = SavedStateHandle().apply { set(PATIENT_ID_BUNDLE, ID) }
        viewModel = PatientDashboardDetailsViewModel(patientDAO, savedStateHandle)
        patient = createPatient(ID.toLong())
    }

    @Test
    fun fetchPatientData_success() {
        Mockito.`when`(patientDAO.findPatientByID(ID)).thenReturn(patient)

        viewModel.fetchPatientData()

        assert(viewModel.result.value is Result.Success)
    }

    @Test
    fun fetchPatientData_error() {
        Mockito.`when`(patientDAO.findPatientByID(ID)).thenReturn(null)

        viewModel.fetchPatientData()

        assert(viewModel.result.value is Result.Error)
    }

    companion object {
        const val ID = "1"
    }
}
