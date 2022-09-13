package org.openmrs.mobile.test.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import com.openmrs.android_sdk.library.api.repository.AllergyRepository
import com.openmrs.android_sdk.library.api.repository.ConceptRepository
import com.openmrs.android_sdk.library.dao.PatientDAO
import com.openmrs.android_sdk.library.models.Allergy
import com.openmrs.android_sdk.library.models.ConceptMembers
import com.openmrs.android_sdk.library.models.Patient
import com.openmrs.android_sdk.library.models.Resource
import com.openmrs.android_sdk.library.models.SystemProperty
import com.openmrs.android_sdk.utilities.ApplicationConstants.AllergyModule.CONCEPT_ALLERGEN_DRUG
import com.openmrs.android_sdk.utilities.ApplicationConstants.AllergyModule.CONCEPT_ALLERGEN_ENVIRONMENT
import com.openmrs.android_sdk.utilities.ApplicationConstants.AllergyModule.CONCEPT_ALLERGEN_FOOD
import com.openmrs.android_sdk.utilities.ApplicationConstants.AllergyModule.CONCEPT_REACTION
import com.openmrs.android_sdk.utilities.ApplicationConstants.AllergyModule.CONCEPT_SEVERITY_MILD
import com.openmrs.android_sdk.utilities.ApplicationConstants.AllergyModule.CONCEPT_SEVERITY_MODERATE
import com.openmrs.android_sdk.utilities.ApplicationConstants.AllergyModule.CONCEPT_SEVERITY_SEVERE
import com.openmrs.android_sdk.utilities.ApplicationConstants.AllergyModule.PROPERTY_FOOD
import com.openmrs.android_sdk.utilities.ApplicationConstants.AllergyModule.SELECT_ALLERGEN
import com.openmrs.android_sdk.utilities.ApplicationConstants.AllergyModule.SELECT_REACTION
import com.openmrs.android_sdk.utilities.ApplicationConstants.BundleKeys.ALLERGY_UUID
import com.openmrs.android_sdk.utilities.ApplicationConstants.BundleKeys.PATIENT_ID_BUNDLE
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.openmrs.mobile.R
import org.openmrs.mobile.activities.addeditallergy.AddEditAllergyViewModel
import org.openmrs.mobile.test.ACUnitTestBaseRx
import rx.Observable

@RunWith(JUnit4::class)
class AddEditAllergyViewModelTest : ACUnitTestBaseRx() {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    lateinit var patientDAO: PatientDAO

    @Mock
    lateinit var conceptRepository: ConceptRepository

    @Mock
    lateinit var allergyRepository: AllergyRepository

    lateinit var savedStateHandle: SavedStateHandle

    lateinit var viewModel: AddEditAllergyViewModel

    private val drugProperty = SystemProperty().apply { conceptUUID = "DRUG UUID" }
    private val foodProperty = SystemProperty().apply { conceptUUID = "FOOD UUID" }
    private val environmentProperty = SystemProperty().apply { conceptUUID = "ENVIRONMENT UUID" }
    private val reactionProperty = SystemProperty().apply { conceptUUID = "REACTION UUID" }
    private val mildSeverityProperty = SystemProperty().apply { conceptUUID = "MILD SEVERITY UUID" }
    private val moderateSeverityProperty = SystemProperty().apply { conceptUUID = "MODERATE SEVERITY UUID" }
    private val severeSeverityProperty = SystemProperty().apply { conceptUUID = "SEVERE SEVERITY UUID" }

    private val drugConceptMembers = ConceptMembers().apply {
        members = listOf(
                Resource("AspirinUUID", "Aspirin", emptyList(), 1),
                Resource("MorphineUUID", "Morphine", emptyList(), 2)
        )
    }
    private val foodConceptMembers = ConceptMembers().apply {
        members = listOf(
                Resource("EggsUUID", "Eggs", emptyList(), 3),
                Resource("FishUUID", "Fish", emptyList(), 4)
        )
    }
    private val environmentConceptMembers = ConceptMembers().apply {
        members = listOf(
                Resource("DustUUID", "Dust", emptyList(), 5),
                Resource("LatexUUID", "Latex", emptyList(), 6)
        )
    }
    private val reactionConceptMembers = ConceptMembers().apply {
        members = listOf(
                Resource("HeadacheUUID", "Headache", emptyList(), 7),
                Resource("CoughUUID", "Cough", emptyList(), 8)
        )
    }
    private val mildSeverity = ConceptMembers().apply { uuid = "mildUUID" }
    private val moderateSeverity = ConceptMembers().apply { uuid = "moderateUUID" }
    private val severeSeverity = ConceptMembers().apply { uuid = "severeUUID" }

    @Before
    override fun setUp() {
        super.setUp()
        savedStateHandle = SavedStateHandle().apply { set(PATIENT_ID_BUNDLE, "10") }
        `when`(patientDAO.findPatientByID(anyString())).thenReturn(Patient())
        `when`(allergyRepository.getAllergyByUUID(anyString())).thenReturn(Observable.just(Allergy()))

        initConcepts()

        viewModel = AddEditAllergyViewModel(patientDAO, conceptRepository, allergyRepository, savedStateHandle)
    }

