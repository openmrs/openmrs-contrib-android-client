package org.openmrs.mobile.activities.formentrypatientlist

import com.openmrs.android_sdk.library.dao.VisitDAO
import com.openmrs.android_sdk.library.models.OperationType.PatientFetching
import com.openmrs.android_sdk.library.models.OperationType.PatientSearching
import com.openmrs.android_sdk.library.models.Patient
import dagger.hilt.android.lifecycle.HiltViewModel
import org.openmrs.mobile.activities.BaseViewModel
import org.openmrs.mobile.utilities.FilterUtil
import rx.android.schedulers.AndroidSchedulers
import javax.inject.Inject

@HiltViewModel
class FormEntryPatientListViewModel @Inject constructor(
        private val visitDAO: VisitDAO
) : BaseViewModel<List<Patient>>() {

    var mQuery: String? = null

    fun fetchSavedPatientsWithActiveVisits(query: String? = null) {
        mQuery = query
        setLoading()
        addSubscription(visitDAO.getActiveVisits()
                .map { visits ->
                    val patients = mutableListOf<Patient>()
                    visits.forEach { patients += it.patient }
                    return@map patients
                }
                .map { patients ->
                    if (query.isNullOrEmpty()) return@map patients
                    else return@map FilterUtil.getPatientsFilteredByQuery(patients, query)
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        {
                            if (query.isNullOrEmpty()) setContent(it, PatientFetching)
                            else setContent(it, PatientSearching)
                        },
                        {
                            if (query.isNullOrEmpty()) setError(it, PatientFetching)
                            else setError(it, PatientSearching)
                        }
                )
        )
    }
}
