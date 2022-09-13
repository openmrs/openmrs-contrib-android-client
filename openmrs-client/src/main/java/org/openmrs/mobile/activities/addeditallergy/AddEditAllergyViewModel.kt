package org.openmrs.mobile.activities.addeditallergy

import androidx.annotation.IdRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import com.openmrs.android_sdk.library.api.repository.AllergyRepository
import com.openmrs.android_sdk.library.api.repository.ConceptRepository
import com.openmrs.android_sdk.library.dao.PatientDAO
import com.openmrs.android_sdk.library.models.AllergenCreate
import com.openmrs.android_sdk.library.models.Allergy
import com.openmrs.android_sdk.library.models.AllergyCreate
import com.openmrs.android_sdk.library.models.AllergyPatient
import com.openmrs.android_sdk.library.models.AllergyReactionCreate
import com.openmrs.android_sdk.library.models.AllergyUuid
import com.openmrs.android_sdk.library.models.Patient
import com.openmrs.android_sdk.library.models.Resource
import com.openmrs.android_sdk.utilities.ApplicationConstants.AllergyModule.CONCEPT_ALLERGEN_DRUG
import com.openmrs.android_sdk.utilities.ApplicationConstants.AllergyModule.CONCEPT_ALLERGEN_ENVIRONMENT
import com.openmrs.android_sdk.utilities.ApplicationConstants.AllergyModule.CONCEPT_ALLERGEN_FOOD
import com.openmrs.android_sdk.utilities.ApplicationConstants.AllergyModule.CONCEPT_REACTION
import com.openmrs.android_sdk.utilities.ApplicationConstants.AllergyModule.CONCEPT_SEVERITY_MILD
import com.openmrs.android_sdk.utilities.ApplicationConstants.AllergyModule.CONCEPT_SEVERITY_MODERATE
import com.openmrs.android_sdk.utilities.ApplicationConstants.AllergyModule.CONCEPT_SEVERITY_SEVERE
import com.openmrs.android_sdk.utilities.ApplicationConstants.AllergyModule.PROPERTY_DRUG
import com.openmrs.android_sdk.utilities.ApplicationConstants.AllergyModule.PROPERTY_FOOD
import com.openmrs.android_sdk.utilities.ApplicationConstants.AllergyModule.PROPERTY_MILD
import com.openmrs.android_sdk.utilities.ApplicationConstants.AllergyModule.PROPERTY_MODERATE
import com.openmrs.android_sdk.utilities.ApplicationConstants.AllergyModule.PROPERTY_OTHER
import com.openmrs.android_sdk.utilities.ApplicationConstants.AllergyModule.PROPERTY_SEVERE
import com.openmrs.android_sdk.utilities.ApplicationConstants.AllergyModule.SELECT_ALLERGEN
import com.openmrs.android_sdk.utilities.ApplicationConstants.AllergyModule.SELECT_REACTION
import com.openmrs.android_sdk.utilities.ApplicationConstants.BundleKeys.ALLERGY_UUID
import com.openmrs.android_sdk.utilities.ApplicationConstants.BundleKeys.PATIENT_ID_BUNDLE
import com.openmrs.android_sdk.utilities.mapAllergies
import dagger.hilt.android.lifecycle.HiltViewModel
import org.openmrs.mobile.R
import org.openmrs.mobile.activities.BaseViewModel
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import javax.inject.Inject

