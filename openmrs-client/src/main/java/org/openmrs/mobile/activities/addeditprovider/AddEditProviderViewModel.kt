package org.openmrs.mobile.activities.addeditprovider

import androidx.lifecycle.SavedStateHandle
import com.openmrs.android_sdk.library.api.repository.ProviderRepository
import com.openmrs.android_sdk.library.models.OperationType.ProviderRegistering
import com.openmrs.android_sdk.library.models.OperationType.ProviderUpdating
import com.openmrs.android_sdk.library.models.Person
import com.openmrs.android_sdk.library.models.PersonName
import com.openmrs.android_sdk.library.models.Provider
import com.openmrs.android_sdk.library.models.ResultType
import com.openmrs.android_sdk.utilities.ApplicationConstants.BundleKeys.PROVIDER_BUNDLE
import dagger.hilt.android.lifecycle.HiltViewModel
import org.openmrs.mobile.activities.BaseViewModel
import rx.android.schedulers.AndroidSchedulers
import javax.inject.Inject

@HiltViewModel
class AddEditProviderViewModel @Inject constructor(
        private val providerRepository: ProviderRepository,
        private val savedStateHandle: SavedStateHandle
) : BaseViewModel<ResultType>() {

    var provider = savedStateHandle.get(PROVIDER_BUNDLE) as? Provider
        private set
    val isUpdateProvider = provider != null

    init {
        if (!isUpdateProvider) provider = Provider()
        setProviderPerson()
    }

    fun initializeProvider(firstName: String, lastName: String, identifier: String) {
        if (isUpdateProvider) updateCurrentProvider(firstName, lastName, identifier)
        else createNewProvider(firstName, lastName, identifier)
    }

    fun submitProvider() {
        if (isUpdateProvider) updateProvider()
        else registerProvider()
    }

    private fun registerProvider() {
        setLoading(ProviderRegistering)
        addSubscription(providerRepository.addProvider(provider)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { resultType: ResultType -> setContent(resultType) },
                        { setError(it) }
                )
        )
    }

    private fun updateProvider() {
        setLoading(ProviderUpdating)
        addSubscription(providerRepository.updateProvider(provider)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { resultType: ResultType -> setContent(resultType) },
                        { setError(it) }
                )
        )
    }

    private fun createNewProvider(firstName: String, lastName: String, identifier: String) {
        val person = Person().apply {
            names = listOf(PersonName().apply {
                givenName = firstName
                familyName = lastName
            })
            uuid = null
            // This display gets used up by the recycler view for the name
            display = "$firstName $lastName"
        }

        this.provider = Provider().apply {
            this.person = person
            this.identifier = identifier
            retired = false
            uuid = null
        }
    }

    private fun updateCurrentProvider(firstName: String, lastName: String, identifier: String) {
        this.provider!!.person!!.run {
            name.givenName = firstName
            name.familyName = lastName
            display = "$firstName $lastName"
        }
        this.provider!!.identifier = identifier
    }

    private fun setProviderPerson() {
        if (isUpdateProvider) {
            // Assign provider person name if not present
            provider!!.person!!.run {
                if (name == null || name.givenName == null || name.familyName == null) {
                    val personDisplay = display!!
                    names = listOf(PersonName().apply {
                        givenName = personDisplay.substring(0, personDisplay.indexOf(' '))
                        familyName = personDisplay.substring(personDisplay.lastIndexOf(' ') + 1)
                    })
                }
            }
        }
    }
}
