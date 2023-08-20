package org.openmrs.mobile.activities.activevisits

import com.openmrs.android_sdk.library.dao.VisitDAO
import com.openmrs.android_sdk.library.models.OperationType.ActiveVisitsFetching
import com.openmrs.android_sdk.library.models.OperationType.ActiveVisitsSearching
import com.openmrs.android_sdk.library.models.Visit
import dagger.hilt.android.lifecycle.HiltViewModel
import org.openmrs.mobile.activities.BaseViewModel
import org.openmrs.mobile.utilities.FilterUtil
import rx.android.schedulers.AndroidSchedulers
import javax.inject.Inject

@HiltViewModel
class ActiveVisitsViewModel @Inject constructor(private val visitDAO: VisitDAO) : BaseViewModel<List<Visit>>() {

    fun fetchActiveVisits() {
        setLoading()
        addSubscription(visitDAO.getActiveVisits()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { visits: List<Visit> -> setContent(visits) },
                        { setError(it, ActiveVisitsFetching) }
                ))
    }

    fun fetchActiveVisits(query: String) {
        setLoading()
        addSubscription(visitDAO.getActiveVisits()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { visits: List<Visit> ->
                            val filteredVisits = FilterUtil.getPatientsWithActiveVisitsFilteredByQuery(visits, query)
                            setContent(filteredVisits)
                        },
                        { setError(it, ActiveVisitsSearching) }
                ))
    }
}