@HiltViewModel
class AddEditAllergyViewModel @Inject constructor(
        private val patientDAO: PatientDAO,
        private val conceptRepository: ConceptRepository,
        private val allergyRepository: AllergyRepository,
        private val savedStateHandle: SavedStateHandle
) : BaseViewModel<Unit>() {

    /* UI */
    var allergenTypeChipId: Int = R.id.allergen_drug
    var allergenListPosition: Int = 0
    var allergenSeverityChipId: Int? = null
    val selectedReactions: LinkedHashMap<String, AllergyReactionCreate> = linkedMapOf()
    var comment: String? = null

    private val patientId: String = savedStateHandle.get(PATIENT_ID_BUNDLE)!!
    private val allergyUuid: String? = savedStateHandle.get(ALLERGY_UUID)
    var isUpdateAllergy = allergyUuid != null
        private set

    val patient: Patient = patientDAO.findPatientByID(patientId)
    var allergyToUpdate: Allergy? = null
        private set

    val allergyCreate = AllergyCreate()

    /* Allergy properties (concepts) */
    var drugAllergens: LinkedHashMap<String, Resource> = linkedMapOf()
        private set
    var foodAllergens: LinkedHashMap<String, Resource> = linkedMapOf()
        private set
    var environmentAllergens: LinkedHashMap<String, Resource> = linkedMapOf()
        private set
    var reactionList: LinkedHashMap<String, Resource> = linkedMapOf()
        private set
    var mildSeverity: String = ""
        private set
    var moderateSeverity: String = ""
        private set
    var severeSeverity: String = ""
        private set

    private val allergyConceptsObservables: List<Observable<Unit>>
        get() = with(conceptRepository) {
            listOf(
                    getSystemProperty(CONCEPT_ALLERGEN_DRUG)
                            .flatMap {
                                getConceptMembers(it.conceptUUID!!)
                            }
                            .map { drugAllergens = it.members.mapAllergies(SELECT_ALLERGEN) },
                    getSystemProperty(CONCEPT_ALLERGEN_FOOD)
                            .flatMap { getConceptMembers(it.conceptUUID!!) }
                            .map { foodAllergens = it.members.mapAllergies(SELECT_ALLERGEN) },
                    getSystemProperty(CONCEPT_ALLERGEN_ENVIRONMENT)
                            .flatMap { getConceptMembers(it.conceptUUID!!) }
                            .map { environmentAllergens = it.members.mapAllergies(SELECT_ALLERGEN) },
                    getSystemProperty(CONCEPT_REACTION)
                            .flatMap { getConceptMembers(it.conceptUUID!!) }
                            .map { reactionList = it.members.mapAllergies(SELECT_REACTION) },
                    getSystemProperty(CONCEPT_SEVERITY_MILD).map { mildSeverity = it.conceptUUID!! },
                    getSystemProperty(CONCEPT_SEVERITY_MODERATE).map { moderateSeverity = it.conceptUUID!! },
                    getSystemProperty(CONCEPT_SEVERITY_SEVERE).map { severeSeverity = it.conceptUUID!! }
            )
        }

    private val areAllConceptsNotEmpty
        get() = drugAllergens.isNotEmpty() && foodAllergens.isNotEmpty() && environmentAllergens.isNotEmpty()
                && reactionList.isNotEmpty()
                && mildSeverity.isNotEmpty() && moderateSeverity.isNotEmpty() && severeSeverity.isNotEmpty()


    init {
        setLoading()
        fetchAllergyConcepts()
    }

    private fun fetchAllergyConcepts() {
        addSubscription(Observable
                .merge(allergyConceptsObservables)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnCompleted {
                    if (areAllConceptsNotEmpty) fetchOldAllergyIfPresentThenSuccess()
                    else setError(Throwable("Some concept lists are empty"))
                }
                .subscribe({}, {
                    clearSubscriptions()
                    setError(it)
                })
        )
    }

    private fun fetchOldAllergyIfPresentThenSuccess() {
        if (!isUpdateAllergy) {
            setContent(Unit)
            return
        }
        addSubscription(allergyRepository.getAllergyByUUID(allergyUuid)
                .map { allergy ->
                    allergyCreate.apply {
                        allergen = AllergenCreate().apply {
                            allergenType = allergy.allergen!!.allergenType
                            codedAllergen = AllergyUuid(allergy.allergen!!.codedAllergen!!.uuid)
                        }
                        severity = AllergyUuid(allergy.severity!!.uuid)
                        comment = allergy.comment
                    }

                    allergenSeverityChipId = when (allergy.severity!!.display) {
                        PROPERTY_MILD -> R.id.mild_severity
                        PROPERTY_MODERATE -> R.id.moderate_severity
                        PROPERTY_SEVERE -> R.id.severe_severity
                        else -> null
                    }

                    allergy?.reactions?.forEach { addReaction(it.reaction!!.display!!) }

                    comment = allergy.comment

                    allergyToUpdate = allergy
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ setContent(Unit) }, { setError(it) })
        )
    }

    fun selectAllergenTypeChip(@IdRes chipViewId: Int) = run { allergenTypeChipId = chipViewId }

    fun selectAllergenSeverityChip(@IdRes chipViewId: Int) = run { allergenSeverityChipId = chipViewId }

    fun selectAllergen(allergenName: String, listPosition: Int, allergenType: String) {
        allergenListPosition = listPosition
        if (allergenName == SELECT_ALLERGEN) {
            allergyCreate.allergen = null;
            return
        }
        val uuid = when (allergenType) {
            PROPERTY_DRUG -> drugAllergens[allergenName]!!.uuid
            PROPERTY_FOOD -> foodAllergens[allergenName]!!.uuid
            PROPERTY_OTHER -> environmentAllergens[allergenName]!!.uuid
            else -> throw Exception()
        }
        allergyCreate.allergen = AllergenCreate().apply {
            this.allergenType = allergenType
            this.codedAllergen = AllergyUuid(uuid)
        }
    }

    fun addReaction(reactionName: String) {
        if (reactionName == SELECT_REACTION) return
        val reactionUuid = reactionList[reactionName]!!.uuid
        selectedReactions[reactionName] = AllergyReactionCreate().apply { reaction = AllergyUuid(reactionUuid) }
    }

    fun removeReaction(reactionName: String) = run { selectedReactions.remove(reactionName) }

    fun submitAllergy(): LiveData<Boolean> {
        allergyCreate.apply {
            patient = AllergyPatient().apply {
                uuid = this@AddEditAllergyViewModel.patient.uuid
                identifier = emptyList()
            }
            reactions = selectedReactions.values.toList()
        }
        allergyCreate.reactions = selectedReactions.values.toList()
        return if (!isUpdateAllergy) createAllergy() else updateAllergy()
    }

    private fun createAllergy(): LiveData<Boolean> {
        val successLiveData = MutableLiveData<Boolean>()
        addSubscription(allergyRepository.createAllergy(patient, allergyCreate)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { successLiveData.value = true },
                        { successLiveData.value = false }
                )
        )
        return successLiveData
    }

    private fun updateAllergy(): LiveData<Boolean> {
        val successLiveData = MutableLiveData<Boolean>()
        addSubscription(allergyRepository.updateAllergy(patient, allergyUuid, allergyToUpdate?.id, allergyCreate)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { successLiveData.value = true },
                        { successLiveData.value = false }
                )
        )
        return successLiveData
    }
}
