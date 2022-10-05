package org.openmrs.mobile.test.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import com.openmrs.android_sdk.library.api.repository.EncounterRepository
import com.openmrs.android_sdk.library.api.repository.FormRepository
import com.openmrs.android_sdk.library.dao.PatientDAO
import com.openmrs.android_sdk.library.databases.entities.FormResourceEntity
import com.openmrs.android_sdk.library.models.Answer
import com.openmrs.android_sdk.library.models.Patient
import com.openmrs.android_sdk.library.models.ResultType
import com.openmrs.android_sdk.utilities.ApplicationConstants.BundleKeys.ENCOUNTERTYPE
import com.openmrs.android_sdk.utilities.ApplicationConstants.BundleKeys.ENCOUNTER_UUID
import com.openmrs.android_sdk.utilities.ApplicationConstants.BundleKeys.FORM_NAME
import com.openmrs.android_sdk.utilities.ApplicationConstants.BundleKeys.PATIENT_ID_BUNDLE
import com.openmrs.android_sdk.utilities.InputField
import com.openmrs.android_sdk.utilities.SelectOneField
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.openmrs.mobile.activities.formdisplay.FormDisplayMainViewModel
import org.openmrs.mobile.test.ACUnitTestBaseRx
import rx.Observable

@RunWith(JUnit4::class)
class FormDisplayMainViewModelTest : ACUnitTestBaseRx() {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    lateinit var patientDAO: PatientDAO

    @Mock
    lateinit var formRepository: FormRepository

    @Mock
    lateinit var encounterRepository: EncounterRepository

    lateinit var savedStateHandle: SavedStateHandle

    lateinit var viewModel: FormDisplayMainViewModel

    private val formResource = FormResourceEntity().apply { uuid = "UUUUU" }

    @Before
    override fun setUp() {
        super.setUp()
        savedStateHandle = SavedStateHandle().apply {
            set(PATIENT_ID_BUNDLE, 88L)
            set(ENCOUNTERTYPE, "test encounter type")
            set(FORM_NAME, "test form name")
        }

        `when`(patientDAO.findPatientByID(anyString())).thenReturn(Patient())
        `when`(formRepository.fetchFormResourceByName(anyString())).thenReturn(Observable.just(formResource))

        viewModel = FormDisplayMainViewModel(patientDAO, formRepository, encounterRepository, savedStateHandle)
    }

    @Test
    fun `submitForm should create new records when not updating an existing encounter`() {
        val inputFields = listOf(
                InputField("CONCEPT 1").apply { value = 100.0 }
        )
        val radioGroupFields = listOf(
                SelectOneField(emptyList(), "CONCEPT 2").apply {
                    chosenAnswer = Answer().apply { concept = "answer concept" }
                }
        )

        `when`(encounterRepository.saveEncounter(any())).thenReturn(Observable.just(ResultType.EncounterSubmissionSuccess))
        viewModel.submitForm(inputFields, radioGroupFields).observeForever { result ->
            assertEquals(ResultType.EncounterSubmissionSuccess, result)
        }

        `when`(encounterRepository.saveEncounter(any())).thenReturn(Observable.just(ResultType.EncounterSubmissionError))
        viewModel.submitForm(inputFields, radioGroupFields).observeForever { result ->
            assertEquals(ResultType.EncounterSubmissionError, result)
        }
    }

    @Test
    fun `submitForm should create new records when updating an existing encounter`() {
        // Passing an existing encounter UUID for updating
        savedStateHandle.set(ENCOUNTER_UUID, "test encounter UUID")

        val inputFields = listOf(
                InputField("CONCEPT 1").apply { value = 100.0 }
        )
        val radioGroupFields = listOf(
                SelectOneField(emptyList(), "CONCEPT 2").apply {
                    chosenAnswer = Answer().apply { concept = "answer concept" }
                }
        )

        `when`(encounterRepository.updateEncounter(anyString(), any())).thenReturn(Observable.just(Unit))
        viewModel.submitForm(inputFields, radioGroupFields).observeForever { result ->
            assertEquals(ResultType.EncounterSubmissionSuccess, result)
        }

        `when`(encounterRepository.updateEncounter(anyString(), any())).thenReturn(Observable.error(Throwable()))
        viewModel.submitForm(inputFields, radioGroupFields).observeForever { result ->
            assertEquals(ResultType.EncounterSubmissionError, result)
        }
    }
}
