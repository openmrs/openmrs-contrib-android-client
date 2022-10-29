package org.openmrs.mobile.test.viewmodels

import androidx.lifecycle.SavedStateHandle
import com.openmrs.android_sdk.library.models.Page
import com.openmrs.android_sdk.utilities.ApplicationConstants.BundleKeys.FORM_FIELDS_BUNDLE
import com.openmrs.android_sdk.utilities.ApplicationConstants.BundleKeys.FORM_PAGE_BUNDLE
import com.openmrs.android_sdk.utilities.InputField
import com.openmrs.android_sdk.utilities.SelectOneField
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.openmrs.mobile.activities.formdisplay.FormDisplayPageViewModel
import org.openmrs.mobile.bundle.FormFieldsWrapper
import org.openmrs.mobile.test.ACUnitTestBaseRx

@RunWith(JUnit4::class)
class FormDisplayPageViewModelTest : ACUnitTestBaseRx() {

    lateinit var savedStateHandle: SavedStateHandle

    lateinit var viewModel: FormDisplayPageViewModel

    private val inputFieldConcept1 = "CONCEPT 1"
    private val inputField1 = InputField(inputFieldConcept1).apply { value = 100.0 }

    private val selectOneFieldConcept = "CONCEPT 3"
    private val selectOneField1 = SelectOneField(emptyList(), selectOneFieldConcept)

    @Before
    override fun setUp() {
        super.setUp()
        val formFieldsWrapper = FormFieldsWrapper().apply {
            inputFields = arrayListOf(inputField1)
            selectOneFields = arrayListOf(selectOneField1)
        }
        savedStateHandle = SavedStateHandle().apply {
            set(FORM_PAGE_BUNDLE, Page())
            set(FORM_FIELDS_BUNDLE, formFieldsWrapper)
        }
        viewModel = FormDisplayPageViewModel(savedStateHandle)
    }

    @Test
    fun `getOrCreateInputField should return the InputField by its concept when field is present`() {
        val foundInputField = viewModel.getOrCreateInputField(inputFieldConcept1)
        assertEquals(inputField1, foundInputField)
    }

    @Test
    fun `getOrCreateInputField should return a new InputField when no matching field is present`() {
        val concept = "CONCEPT 99"

        val resultedInputField = viewModel.getOrCreateInputField(concept)

        assertEquals(InputField(concept), resultedInputField)
    }

    @Test
    fun findSelectOneFieldById() {
        val resultedSelectOneField = viewModel.findSelectOneFieldById(selectOneFieldConcept)
        assertEquals(selectOneField1, resultedSelectOneField)
    }
}
