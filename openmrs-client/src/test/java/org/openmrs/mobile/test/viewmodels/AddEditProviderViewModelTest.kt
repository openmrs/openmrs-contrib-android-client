package org.openmrs.mobile.test.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import com.openmrs.android_sdk.library.api.repository.ProviderRepository
import com.openmrs.android_sdk.library.models.Person
import com.openmrs.android_sdk.library.models.PersonName
import com.openmrs.android_sdk.library.models.Provider
import com.openmrs.android_sdk.library.models.Result
import com.openmrs.android_sdk.library.models.ResultType.AddProviderSuccess
import com.openmrs.android_sdk.library.models.ResultType.UpdateProviderSuccess
import com.openmrs.android_sdk.utilities.ApplicationConstants.BundleKeys.PROVIDER_BUNDLE
import org.junit.Rule
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.openmrs.mobile.activities.addeditprovider.AddEditProviderViewModel
import org.openmrs.mobile.test.ACUnitTestBaseRx
import rx.Observable

@RunWith(JUnit4::class)
class AddEditProviderViewModelTest : ACUnitTestBaseRx() {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    lateinit var providerRepository: ProviderRepository

    lateinit var viewModel: AddEditProviderViewModel

    @Test
    fun `initializeProvider should create a new provider in the ViewModel`() {
        viewModel = AddEditProviderViewModel(providerRepository, SavedStateHandle())

        assertNull(viewModel.provider!!.person)
        viewModel.initializeProvider("John", "Smith", "Doctor")

        assertEquals("John", viewModel.provider!!.person!!.name.givenName)
        assertEquals("Smith", viewModel.provider!!.person!!.name.familyName)
        assertEquals("Doctor", viewModel.provider!!.identifier)
    }

    @Test
    fun `initializeProvider should modify the current provider in the ViewModel`() {
        val provider = Provider().apply {
            identifier = "Doctor"
            uuid = "UUID"
            person = Person().apply {
                names = listOf(PersonName().apply {
                    givenName = "John"
                    familyName = "Smith"
                    display = "John Smith"
                })
            }
        }
        val savedStateHandle = SavedStateHandle().apply { set(PROVIDER_BUNDLE, provider) }
        viewModel = AddEditProviderViewModel(providerRepository, savedStateHandle)

        viewModel.initializeProvider("John", "Sanchez", "Pharmacist")

        assertEquals("John", viewModel.provider!!.person!!.name.givenName)
        assertEquals("Sanchez", viewModel.provider!!.person!!.name.familyName)
        assertEquals("Pharmacist", viewModel.provider!!.identifier)
        assertEquals("UUID", viewModel.provider!!.uuid)
    }

    @Test
    fun `submitProvider should create new provider when no provider is passed`() {
        viewModel = AddEditProviderViewModel(providerRepository, SavedStateHandle())
        `when`(providerRepository.addProvider(any())).thenReturn(Observable.just(AddProviderSuccess))

        viewModel.submitProvider()
        val resultType = (viewModel.result.value as Result.Success).data

        assertEquals(AddProviderSuccess, resultType)
    }

    @Test
    fun `submitProvider should update existing patient when its id is passed`() {
        val provider = Provider().apply {
            person = Person().apply { display = "John Smith" }
            identifier = "Doctor"
        }
        val savedStateHandle = SavedStateHandle().apply { set(PROVIDER_BUNDLE, provider) }
        viewModel = AddEditProviderViewModel(providerRepository, savedStateHandle)
        `when`(providerRepository.updateProvider(any())).thenReturn(Observable.just(UpdateProviderSuccess))

        viewModel.submitProvider()
        val resultType = (viewModel.result.value as Result.Success).data

        assertEquals(UpdateProviderSuccess, resultType)
    }

    @Test
    fun `submitProvider should return error`() {
        val throwable = Throwable("errorMessage")
        viewModel = AddEditProviderViewModel(providerRepository, SavedStateHandle())
        `when`(providerRepository.addProvider(any())).thenReturn(Observable.error(throwable))

        viewModel.submitProvider()

        assert(viewModel.result.value is Result.Error)
    }
}
