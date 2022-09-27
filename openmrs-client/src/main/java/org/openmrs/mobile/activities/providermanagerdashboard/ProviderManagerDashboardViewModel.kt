package org.openmrs.mobile.activities.providermanagerdashboard

import com.openmrs.android_sdk.library.api.repository.ProviderRepository
import com.openmrs.android_sdk.library.models.Provider
import dagger.hilt.android.lifecycle.HiltViewModel
import org.openmrs.mobile.activities.BaseViewModel
import org.openmrs.mobile.utilities.FilterUtil
import rx.android.schedulers.AndroidSchedulers
import javax.inject.Inject

@HiltViewModel
class ProviderManagerDashboardViewModel @Inject constructor(
        private val providerRepository: ProviderRepository
) : BaseViewModel<List<Provider>>() {

    fun fetchProviders(query: String? = null) {
        setLoading()
        addSubscription(providerRepository.getProviders()
                .map { providers ->
                    if (query.isNullOrEmpty()) return@map providers
                    else return@map FilterUtil.getProvidersFilteredByQuery(providers, query)
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { providers -> setContent(providers) },
                        { throwable -> setError(throwable) }
                )
        )
    }
}
