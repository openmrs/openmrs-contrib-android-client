package org.openmrs.mobile.test.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import com.openmrs.android_sdk.library.api.repository.ConceptRepository
import com.openmrs.android_sdk.library.api.repository.PatientRepository
import com.openmrs.android_sdk.library.dao.PatientDAO
import com.openmrs.android_sdk.library.models.ConceptAnswers
import com.openmrs.android_sdk.library.models.Patient
import com.openmrs.android_sdk.library.models.PersonAddress
import com.openmrs.android_sdk.library.models.PersonName
import com.openmrs.android_sdk.library.models.Result
import com.openmrs.android_sdk.library.models.ResultType.PatientUpdateSuccess
import com.openmrs.android_sdk.utilities.ApplicationConstants.BundleKeys.COUNTRIES_BUNDLE
import com.openmrs.android_sdk.utilities.ApplicationConstants.BundleKeys.PATIENT_ID_BUNDLE
import com.openmrs.android_sdk.utilities.PatientValidator
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertIterableEquals
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.openmrs.mobile.activities.addeditpatient.AddEditPatientViewModel
import org.openmrs.mobile.test.ACUnitTestBaseRx
import rx.Observable

@RunWith(JUnit4::class)
class AddEditPatientViewModelTest : ACUnitTestBaseRx() {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    lateinit var patientDAO: PatientDAO

    @Mock
    lateinit var patientRepository: PatientRepository

    @Mock
    lateinit var conceptRepository: ConceptRepository

    @Mock
    lateinit var savedStateHandle: SavedStateHandle

    lateinit var viewModel: AddEditPatientViewModel

    private val countries = listOf("country1", "country2", "country3")

    @Before
    override fun setUp() {
        super.setUp()
        `when`(patientDAO.findPatientByID(anyString())).thenReturn(Patient())
        savedStateHandle = SavedStateHandle().apply { set(COUNTRIES_BUNDLE, countries) }
    }

    @Test
    fun `resetPatient should clear all states and patient related data`() {
        viewModel = AddEditPatientViewModel(patientDAO, patientRepository, conceptRepository, savedStateHandle)
        updatePatientData(0L, viewModel.patient)

        viewModel.resetPatient()

        with(viewModel) {
            // ViewModel state holding
            assertFalse(isUpdatePatient)
            assertNull(capturedPhotoFile)
            assertNull(dateHolder)
        }
        with(viewModel.patient) {
            // ViewModel's patient object state
            assertNull(id)
            assertNull(gender)
            assertFalse(birthdateEstimated)
            assertNull(birthdate)
            assertNull(isDeceased)
            assertNull(causeOfDeath)
            assertIterableEquals(emptyList<PersonName>(), names)
            assertIterableEquals(emptyList<PersonAddress>(), addresses)
        }
    }

    @Test
    fun `confirmPatient should create new patient when no patient id passed`() {
        viewModel = AddEditPatientViewModel(patientDAO, patientRepository, conceptRepository, savedStateHandle)
        `when`(patientRepository.registerPatient(any<Patient>())).thenReturn(Observable.just(Patient()))
        with(viewModel) {
            patientValidator = mock(PatientValidator::class.java)
            `when`(patientValidator.validate()).thenReturn(true)

            confirmPatient()

            assert(viewModel.result.value is Result.Success)
        }
    }

    @Test
    fun `confirmPatient should update existing patient when its id is passed`() {
        savedStateHandle.apply { set(PATIENT_ID_BUNDLE, "1L") }
        viewModel = AddEditPatientViewModel(patientDAO, patientRepository, conceptRepository, savedStateHandle)
        `when`(patientRepository.updatePatient(any<Patient>())).thenReturn(Observable.just(PatientUpdateSuccess))
        with(viewModel) {
            patientValidator = mock(PatientValidator::class.java)
            `when`(patientValidator.validate()).thenReturn(true)

            confirmPatient()

            assertEquals(PatientUpdateSuccess, patientUpdateLiveData.value)
        }
    }

    @Test
    fun `confirmPatient should do nothing when patient data is invalid`() {
        viewModel = AddEditPatientViewModel(patientDAO, patientRepository, conceptRepository, savedStateHandle)
        `when`(patientRepository.registerPatient(any<Patient>())).thenReturn(Observable.just(Patient()))
        with(viewModel) {
            patientValidator = mock(PatientValidator::class.java)
            `when`(patientValidator.validate()).thenReturn(false)

            confirmPatient()

            assertNull(result.value)
        }
    }

    @Test
    fun fetchSimilarPatients() {
        viewModel = AddEditPatientViewModel(patientDAO, patientRepository, conceptRepository, savedStateHandle)
        with(viewModel) {
            val similarPatients = listOf(createPatient(1L), createPatient(2L), createPatient(3L))
            patientValidator = mock(PatientValidator::class.java)
            `when`(patientValidator.validate()).thenReturn(true)
            `when`(patientRepository.fetchSimilarPatients(any<Patient>())).thenReturn(Observable.just(similarPatients))

            fetchSimilarPatients()

            assertIterableEquals(similarPatients, similarPatientsLiveData.value)
        }
    }

    @Test
    fun fetchCausesOfDeath() {
        viewModel = AddEditPatientViewModel(patientDAO, patientRepository, conceptRepository, savedStateHandle)
        `when`(patientRepository.getCauseOfDeathGlobalConceptID()).thenReturn(Observable.just(String()))
        `when`(conceptRepository.getConceptByUuid(anyString())).thenReturn(Observable.just(ConceptAnswers()))

        viewModel.fetchCausesOfDeath().observeForever {
            assert(it is ConceptAnswers)
        }
    }
}
