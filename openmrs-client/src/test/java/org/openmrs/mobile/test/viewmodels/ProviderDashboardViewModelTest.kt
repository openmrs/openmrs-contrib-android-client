package org.openmrs.mobile.test.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import com.openmrs.android_sdk.library.api.repository.ProviderRepository
import com.openmrs.android_sdk.library.models.Provider
import com.openmrs.android_sdk.library.models.ResultType
import com.openmrs.android_sdk.utilities.ApplicationConstants.BundleKeys.PROVIDER_BUNDLE
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.openmrs.mobile.activities.providerdashboard.ProviderDashboardViewModel
import org.openmrs.mobile.test.ACUnitTestBaseRx
import rx.Observable

@RunWith(JUnit4::class)
class ProviderDashboardViewModelTest : ACUnitTestBaseRx() {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    lateinit var providerRepository: ProviderRepository

    lateinit var viewModel: ProviderDashboardViewModel

    @Before
    override fun setUp() {
        super.setUp()
        val savedStateHandle = SavedStateHandle().apply { set(PROVIDER_BUNDLE, Provider()) }
        viewModel = ProviderDashboardViewModel(providerRepository, savedStateHandle)
    }

    @Test
    fun deleteProvider() {
        `when`(providerRepository.deleteProviders(any())).thenReturn(Observable.just(ResultType.ProviderDeletionSuccess))
        viewModel.deleteProvider().observeForever { resultType ->
            assertEquals(ResultType.ProviderDeletionSuccess, resultType)
        }
    }
}
