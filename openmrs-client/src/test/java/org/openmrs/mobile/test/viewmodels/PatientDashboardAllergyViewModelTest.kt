package org.openmrs.mobile.test.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import com.openmrs.android_sdk.library.api.repository.AllergyRepository
import com.openmrs.android_sdk.library.dao.PatientDAO
import com.openmrs.android_sdk.library.models.Allergy
import com.openmrs.android_sdk.library.models.Patient
import com.openmrs.android_sdk.library.models.Result
import com.openmrs.android_sdk.library.models.ResultType.AllergyDeletionError
import com.openmrs.android_sdk.library.models.ResultType.AllergyDeletionLocalSuccess
import com.openmrs.android_sdk.library.models.ResultType.AllergyDeletionSuccess
import com.openmrs.android_sdk.utilities.ApplicationConstants.BundleKeys.PATIENT_ID_BUNDLE
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mock
import org.mockito.Mockito
import org.openmrs.mobile.activities.patientdashboard.allergy.PatientDashboardAllergyViewModel
import org.openmrs.mobile.test.ACUnitTestBaseRx
import rx.Observable

@RunWith(JUnit4::class)
class PatientDashboardAllergyViewModelTest : ACUnitTestBaseRx() {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    lateinit var patientDAO: PatientDAO

    @Mock
    lateinit var allergyRepository: AllergyRepository

    lateinit var savedStateHandle: SavedStateHandle

    lateinit var viewModel: PatientDashboardAllergyViewModel

    lateinit var patient: Patient

    lateinit var allergies: List<Allergy>

    @Before
    override fun setUp() {
        super.setUp()
        savedStateHandle = SavedStateHandle().apply { set(PATIENT_ID_BUNDLE, PATIENT_ID) }
        viewModel = PatientDashboardAllergyViewModel(patientDAO, allergyRepository, savedStateHandle)
        patient = createPatient(PATIENT_ID.toLong())
        allergies = listOf(createAllergy(1L, "doctor"), createAllergy(2L, "doctor"))
    }

    @Test
    fun fetchAllergies_success() {
        Mockito.`when`(allergyRepository.getAllergyFromDatabase(PATIENT_ID)).thenReturn(Observable.just(allergies))

        viewModel.fetchAllergies()

        assert(viewModel.result.value is Result.Success)
    }

    @Test
    fun fetchAllergies_error() {
        val errorMsg = "Error message!"
        val throwable = Throwable(errorMsg)
        Mockito.`when`(allergyRepository.getAllergyFromDatabase(PATIENT_ID)).thenReturn(Observable.error(throwable))

        viewModel.fetchAllergies()

        val actualResult = (viewModel.result.value as Result.Error).throwable.message

        assertEquals(errorMsg, actualResult)
    }

    @Test
    fun deleteAllergy_success() {
        val allergy = allergies[0]
        Mockito.`when`(patientDAO.findPatientByID(PATIENT_ID)).thenReturn(patient)
        Mockito.`when`(allergyRepository.deleteAllergy(patient.uuid, allergy.uuid))
                .thenReturn(Observable.just(AllergyDeletionSuccess))

        viewModel.deleteAllergy(allergy.uuid!!).observeForever { actualResultType ->
            assertEquals(AllergyDeletionSuccess, actualResultType)
        }
    }

    @Test
    fun deleteAllergy_LocalSuccess() {
        val allergy = allergies[0]
        Mockito.`when`(patientDAO.findPatientByID(PATIENT_ID)).thenReturn(patient)
        Mockito.`when`(allergyRepository.deleteAllergy(patient.uuid, allergy.uuid))
                .thenReturn(Observable.just(AllergyDeletionLocalSuccess))

        viewModel.deleteAllergy(allergy.uuid!!).observeForever { actualResultType ->
            assertEquals(AllergyDeletionLocalSuccess, actualResultType)
        }
    }

    @Test
    fun deleteAllergy_error() {
        val allergy = allergies[0]
        Mockito.`when`(patientDAO.findPatientByID(PATIENT_ID)).thenReturn(patient)
        Mockito.`when`(allergyRepository.deleteAllergy(patient.uuid, allergy.uuid))
                .thenReturn(Observable.just(AllergyDeletionError))

        viewModel.deleteAllergy(allergy.uuid!!).observeForever { actualResultType ->
            assertEquals(AllergyDeletionError, actualResultType)
        }
    }

    companion object {
        const val PATIENT_ID = "1"
    }
}