    private fun initConcepts() = with(conceptRepository) {
        `when`(getSystemProperty(CONCEPT_ALLERGEN_DRUG)).thenReturn(Observable.just(drugProperty))
        `when`(getSystemProperty(CONCEPT_ALLERGEN_FOOD)).thenReturn(Observable.just(foodProperty))
        `when`(getSystemProperty(CONCEPT_ALLERGEN_ENVIRONMENT)).thenReturn(Observable.just(environmentProperty))
        `when`(getSystemProperty(CONCEPT_REACTION)).thenReturn(Observable.just(reactionProperty))
        `when`(getSystemProperty(CONCEPT_SEVERITY_MILD)).thenReturn(Observable.just(mildSeverityProperty))
        `when`(getSystemProperty(CONCEPT_SEVERITY_MODERATE)).thenReturn(Observable.just(moderateSeverityProperty))
        `when`(getSystemProperty(CONCEPT_SEVERITY_SEVERE)).thenReturn(Observable.just(severeSeverityProperty))

        `when`(getConceptMembers(drugProperty.conceptUUID!!)).thenReturn(Observable.just(drugConceptMembers))
        `when`(getConceptMembers(foodProperty.conceptUUID!!)).thenReturn(Observable.just(foodConceptMembers))
        `when`(getConceptMembers(environmentProperty.conceptUUID!!)).thenReturn(Observable.just(environmentConceptMembers))
        `when`(getConceptMembers(reactionProperty.conceptUUID!!)).thenReturn(Observable.just(reactionConceptMembers))
        `when`(getConceptMembers(mildSeverityProperty.conceptUUID!!)).thenReturn(Observable.just(mildSeverity))
        `when`(getConceptMembers(moderateSeverityProperty.conceptUUID!!)).thenReturn(Observable.just(moderateSeverity))
        `when`(getConceptMembers(severeSeverityProperty.conceptUUID!!)).thenReturn(Observable.just(severeSeverity))
    }

    @Test
    fun selectAllergenTypeChip() {
        val chipId = R.id.allergen_food

        viewModel.selectAllergenTypeChip(chipId)

        assertEquals(chipId, viewModel.allergenTypeChipId)
    }

    @Test
    fun selectAllergenSeverityChip() {
        val chipId = R.id.moderate_severity

        viewModel.selectAllergenSeverityChip(chipId)

        assertEquals(chipId, viewModel.allergenSeverityChipId)
    }

    @Test
    fun `selectAllergen from allergens list`() {
        val position = 1
        val allergen = foodConceptMembers.members[position].display!!
        val allergenUuid = foodConceptMembers.members[position].uuid!!

        viewModel.selectAllergen(allergenName = allergen, listPosition = position, allergenType = PROPERTY_FOOD)

        assertEquals(position, viewModel.allergenListPosition)
        assertEquals(PROPERTY_FOOD, viewModel.allergyCreate.allergen!!.allergenType)
        assertEquals(allergenUuid, viewModel.allergyCreate.allergen!!.codedAllergen!!.uuid)
    }

    @Test
    fun `selectAllergen function when list header (Select Allergen) item is selected`() {
        val position = 0
        val headerItem = SELECT_ALLERGEN

        viewModel.selectAllergen(allergenName = headerItem, listPosition = position, allergenType = "INVALID TYPE")

        assertEquals(position, viewModel.allergenListPosition)
        assertNull(viewModel.allergyCreate.allergen)
    }

    @Test
    fun addReaction() {
        val reactionName = reactionConceptMembers.members[0].display!!
        val reactionUuid = reactionConceptMembers.members[0].uuid!!

        viewModel.addReaction(reactionName)

        assertEquals(reactionUuid, viewModel.selectedReactions[reactionName]!!.reaction!!.uuid)
    }

    @Test
    fun `addReaction function when list header (Select Reaction) item is selected`() {
        val headerItem = SELECT_REACTION

        viewModel.addReaction(headerItem)

        assertNull(viewModel.selectedReactions[headerItem])
    }

    @Test
    fun removeReaction() {
        val reactionName = reactionConceptMembers.members[0].display!!

        viewModel.addReaction(reactionName)
        assertNotNull(viewModel.selectedReactions[reactionName])

        viewModel.removeReaction(reactionName)
        assertNull(viewModel.selectedReactions[reactionName])
    }

    @Test
    fun `submitAllergy should create new allergy when no existing allergy UUID is passed`() {
        `when`(allergyRepository.createAllergy(any(), any())).thenReturn(Observable.just(true))

        viewModel.submitAllergy().observeForever {
            assertTrue(it)
        }
    }

    @Test
    fun `submitAllergy should update allergy when its UUID is passed to the ViewModel`() {
        savedStateHandle.set(ALLERGY_UUID, "UUID12345")
        viewModel = AddEditAllergyViewModel(patientDAO, conceptRepository, allergyRepository, savedStateHandle)
        `when`(allergyRepository.updateAllergy(any(), any(), any(), any())).thenReturn(Observable.just(true))

        viewModel.submitAllergy().observeForever {
            assertTrue(it)
        }
    }
}
