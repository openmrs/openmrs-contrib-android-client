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
package org.openmrs.mobile.activities.addeditprovider

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.openmrs.android_sdk.library.models.OperationType.ProviderRegistering
import com.openmrs.android_sdk.library.models.OperationType.ProviderUpdating
import com.openmrs.android_sdk.library.models.Provider
import com.openmrs.android_sdk.library.models.Result
import com.openmrs.android_sdk.library.models.ResultType.AddProviderLocalSuccess
import com.openmrs.android_sdk.library.models.ResultType.AddProviderSuccess
import com.openmrs.android_sdk.library.models.ResultType.UpdateProviderLocalSuccess
import com.openmrs.android_sdk.library.models.ResultType.UpdateProviderSuccess
import com.openmrs.android_sdk.utilities.ApplicationConstants.BundleKeys.PROVIDER_BUNDLE
import com.openmrs.android_sdk.utilities.StringUtils.ILLEGAL_CHARACTERS
import com.openmrs.android_sdk.utilities.StringUtils.validateText
import com.openmrs.android_sdk.utilities.ToastUtil
import dagger.hilt.android.AndroidEntryPoint
import org.openmrs.mobile.R
import org.openmrs.mobile.activities.BaseFragment
import org.openmrs.mobile.activities.providermanagerdashboard.ProviderManagerDashboardViewModel
import org.openmrs.mobile.databinding.FragmentAddProviderBinding
import org.openmrs.mobile.utilities.ViewUtils.getInput
import org.openmrs.mobile.utilities.ViewUtils.isEmpty

@AndroidEntryPoint
class AddEditProviderFragment : BaseFragment() {
    private var _binding: FragmentAddProviderBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AddEditProviderViewModel by viewModels()
    private val matchingProvidersViewModel: ProviderManagerDashboardViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAddProviderBinding.inflate(inflater, container, false)

        restoreState()
        setupObserver()
        setupListeners()

        return binding.root
    }

    private fun restoreState() {
        if (!viewModel.isUpdateProvider) return
        with(viewModel.provider!!) {
            binding.firstNameEditText.setText(person!!.name.givenName)
            binding.lastNameEditText.setText(person!!.name.familyName)
            binding.identifierEditText.setText(identifier)
        }
    }

    private fun setupObserver() {
        matchingProvidersViewModel.result.observe(viewLifecycleOwner, Observer { result ->
            when (result) {
                is Result.Loading -> binding.submitButton.isEnabled = false
                is Result.Success -> {
                    binding.submitButton.isEnabled = true
                    if (result.data.isEmpty()) submitProvider()
                    else showMatchingProvidersDialog(result.data)
                }
                is Result.Error -> {
                }
                else -> throw IllegalStateException()
            }
        })
        viewModel.result.observe(viewLifecycleOwner, Observer { result ->
            when (result) {
                is Result.Loading -> binding.submitButton.isEnabled = false
                is Result.Success -> {
                    val messageResId = when (result.data) {
                        AddProviderLocalSuccess -> R.string.offline_provider_add
                        AddProviderSuccess -> R.string.add_provider_success_msg
                        UpdateProviderLocalSuccess -> R.string.offline_provider_edit
                        UpdateProviderSuccess -> R.string.edit_provider_success_msg
                        else -> throw IllegalStateException()
                    }
                    ToastUtil.success(getString(messageResId))
                    requireActivity().finish()
                }
                is Result.Error -> {
                    val messageResId = when (result.operationType) {
                        ProviderRegistering -> R.string.add_provider_failure_msg
                        ProviderUpdating -> R.string.edit_provider_failure_msg
                        else -> throw IllegalStateException()
                    }
                    ToastUtil.error(getString(messageResId))
                    binding.submitButton.isEnabled = true
                }
                else -> throw IllegalStateException()
            }
        })
    }

    private fun setupListeners() = with(binding) {
        submitButton.setOnClickListener { fetchMatchingProviders() }
        cancelButton.setOnClickListener { requireActivity().finish() }
    }

    private fun fetchMatchingProviders() {
        if (validateFields()) matchingProvidersViewModel.fetchProviders(viewModel.provider!!.person!!.display!!)
    }

    private fun submitProvider() {
        if (validateFields()) viewModel.submitProvider()
    }

    private fun validateFields(): Boolean = with(binding) {
        val emptyError = getString(R.string.emptyerror)

        // Invalid characters for given name only
        val givenNameError = getString(R.string.fname_invalid_error)
        // Invalid family name
        val familyNameError = getString(R.string.lname_invalid_error)

        // First name validation
        if (isEmpty(firstNameEditText)) {
            firstNameTextLayout.isErrorEnabled = true
            firstNameTextLayout.error = emptyError
            return false
        } else if (!validateText(getInput(firstNameEditText), ILLEGAL_CHARACTERS)) {
            firstNameTextLayout.isErrorEnabled = true
            firstNameTextLayout.error = givenNameError
            return false
        } else {
            firstNameTextLayout.isErrorEnabled = false
        }

        // Family name validation
        if (isEmpty(lastNameEditText)) {
            lastNameTextLayout.isErrorEnabled = true
            lastNameTextLayout.error = emptyError
            return false
        } else if (!validateText(getInput(lastNameEditText), ILLEGAL_CHARACTERS)) {
            lastNameTextLayout.isErrorEnabled = true
            lastNameTextLayout.error = familyNameError
            return false
        } else {
            lastNameTextLayout.isErrorEnabled = false
        }

        // identifier validation
        if (isEmpty(identifierEditText)) {
            identifierTextLayout.isErrorEnabled = true
            identifierTextLayout.error = emptyError
            return false
        } else {
            identifierTextLayout.isErrorEnabled = false
        }

        // All valid. Update provider data in the ViewModel.
        val firstName = firstNameEditText.text.toString()
        val lastName = lastNameEditText.text.toString()
        val identifier = identifierEditText.text.toString()
        viewModel.initializeProvider(firstName, lastName, identifier)

        return true
    }

    private fun showMatchingProvidersDialog(matchingProviders: List<Provider>) {
        val dialogView = layoutInflater.inflate(R.layout.custom_matching_provider_alert_dialog, null)
        dialogView.findViewById<RecyclerView>(R.id.custom_matching_provider_rv).apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(activity)
            adapter = MatchingProviderRecyclerViewAdapter(activity, matchingProviders)
        }

        AlertDialog.Builder(requireContext())
                .setTitle(R.string.title_dialog_matching_provider)
                .setView(dialogView)
                .setPositiveButton(R.string.dialog_matching_provider_positive_btn) { _, _ -> submitProvider() }
                .setNegativeButton(R.string.dialog_button_cancel) { _, _ -> }
                .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(providerToEdit: Provider?): AddEditProviderFragment = AddEditProviderFragment().apply {
            arguments = bundleOf(PROVIDER_BUNDLE to providerToEdit)
        }
    }
}
