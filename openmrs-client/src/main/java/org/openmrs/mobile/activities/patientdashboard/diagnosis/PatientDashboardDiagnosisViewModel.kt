package org.openmrs.mobile.activities.patientdashboard.diagnosis

import androidx.lifecycle.SavedStateHandle
import com.openmrs.android_sdk.library.dao.EncounterDAO
import com.openmrs.android_sdk.library.models.Encounter
import com.openmrs.android_sdk.library.models.EncounterType
import com.openmrs.android_sdk.library.models.EncounterType.Companion.VISIT_NOTE
import com.openmrs.android_sdk.utilities.ApplicationConstants.BundleKeys.PATIENT_ID_BUNDLE
import dagger.hilt.android.lifecycle.HiltViewModel
import org.openmrs.mobile.activities.BaseViewModel
import rx.android.schedulers.AndroidSchedulers
import javax.inject.Inject
import java.util.ArrayList

@HiltViewModel
class PatientDashboardDiagnosisViewModel @Inject constructor(
        private val encounterDAO: EncounterDAO,
        private val savedStateHandle: SavedStateHandle
) : BaseViewModel<List<String>>() {

    private val patientId: String = savedStateHandle.get(PATIENT_ID_BUNDLE)!!

    fun fetchDiagnoses() {
        setLoading()
        addSubscription(encounterDAO.getAllEncountersByType(patientId.toLong(), EncounterType(VISIT_NOTE))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { encounters: List<Encounter> ->
                    val diagnosis = loadDiagnosesFromEncounters(encounters)
                    setContent(diagnosis)
                }
        )
    }

    private fun loadDiagnosesFromEncounters(encounters: List<Encounter>): List<String> {
        val diagnoses = ArrayList<String>()
        for (encounter in encounters) {
            for (obs in encounter.observations) {
                if (!obs.diagnosisList.isNullOrEmpty() && !diagnoses.contains(obs.diagnosisList!!)) {
                    diagnoses.add(obs.diagnosisList!!)
                }
            }
        }
        return diagnoses
    }
}
