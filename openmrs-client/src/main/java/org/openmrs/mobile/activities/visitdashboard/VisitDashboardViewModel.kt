package org.openmrs.mobile.activities.visitdashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import com.openmrs.android_sdk.library.api.repository.VisitRepository
import com.openmrs.android_sdk.library.dao.VisitDAO
import com.openmrs.android_sdk.library.models.Result
import com.openmrs.android_sdk.library.models.Visit
import com.openmrs.android_sdk.utilities.ApplicationConstants.BundleKeys.VISIT_ID
import dagger.hilt.android.lifecycle.HiltViewModel
import org.openmrs.mobile.activities.BaseViewModel
import rx.android.schedulers.AndroidSchedulers
import javax.inject.Inject

@HiltViewModel
class VisitDashboardViewModel @Inject constructor(
        private val visitDAO: VisitDAO,
        private val visitRepository: VisitRepository,
        private val savedStateHandle: SavedStateHandle
) : BaseViewModel<Visit>() {

    private val visitId: Long = savedStateHandle.get(VISIT_ID)!!
    val visit: Visit?
        get() {
            val visitResult = result.value
            return if (visitResult is Result.Success<Visit>) visitResult.data else null
        }

    fun fetchCurrentVisit() {
        setLoading()
        addSubscription(visitDAO.getVisitByID(visitId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { visit -> setContent(visit) },
                        { setError(it) }
                )
        )
    }

    fun endCurrentVisit(): LiveData<Boolean> {
        val endVisitResult = MutableLiveData<Boolean>()

        if (visit != null) {
            addSubscription(visitRepository.endVisit(visit)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            { endVisitResult.value = true },
                            { endVisitResult.value = false }
                    )
            )
        } else {
            endVisitResult.value = false
        }

        return endVisitResult
    }
}
