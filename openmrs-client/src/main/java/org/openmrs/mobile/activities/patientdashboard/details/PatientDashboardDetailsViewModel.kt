package org.openmrs.mobile.activities.patientdashboard.details

import androidx.lifecycle.SavedStateHandle
import com.openmrs.android_sdk.library.api.repository.PatientRepository
import com.openmrs.android_sdk.library.dao.PatientDAO
import com.openmrs.android_sdk.library.models.OperationType.PatientFetching
import com.openmrs.android_sdk.library.models.Patient
import com.openmrs.android_sdk.utilities.ApplicationConstants.BundleKeys.PATIENT_ID_BUNDLE
import dagger.hilt.android.lifecycle.HiltViewModel
import org.openmrs.mobile.activities.BaseViewModel
import javax.inject.Inject

@HiltViewModel
class PatientDashboardDetailsViewModel @Inject constructor(
        private val patientDAO: PatientDAO,
        private val savedStateHandle: SavedStateHandle
) : BaseViewModel<Patient>() {

    private val patientId: String = savedStateHandle.get(PATIENT_ID_BUNDLE)!!

    fun fetchPatientData() {
        setLoading(PatientFetching)
        val patient = patientDAO.findPatientByID(patientId)
        if (patient != null) setContent(patient, PatientFetching)
        else setError(IllegalStateException("Error fetching patient"), PatientFetching)
    }
}
