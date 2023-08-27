package org.openmrs.mobile.test.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.openmrs.android_sdk.library.dao.VisitDAO
import com.openmrs.android_sdk.library.models.Result
import com.openmrs.android_sdk.library.models.Visit
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertIterableEquals
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.openmrs.mobile.activities.activevisits.ActiveVisitsViewModel
import org.openmrs.mobile.test.ACUnitTestBaseRx
import rx.Observable

@RunWith(JUnit4::class)
class ActiveVisitsViewModelTest : ACUnitTestBaseRx() {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    lateinit var visitDAO: VisitDAO

    lateinit var visitList: List<Visit>

    lateinit var viewModel: ActiveVisitsViewModel

    @Before
    override fun setUp(){
        super.setUp()
        MockitoAnnotations.initMocks(this)
        visitList = createVisitList()
        viewModel = ActiveVisitsViewModel(visitDAO)
    }

    @Test
    fun getActiveVisits_success(){
        Mockito.`when`(visitDAO.getActiveVisits()).thenReturn(Observable.just(visitList))

        viewModel.fetchActiveVisits()

        val actualResult = (viewModel.result.value as Result.Success).data

        assertIterableEquals(visitList, actualResult)
    }

    @Test
    fun getActiveVisits_error(){
        val errorMsg = "Error message!"
        val throwable = Throwable(errorMsg)
        Mockito.`when`(visitDAO.getActiveVisits()).thenReturn(Observable.error(throwable))

        viewModel.fetchActiveVisits()

        val actualResult = (viewModel.result.value as Result.Error).throwable.message

        assertEquals(errorMsg, actualResult)
    }

    @Test
    fun getActiveVisitsWithQuery_success(){
        val visit = visitList[0]
        val filteredVisits = listOf(visit)
        Mockito.`when`(visitDAO.getActiveVisits()).thenReturn(Observable.just(filteredVisits))

        viewModel.fetchActiveVisits(visit.display!!)

        val actualResult = (viewModel.result.value as Result.Success).data

        assertIterableEquals(filteredVisits, actualResult)
    }

    @Test
    fun getActiveVisitsWithQuery_noMatchingVisits(){
        Mockito.`when`(visitDAO.getActiveVisits()).thenReturn(Observable.just(emptyList()))

        viewModel.fetchActiveVisits("Visit99")

        val actualResult = (viewModel.result.value as Result.Success).data

        assertIterableEquals(emptyList<Visit>(), actualResult)
    }

    @Test
    fun getActiveVisitsWithQuery_error(){
        val errorMsg = "Error message!"
        val throwable = Throwable(errorMsg)
        Mockito.`when`(visitDAO.getActiveVisits()).thenReturn(Observable.error(throwable))

        viewModel.fetchActiveVisits("visit1")

        val actualResult = (viewModel.result.value as Result.Error).throwable.message

        assertEquals(errorMsg, actualResult)
    }
}
