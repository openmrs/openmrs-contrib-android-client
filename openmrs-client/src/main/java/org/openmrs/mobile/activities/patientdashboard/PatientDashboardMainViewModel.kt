package org.openmrs.mobile.activities.patientdashboard

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import com.openmrs.android_sdk.library.api.repository.AllergyRepository
import com.openmrs.android_sdk.library.api.repository.PatientRepository
import com.openmrs.android_sdk.library.api.repository.VisitRepository
import com.openmrs.android_sdk.library.dao.PatientDAO
import com.openmrs.android_sdk.library.dao.VisitDAO
import com.openmrs.android_sdk.library.models.OperationType
import com.openmrs.android_sdk.library.models.OperationType.PatientDeleting
import com.openmrs.android_sdk.library.models.OperationType.PatientSynchronizing
import com.openmrs.android_sdk.library.models.Patient
import com.openmrs.android_sdk.utilities.ApplicationConstants.BundleKeys.PATIENT_ID_BUNDLE
import dagger.hilt.android.lifecycle.HiltViewModel
import org.openmrs.mobile.activities.BaseViewModel
import rx.android.schedulers.AndroidSchedulers
import javax.inject.Inject


@HiltViewModel
class PatientDashboardMainViewModel @Inject constructor(
        private val patientDAO: PatientDAO,
        private val visitDAO: VisitDAO,
        private val patientRepository: PatientRepository,
        private val visitRepository: VisitRepository,
        private val allergyRepository: AllergyRepository,
        private val savedStateHandle: SavedStateHandle
) : BaseViewModel<Unit>() {

    val patientId: String = savedStateHandle.get<Long>(PATIENT_ID_BUNDLE)?.toString()!!
    private val patient: Patient = patientDAO.findPatientByID(patientId)
    private val patientUuid: String = patient.uuid!!

    private var runningSyncs = 0

    fun deletePatient() {
        setLoading(PatientDeleting)
        patientDAO.deletePatient(patientId.toLong())
        addSubscription(visitDAO.deleteVisitsByPatientId(patientId.toLong())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { setContent(Unit, PatientDeleting) },
                        { setError(it, PatientDeleting) }
                )
        )
    }

    fun syncPatientData() {
        setLoading(PatientSynchronizing)
        syncDetails()
        syncVisits()
        syncAllergies()
        syncVitals()
    }

    private fun syncDetails() {
        runningSyncs++
        addSubscription(patientRepository.downloadPatientByUuid(patientUuid)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { setContent(Unit, PatientSynchronizing) },
                        { setError(it, PatientSynchronizing) }
                )
        )
    }

    private fun syncVisits() {
        runningSyncs++
        addSubscription(visitRepository.syncVisitsData(patient)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { setContent(Unit, PatientSynchronizing) },
                        { setError(it, PatientSynchronizing) }
                ))
    }

    private fun syncAllergies() {
        runningSyncs++
        addSubscription(allergyRepository.syncAllergies(patient)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { setContent(Unit, PatientSynchronizing) },
                        { setError(it, PatientSynchronizing) }
                )
        )
    }

    private fun syncVitals() {
        runningSyncs++
        addSubscription(visitRepository.syncLastVitals(patient.uuid)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { setContent(Unit, PatientSynchronizing) },
                        { setError(it, PatientSynchronizing) }
                )
        )
    }

    override fun setContent(data: Unit, operationType: OperationType) {
        if (operationType == PatientSynchronizing) {
            runningSyncs--
            // Check if no syncs are still running
            if (runningSyncs == 0) super.setContent(data, operationType)
        } else {
            super.setContent(data, operationType)
        }
    }

    override fun setError(t: Throwable, operationType: OperationType) {
        Log.d("GeneralLogKey", " setError: ${t.message}")
        if (operationType == PatientSynchronizing) clearSubscriptions()
        super.setError(t, operationType)
    }
}
