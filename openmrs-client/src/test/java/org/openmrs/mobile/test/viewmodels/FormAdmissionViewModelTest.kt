package org.openmrs.mobile.test.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import com.openmrs.android_sdk.library.api.repository.EncounterRepository
import com.openmrs.android_sdk.library.api.repository.FormRepository
import com.openmrs.android_sdk.library.api.repository.ProviderRepository
import com.openmrs.android_sdk.library.dao.PatientDAO
import com.openmrs.android_sdk.library.databases.entities.FormResourceEntity
import com.openmrs.android_sdk.library.databases.entities.LocationEntity
import com.openmrs.android_sdk.library.models.Patient
import com.openmrs.android_sdk.library.models.Resource
import com.openmrs.android_sdk.library.models.ResultType.EncounterSubmissionError
import com.openmrs.android_sdk.library.models.ResultType.EncounterSubmissionSuccess
import com.openmrs.android_sdk.utilities.ApplicationConstants.BundleKeys.ENCOUNTERTYPE
import com.openmrs.android_sdk.utilities.ApplicationConstants.BundleKeys.FORM_NAME
import com.openmrs.android_sdk.utilities.ApplicationConstants.BundleKeys.PATIENT_ID_BUNDLE
import com.openmrs.android_sdk.utilities.ApplicationConstants.LOCATION
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.anyString
import org.mockito.kotlin.any
import org.openmrs.mobile.activities.formadmission.FormAdmissionViewModel
import org.openmrs.mobile.test.ACUnitTestBaseRx
import rx.Observable
import java.util.ArrayList

@RunWith(JUnit4::class)
class FormAdmissionViewModelTest : ACUnitTestBaseRx() {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    lateinit var patientDAO: PatientDAO

    @Mock
    lateinit var formRepository: FormRepository

    @Mock
    lateinit var encounterRepository: EncounterRepository

    @Mock
    lateinit var providerRepository: ProviderRepository

    lateinit var savedStateHandle: SavedStateHandle

    lateinit var viewModel: FormAdmissionViewModel

    private val providerList = listOf(
            createProvider(1L, "doctor"),
            createProvider(2L, "nurse")
    )
    private val encounterRoleList = listOf(
            Resource("uuid", "display", ArrayList(), 1L),
            Resource("uuid 2", "display 2", ArrayList(), 2L)
    )
    private val targetLocationList = listOf(
            LocationEntity("entity 1"),
            LocationEntity("entity 2")
    )
    private val formResource = FormResourceEntity().apply { uuid = "UUUUU" }

    @Before
    override fun setUp() {
        super.setUp()
        savedStateHandle = SavedStateHandle().apply {
            set(PATIENT_ID_BUNDLE, 88L)
            set(ENCOUNTERTYPE, "test encounter type")
            set(FORM_NAME, "test form name")
            set(LOCATION, "test location")
        }

        initObservables()

        viewModel = FormAdmissionViewModel(patientDAO, formRepository, encounterRepository,
                providerRepository, savedStateHandle)
    }

    private fun initObservables() {
        `when`(patientDAO.findPatientByID(anyString())).thenReturn(Patient())
        `when`(providerRepository.getProviders()).thenReturn(Observable.just(providerList))
        `when`(providerRepository.getEncounterRoles()).thenReturn(Observable.just(encounterRoleList))
        `when`(providerRepository.getLocations(anyString())).thenReturn(Observable.just(targetLocationList))
        `when`(formRepository.fetchFormResourceByName(anyString())).thenReturn(Observable.just(formResource))
    }

    @Test
    fun selectProvider() {
        val position = 0
        val provider = providerList[position]

        viewModel.selectProvider(providerName = provider.display!!, listPosition = position)

        assertEquals(position, viewModel.providerListPosition)
        assertEquals(provider.uuid, viewModel.providerUuid)
    }

    @Test
    fun selectEncounterRole() {
        val position = 0
        val encounter = encounterRoleList[position]

        viewModel.selectEncounterRole(roleName = encounter.display!!, listPosition = position)

        assertEquals(position, viewModel.encounterRoleListPosition)
        assertEquals(encounter.uuid, viewModel.encounterRoleUuid)
    }

    @Test
    fun selectTargetLocation() {
        val position = 0
        val targetLocation = targetLocationList[position]

        viewModel.selectTargetLocation(locationName = targetLocation.display!!, listPosition = position)

        assertEquals(position, viewModel.targetLocationListPosition)
        assertEquals(targetLocation.uuid, viewModel.targetLocationUuid)
    }

    @Test
    fun submitAdmission() {
        viewModel.selectProvider(providerList[0].display!!, 0)
        viewModel.selectEncounterRole(encounterRoleList[0].display!!, 0)
        viewModel.selectTargetLocation(targetLocationList[0].display!!, 0)

        `when`(encounterRepository.saveEncounter(any())).thenReturn(Observable.just(EncounterSubmissionSuccess))
        viewModel.submitAdmission().observeForever { result ->
            assertEquals(EncounterSubmissionSuccess, result)
        }

        `when`(encounterRepository.saveEncounter(any())).thenReturn(Observable.just(EncounterSubmissionError))
        viewModel.submitAdmission().observeForever { result ->
            assertEquals(EncounterSubmissionError, result)
        }
    }
}
