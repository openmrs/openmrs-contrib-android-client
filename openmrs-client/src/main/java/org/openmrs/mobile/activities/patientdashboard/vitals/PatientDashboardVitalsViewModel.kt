package org.openmrs.mobile.activities.patientdashboard.vitals

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import com.openmrs.android_sdk.library.api.repository.VisitRepository
import com.openmrs.android_sdk.library.dao.EncounterDAO
import com.openmrs.android_sdk.library.dao.PatientDAO
import com.openmrs.android_sdk.library.models.Encounter
import com.openmrs.android_sdk.library.models.OperationType.PatientVitalsFetching
import com.openmrs.android_sdk.utilities.ApplicationConstants.BundleKeys.PATIENT_ID_BUNDLE
import dagger.hilt.android.lifecycle.HiltViewModel
import org.openmrs.mobile.activities.BaseViewModel
import rx.android.schedulers.AndroidSchedulers
import javax.inject.Inject

@HiltViewModel
class PatientDashboardVitalsViewModel @Inject constructor(
        private val patientDAO: PatientDAO,
        private val encounterDAO: EncounterDAO,
        private val savedStateHandle: SavedStateHandle
) : BaseViewModel<Encounter>() {

    private val patientId: String = savedStateHandle.get(PATIENT_ID_BUNDLE)!!

    fun fetchLastVitals() {
        setLoading(PatientVitalsFetching)
        val patient = patientDAO.findPatientByID(patientId)
        addSubscription(encounterDAO.getLastVitalsEncounter(patient.uuid)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { encounter -> setContent(encounter, PatientVitalsFetching) },
                        { setError(it) }
                )
        )
    }

    fun fetchLastVitalsEncounter(): LiveData<Encounter> {
        setLoading()
        val liveData = MutableLiveData<Encounter>()
        val patient = patientDAO.findPatientByID(patientId)
        addSubscription(encounterDAO.getLastVitalsEncounter(patient.uuid)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { encounter -> liveData.value = encounter }
        )
        return liveData
    }
}
