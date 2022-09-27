package org.openmrs.mobile.activities.formadmission

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import com.openmrs.android_sdk.library.api.repository.EncounterRepository
import com.openmrs.android_sdk.library.api.repository.FormRepository
import com.openmrs.android_sdk.library.api.repository.ProviderRepository
import com.openmrs.android_sdk.library.dao.PatientDAO
import com.openmrs.android_sdk.library.databases.entities.LocationEntity
import com.openmrs.android_sdk.library.models.EncounterProviderCreate
import com.openmrs.android_sdk.library.models.Encountercreate
import com.openmrs.android_sdk.library.models.Patient
import com.openmrs.android_sdk.library.models.Provider
import com.openmrs.android_sdk.library.models.Resource
import com.openmrs.android_sdk.library.models.ResultType
import com.openmrs.android_sdk.utilities.ApplicationConstants.BundleKeys.ENCOUNTERTYPE
import com.openmrs.android_sdk.utilities.ApplicationConstants.BundleKeys.FORM_NAME
import com.openmrs.android_sdk.utilities.ApplicationConstants.BundleKeys.PATIENT_ID_BUNDLE
import com.openmrs.android_sdk.utilities.ApplicationConstants.LOCATION
import com.openmrs.android_sdk.utilities.execute
import dagger.hilt.android.lifecycle.HiltViewModel
import org.openmrs.mobile.activities.BaseViewModel
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import javax.inject.Inject

@HiltViewModel
class FormAdmissionViewModel @Inject constructor(
        private val patientDAO: PatientDAO,
        private val formRepository: FormRepository,
        private val encounterRepository: EncounterRepository,
        private val providerRepository: ProviderRepository,
        private val savedStateHandle: SavedStateHandle
) : BaseViewModel<Unit>() {

    /* UI */
    var providerListPosition: Int = 0
    var encounterRoleListPosition: Int = 0
    var targetLocationListPosition: Int = 0

    private val patientId: Long = savedStateHandle.get(PATIENT_ID_BUNDLE)!!
    private val encounterType: String = savedStateHandle.get(ENCOUNTERTYPE)!!
    private val formName: String = savedStateHandle.get(FORM_NAME)!!
    private val currentLocation: String = savedStateHandle.get(LOCATION)!!

    val patient: Patient = patientDAO.findPatientByID(patientId.toString())

    /* Lists of form fields */
    var providers: LinkedHashMap<String, Provider> = linkedMapOf()
        private set
    var encounterRoles: LinkedHashMap<String, Resource> = linkedMapOf()
        private set
    var targetLocations: LinkedHashMap<String, LocationEntity> = linkedMapOf()
        private set

    /* UUIDs of the selected items in form lists */
    var providerUuid: String? = null
        private set
    var encounterRoleUuid: String? = null
        private set
    var targetLocationUuid: String? = null
        private set

    private val listsObservables
        get() = listOf(
                providerRepository.getProviders().map { list ->
                    providers = LinkedHashMap<String, Provider>().apply {
                        list.forEach { put(it.display!!, it) }
                    }
                },
                providerRepository.getEncounterRoles().map { list ->
                    encounterRoles = LinkedHashMap<String, Resource>().apply {
                        list.forEach { put(it.display!!, it) }
                    }
                },
                providerRepository.getLocations(currentLocation).map { list ->
                    targetLocations = LinkedHashMap<String, LocationEntity>().apply {
                        list.forEach { put(it.display!!, it) }
                    }
                }
        )


    init {
        fetchFormFields()
    }

    private fun fetchFormFields() {
        setLoading()
        addSubscription(Observable.merge(listsObservables)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnCompleted {
                    if (providers.isNotEmpty() && encounterRoles.isNotEmpty() && targetLocations.isNotEmpty()) {
                        setContent(Unit)
                    } else {
                        setError(Throwable("Some form field lists are empty"))
                    }
                }
                .subscribe({}, {
                    clearSubscriptions()
                    setError(it)
                })
        )
    }

    fun selectProvider(providerName: String, listPosition: Int) {
        providerListPosition = listPosition
        providerUuid = providers[providerName]!!.uuid
    }

    fun selectEncounterRole(roleName: String, listPosition: Int) {
        encounterRoleListPosition = listPosition
        encounterRoleUuid = encounterRoles[roleName]!!.uuid
    }

    fun selectTargetLocation(locationName: String, listPosition: Int) {
        targetLocationListPosition = listPosition
        targetLocationUuid = targetLocations[locationName]!!.uuid
    }

    fun submitAdmission(): LiveData<ResultType> {
        val resultLiveData = MutableLiveData<ResultType>()

        addSubscription(Observable.fromCallable {
            val enc = Encountercreate()
            enc.patient = patient.uuid
            enc.encounterType = encounterType
            enc.formname = formName
            enc.patientId = patientId
            enc.formUuid = formRepository.fetchFormResourceByName(formName).execute().uuid
            enc.location = targetLocationUuid
            enc.encounterProvider = listOf(EncounterProviderCreate(providerUuid!!, encounterRoleUuid!!))
            return@fromCallable enc
        }
                .flatMap { encounterCreate -> encounterRepository.saveEncounter(encounterCreate) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { resultLiveData.value = it },
                        { resultLiveData.value = ResultType.EncounterSubmissionError }
                )
        )

        return resultLiveData
    }
}
