package org.openmrs.mobile.test.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.gson.Gson
import com.openmrs.android_sdk.library.api.repository.FormRepository
import com.openmrs.android_sdk.library.dao.EncounterDAO
import com.openmrs.android_sdk.library.databases.entities.FormResourceEntity
import com.openmrs.android_sdk.library.models.EncounterType
import com.openmrs.android_sdk.library.models.EncounterType.Companion.ADMISSION
import com.openmrs.android_sdk.library.models.EncounterType.Companion.VITALS
import com.openmrs.android_sdk.library.models.Result
import org.junit.Rule
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.openmrs.mobile.activities.formlist.FormListViewModel
import org.openmrs.mobile.test.ACUnitTestBaseRx
import rx.Observable

@RunWith(JUnit4::class)
class FormListViewModelTest : ACUnitTestBaseRx() {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    lateinit var encounterDAO: EncounterDAO

    @Mock
    lateinit var formRepository: FormRepository

    lateinit var viewModel: FormListViewModel

    @Test
    fun `ViewModel should load only forms provided with json valueReference (form fields)`() {
        val formName1 = "firstForm"
        val formName2 = "secondForm"
        val formName3 = "thirdForm"
        val formName4 = "fourthForm"
        val formResourceList = listOf(
                createFormResource(formName1),
                createFormResource(formName2),
                createFormResource(formName3, "json"),
                createFormResource(formName4, "NOT json")
        )

        val expectedFormsArray = arrayOf(formName3)
        `when`(formRepository.fetchFormResourceList()).thenReturn(Observable.just(formResourceList))


        viewModel = FormListViewModel(encounterDAO, formRepository)

        val result = viewModel.result.value as Result.Success
        assertArrayEquals(expectedFormsArray, result.data)
    }

    @Test
    fun `click on a form should get data for that form`() {
        val formName1 = "Admission (Simple)"
        val formName2 = "Vitals"
        val encounterName1 = ADMISSION
        val encounterName2 = VITALS
        val encounterTypeUuid1 = "15789573219881759238790"
        val encounterTypeUuid2 = "45425454534354534354354"
        val encounterType1 = EncounterType(ADMISSION).apply { uuid = encounterTypeUuid1 }
        val encounterType2 = EncounterType(VITALS).apply { uuid = encounterTypeUuid2 }
        val formResource1 = createFormResource(formName1, "json")
        val formResource2 = createFormResource(formName2, "json")
        val formResourceList: List<FormResourceEntity> = listOf(formResource1, formResource2)

        `when`(formRepository.fetchFormResourceList()).thenReturn(Observable.just(formResourceList))
        `when`(encounterDAO.getEncounterTypeByFormName(ADMISSION)).thenReturn(encounterType1)
        `when`(encounterDAO.getEncounterTypeByFormName(VITALS)).thenReturn(encounterType2)

        viewModel = FormListViewModel(encounterDAO, formRepository)

        val selectedForm1 = viewModel.SelectedForm(0)
        assertEquals(formName1, selectedForm1.formName)
        assertEquals(encounterName1, selectedForm1.encounterName)
        assertEquals(encounterType1.uuid, selectedForm1.encounterType)
        assertEquals(formResource1.resources[0].valueReference, selectedForm1.formFieldsJson)

        val selectedForm2 = viewModel.SelectedForm(1)
        assertEquals(formName2, selectedForm2.formName)
        assertEquals(encounterName2, selectedForm2.encounterName)
        assertEquals(encounterType2.uuid, selectedForm2.encounterType)
        assertEquals(formResource2.resources[0].valueReference, selectedForm2.formFieldsJson)
    }

    private fun createFormResource(formName: String, subResourceName: String? = null): FormResourceEntity {
        val exampleJson = getExampleFormResourceJson(formName)
        val formResourceEntity = Gson().fromJson(exampleJson, FormResourceEntity::class.java).apply {
            valueReference = exampleJson
            if (subResourceName != null) {
                val subResource = getExampleFormResourceJson(subResourceName)
                resources = listOf(Gson().fromJson(subResource, FormResourceEntity::class.java))
            }

        }
        return formResourceEntity
    }

    private fun getExampleFormResourceJson(name: String): String {
        return "{" +
                "\"display\":\"json\"," +
                "\"name\":\"" + name + "\"," +
                "\"valueReference\":\"" +
                "{" +
                "\\\"name\\\":\\\"Some Form\\\"," +
                "\\\"uuid\\\":\\\"77174d67-954f-45c4-a782-d157e70d59f4\\\"" +
                "}\"" +
                "}"
    }
}
