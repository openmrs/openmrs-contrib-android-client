package org.openmrs.mobile.test.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.openmrs.android_sdk.library.api.repository.ProviderRepository
import com.openmrs.android_sdk.library.models.Provider
import com.openmrs.android_sdk.library.models.Result
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertIterableEquals
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.openmrs.mobile.activities.providermanagerdashboard.ProviderManagerDashboardViewModel
import org.openmrs.mobile.test.ACUnitTestBaseRx
import rx.Observable

@RunWith(JUnit4::class)
class ProviderManagerDashboardViewModelTest : ACUnitTestBaseRx() {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    lateinit var providerRepository: ProviderRepository

    lateinit var viewModel: ProviderManagerDashboardViewModel

    @Before
    override fun setUp() {
        super.setUp()
        viewModel = ProviderManagerDashboardViewModel(providerRepository)
    }

    @Test
    fun `fetch providers should succeed`() {
        val providers = listOf(
                createProvider(1L, "doctor"),
                createProvider(2L, "nurse")
        )
        `when`(providerRepository.getProviders()).thenReturn(Observable.just(providers))

        viewModel.fetchProviders()
        val result = viewModel.result.value as Result.Success<List<Provider>>

        assertIterableEquals(providers, result.data)
    }

    @Test
    fun `fetch providers should return error`() {
        val errorMsg = "Error message!"
        val throwable = Throwable(errorMsg)
        `when`(providerRepository.getProviders()).thenReturn(Observable.error(throwable))

        viewModel.fetchProviders()
        val result = (viewModel.result.value as Result.Error).throwable.message

        assertEquals(errorMsg, result)
    }

    @Test
    fun `fetch providers by query name should succeed`() {
        val providers = listOf(
                createProvider(1L, "Doctor"),
                createProvider(2L, "Nurse")
        )
        val filteredPatients = listOf(providers[0])
        `when`(providerRepository.getProviders()).thenReturn(Observable.just(providers))

        viewModel.fetchProviders("doctor")

        val result = viewModel.result.value as Result.Success<List<Provider>>
        assertIterableEquals(filteredPatients, result.data)
    }
}
