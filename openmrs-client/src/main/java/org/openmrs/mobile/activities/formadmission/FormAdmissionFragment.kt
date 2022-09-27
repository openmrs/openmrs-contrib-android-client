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
package org.openmrs.mobile.activities.formadmission

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.openmrs.android_sdk.library.OpenmrsAndroid
import com.openmrs.android_sdk.library.models.Result
import com.openmrs.android_sdk.library.models.ResultType.EncounterSubmissionLocalSuccess
import com.openmrs.android_sdk.library.models.ResultType.EncounterSubmissionSuccess
import com.openmrs.android_sdk.utilities.ApplicationConstants.BundleKeys.ENCOUNTERTYPE
import com.openmrs.android_sdk.utilities.ApplicationConstants.BundleKeys.FORM_NAME
import com.openmrs.android_sdk.utilities.ApplicationConstants.BundleKeys.PATIENT_ID_BUNDLE
import com.openmrs.android_sdk.utilities.ApplicationConstants.LOCATION
import com.openmrs.android_sdk.utilities.ToastUtil
import dagger.hilt.android.AndroidEntryPoint
import org.openmrs.mobile.R
import org.openmrs.mobile.activities.BaseFragment
import org.openmrs.mobile.databinding.FragmentFormAdmissionBinding
import org.openmrs.mobile.utilities.makeGone
import org.openmrs.mobile.utilities.makeVisible
import org.openmrs.mobile.utilities.observeOnce
import java.text.SimpleDateFormat
import java.util.Calendar

@AndroidEntryPoint
class FormAdmissionFragment : BaseFragment() {
    private var _binding: FragmentFormAdmissionBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FormAdmissionViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentFormAdmissionBinding.inflate(inflater, container, false)

        setupObserver()

        return binding.root
    }

    private fun setupObserver() = with(viewModel) {
        result.observe(viewLifecycleOwner, Observer { result ->
            when (result) {
                is Result.Loading -> showLoading(true)
                is Result.Success -> {
                    setupProviderSpinner(providers.keys.toList())
                    setupEncounterRoleSpinner(encounterRoles.keys.toList())
                    setupTargetLocationSpinner(targetLocations.keys.toList())
                    restoreState()
                    showLoading(false)
                }
                is Result.Error -> {
                    ToastUtil.error(getString(R.string.error_occurred))
                    showLoading(false)
                }
                else -> throw IllegalStateException()
            }
        })
    }

    private fun restoreState() = with(binding) {
        val currentDate = Calendar.getInstance().time
        @SuppressLint("SimpleDateFormat") val df = SimpleDateFormat("dd/MM/yyyy")

        admissionDateHeader.text = df.format(currentDate)
        submitButton.setOnClickListener { submitAdmission() }
        cancelButton.setOnClickListener { requireActivity().finish() }

        admittedBySpinner.setSelection(viewModel.providerListPosition)
        encounterRoleSpinner.setSelection(viewModel.encounterRoleListPosition)
        admittedToSpinner.setSelection(viewModel.targetLocationListPosition)

    }

    private fun showLoading(loading: Boolean) = with(binding) {
        if (loading) {
            transparentScreen.makeVisible()
            progressBar.makeVisible()
        } else {
            transparentScreen.makeGone()
            progressBar.makeGone()
        }
    }

    private fun setupProviderSpinner(providers: List<String>) = with(binding.admittedBySpinner) {
        adapter = ArrayAdapter(requireActivity(), android.R.layout.simple_list_item_1, providers)
        onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                viewModel.selectProvider(selectedItem.toString(), selectedItemPosition)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setupEncounterRoleSpinner(encounterRoles: List<String>) = with(binding.encounterRoleSpinner) {
        adapter = ArrayAdapter(requireActivity(), android.R.layout.simple_list_item_1, encounterRoles)
        onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                viewModel.selectEncounterRole(selectedItem.toString(), selectedItemPosition)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setupTargetLocationSpinner(locations: List<String>) = with(binding.admittedToSpinner) {
        adapter = ArrayAdapter(requireActivity(), android.R.layout.simple_list_item_1, locations)
        onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                viewModel.selectTargetLocation(selectedItem.toString(), selectedItemPosition)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun submitAdmission() {
        showLoading(true)
        viewModel.submitAdmission().observeOnce(viewLifecycleOwner, Observer { result ->
            when (result) {
                EncounterSubmissionSuccess -> {
                    ToastUtil.success(getString(R.string.form_submitted_successfully))
                    requireActivity().finish()
                }
                EncounterSubmissionLocalSuccess -> {
                    ToastUtil.notify(getString(R.string.form_data_sync_is_off_message))
                    requireActivity().finish()
                }
                else -> ToastUtil.error(getString(R.string.form_data_submit_error))
            }
            showLoading(false)
        })
    }

    companion object {
        fun newInstance(patientID: Long, encounterType: String,
                        formName: String, currentLocation: String = OpenmrsAndroid.getServerUrl()) = FormAdmissionFragment().apply {
            arguments = bundleOf(
                    PATIENT_ID_BUNDLE to patientID,
                    ENCOUNTERTYPE to encounterType,
                    FORM_NAME to formName,
                    LOCATION to currentLocation
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
