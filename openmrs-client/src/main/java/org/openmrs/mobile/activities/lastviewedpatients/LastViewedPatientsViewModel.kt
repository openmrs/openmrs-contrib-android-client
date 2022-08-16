package org.openmrs.mobile.activities.lastviewedpatients

import com.openmrs.android_sdk.library.api.repository.PatientRepository
import com.openmrs.android_sdk.library.dao.PatientDAO
import com.openmrs.android_sdk.library.models.Link
import com.openmrs.android_sdk.library.models.OperationType.LastViewedPatientsFetching
import com.openmrs.android_sdk.library.models.OperationType.PatientSearching
import com.openmrs.android_sdk.library.models.Patient
import com.openmrs.android_sdk.library.models.Results
import dagger.hilt.android.lifecycle.HiltViewModel
import org.openmrs.mobile.activities.BaseViewModel
import rx.android.schedulers.AndroidSchedulers
import javax.inject.Inject


@HiltViewModel
class LastViewedPatientsViewModel @Inject constructor(
        private val patientDAO: PatientDAO,
        private val patientRepository: PatientRepository
) : BaseViewModel<List<Patient>>() {

    private val paginatedPatients = mutableListOf<Patient>()
    var isDownloadedAll = false
        private set
    var startIndex = 0
        private set
    private val limit = 15

    fun fetchLastViewedPatients(limit: Int = this.limit) {
        if (!isDownloadedAll) {
            setLoading(LastViewedPatientsFetching)
            addSubscription(patientRepository.loadMorePatients(limit, startIndex)
                    .map { patientResults: Results<Patient> ->
                        processPagination(patientResults, limit)

                        if (paginatedPatients.size >= limit) return@map patientResults.results

                        while (paginatedPatients.size < limit && !isDownloadedAll) {
                            val moreResults = patientRepository.loadMorePatients(limit, startIndex).toSingle().toBlocking().value()
                            processPagination(moreResults, limit)
                        }
                        return@map paginatedPatients
                    }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            { patientList -> setContent(patientList, LastViewedPatientsFetching) },
                            { setError(it, LastViewedPatientsFetching) }
                    )
            )
        }
    }

    fun fetchPatients(query: String) {
        setLoading(PatientSearching)
        addSubscription(patientRepository.findPatients(query)
                .map { patientList -> return@map patientDAO.excludeSavedPatients(patientList) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { patientList -> setContent(patientList, PatientSearching) },
                        { setError(it, PatientSearching) }
                )
        )
    }

    private fun updateStartIndex(links: List<Link>, limit: Int) {
        for (link in links) {
            if (link.rel == "next") {
                startIndex += limit
                isDownloadedAll = false
                return
            }
        }
        isDownloadedAll = true
    }

    private fun processPagination(patientResults: Results<Patient>, limit: Int) {
        val patientList = patientDAO.excludeSavedPatients(patientResults.results)
        updateStartIndex(patientResults.links, limit)
        paginatedPatients.addAll(patientList)
    }

    fun resetPagination() {
        startIndex = 0
        isDownloadedAll = false
        paginatedPatients.clear()
    }

}
