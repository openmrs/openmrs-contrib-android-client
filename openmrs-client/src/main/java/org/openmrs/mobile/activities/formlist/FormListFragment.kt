/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.mobile.activities.formlist

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.annotation.Nullable
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.snackbar.Snackbar.SnackbarLayout
import com.openmrs.android_sdk.library.models.EncounterType
import com.openmrs.android_sdk.library.models.Result
import com.openmrs.android_sdk.utilities.ApplicationConstants
import com.openmrs.android_sdk.utilities.ApplicationConstants.BundleKeys.ENCOUNTERTYPE
import com.openmrs.android_sdk.utilities.ApplicationConstants.BundleKeys.FORM_NAME
import com.openmrs.android_sdk.utilities.ApplicationConstants.BundleKeys.PATIENT_ID_BUNDLE
import com.openmrs.android_sdk.utilities.ApplicationConstants.BundleKeys.VALUEREFERENCE
import com.openmrs.android_sdk.utilities.ToastUtil
import dagger.hilt.android.AndroidEntryPoint
import org.openmrs.mobile.R
import org.openmrs.mobile.activities.BaseFragment
import org.openmrs.mobile.activities.formadmission.FormAdmissionActivity
import org.openmrs.mobile.activities.formdisplay.FormDisplayActivity
import org.openmrs.mobile.databinding.FragmentFormListBinding

@AndroidEntryPoint
class FormListFragment : BaseFragment() {
    private var _binding: FragmentFormListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FormListViewModel by viewModels()

    @Nullable
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentFormListBinding.inflate(inflater, container, false)

        setupFormClickListener()
        setupObserver()

        return binding.root
    }

    private fun setupFormClickListener() {
        binding.formlist.setOnItemClickListener { _, _, position: Int, _ ->
            viewModel.SelectedForm(position).run {
                if (encounterType == null) {
                    ToastUtil.error(getString(R.string.no_such_form_name_error_message, formName))
                    return@setOnItemClickListener
                }
                val patientId: Long = requireArguments().get(PATIENT_ID_BUNDLE) as Long
                if (encounterName == EncounterType.ADMISSION) {
                    startAdmissionFormActivity(formName!!, patientId, encounterType!!)
                } else {
                    startFormDisplayActivity(formName!!, patientId, formFieldsJson!!, encounterType!!)
                }
            }
        }
    }

    private fun setupObserver() {
        viewModel.result.observe(viewLifecycleOwner, Observer { result ->
            when (result) {
                is Result.Loading -> {}
                is Result.Success -> showFormList(result.data)
                is Result.Error -> {}
                else -> throw IllegalStateException()
            }
        })
    }

    private fun showFormList(forms: Array<String>) {
        if (forms.isEmpty()) {
            val snackBar = Snackbar.make(binding.root, ApplicationConstants.EMPTY_STRING, Snackbar.LENGTH_INDEFINITE)
            val customSnackBarView = layoutInflater.inflate(R.layout.snackbar, null)
            val snackBarLayout = snackBar.view as SnackbarLayout
            snackBarLayout.setPadding(0, 0, 0, 0)

            val typeface = Typeface.createFromAsset(requireActivity().assets, ApplicationConstants.TypeFacePathConstants.ROBOTO_MEDIUM)

            val noticeField = customSnackBarView.findViewById<TextView>(R.id.snackbar_text)
            noticeField.setText(R.string.snackbar_no_forms_found)

            val dismissButton = customSnackBarView.findViewById<TextView>(R.id.snackbar_action_button)
            dismissButton.typeface = typeface
            dismissButton.setOnClickListener { snackBar.dismiss() }

            snackBarLayout.addView(customSnackBarView, 0)
            snackBar.show()
        } else {
            binding.formlist.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, forms)
        }
    }

    private fun startFormDisplayActivity(formName: String, patientId: Long, valueRefString: String, encounterType: String) {
        Intent(context, FormDisplayActivity::class.java).apply {
            putExtra(FORM_NAME, formName)
            putExtra(PATIENT_ID_BUNDLE, patientId)
            putExtra(VALUEREFERENCE, valueRefString)
            putExtra(ENCOUNTERTYPE, encounterType)
            startActivity(this)
        }
    }

    private fun startAdmissionFormActivity(formName: String, patientId: Long, encounterType: String) {
        Intent(context, FormAdmissionActivity::class.java).apply {
            putExtra(FORM_NAME, formName)
            putExtra(PATIENT_ID_BUNDLE, patientId)
            putExtra(ENCOUNTERTYPE, encounterType)
            startActivity(this)
        }
    }

    companion object {
        fun newInstance(patientId: Long) = FormListFragment().apply {
            arguments = bundleOf(PATIENT_ID_BUNDLE to patientId)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
