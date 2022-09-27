package org.openmrs.mobile.activities.providerdashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import com.openmrs.android_sdk.library.api.repository.ProviderRepository
import com.openmrs.android_sdk.library.models.Provider
import com.openmrs.android_sdk.library.models.ResultType
import com.openmrs.android_sdk.utilities.ApplicationConstants.BundleKeys.PROVIDER_BUNDLE
import dagger.hilt.android.lifecycle.HiltViewModel
import org.openmrs.mobile.activities.BaseViewModel
import rx.android.schedulers.AndroidSchedulers
import javax.inject.Inject

@HiltViewModel
class ProviderDashboardViewModel @Inject constructor(
        private val providerRepository: ProviderRepository,
        private val savedStateHandle: SavedStateHandle
) : BaseViewModel<Unit>() {

    val provider = savedStateHandle.get<Provider>(PROVIDER_BUNDLE)!!
    val screenTitle: String get() {
        var display = provider.person!!.display
        if (display == null) {
            display = provider.person!!.name.nameString
        }
        return display
    }

    fun deleteProvider(): LiveData<ResultType> {
        val resultType = MutableLiveData<ResultType>()
        addSubscription(providerRepository.deleteProviders(provider.uuid)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { resultType.value = it },
                        { resultType.value = ResultType.ProviderDeletionError }
                )
        )
        return resultType
    }
}
