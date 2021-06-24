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
package org.openmrs.mobile.activities.providermanagerdashboard.addprovider

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.openmrs.mobile.R
import org.openmrs.mobile.activities.ACBaseFragment
import org.openmrs.mobile.databinding.FragmentAddProviderBinding
import org.openmrs.mobile.models.Provider
import org.openmrs.mobile.utilities.ApplicationConstants
import org.openmrs.mobile.utilities.ViewUtils
import org.openmrs.mobile.utilities.ViewUtils.getInput
import org.openmrs.mobile.utilities.ViewUtils.isEmpty
import org.openmrs.mobile.utilities.ViewUtils.validateText

class AddProviderFragment : ACBaseFragment<AddProviderContract.Presenter?>(), AddProviderContract.View {
    private var editProvider: Provider? = null
    private var existingProviders: ArrayList<Provider?>? = null
    private var _binding: FragmentAddProviderBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = FragmentAddProviderBinding.inflate(inflater, container, false)
        editProvider = requireActivity().intent
                .getSerializableExtra(ApplicationConstants.BundleKeys.PROVIDER_ID_BUNDLE) as? Provider
        existingProviders = requireActivity().intent
                .getSerializableExtra(ApplicationConstants.BundleKeys.EXISTING_PROVIDERS_BUNDLE) as ArrayList<Provider?>?
        setupUI()
        return binding.root
    }

    private fun setupUI() {
        with(binding) {
        if (editProvider != null) {
            var displayName = editProvider!!.person!!.display
            val firstName: String?
            val lastName: String?
            if (displayName == null) {
                firstName = editProvider!!.person!!.name.givenName
                lastName = editProvider!!.person!!.name.familyName
                displayName = "$firstName $lastName"
            } else {
                firstName = displayName.substring(0, displayName.indexOf(' '))
                lastName = displayName.substring(displayName.lastIndexOf(' ') + 1)
            }
            firstNameEditText.setText(firstName)
            lastNameEditText.setText(lastName)
            identifierEditText.setText(editProvider!!.identifier)
            editProvider!!.person!!.display = displayName
        }
        submitButton.setOnClickListener { v: View? ->
            if (validateFields()) {
                val firstName = firstNameEditText.text!!.toString()
                val lastName = lastNameEditText.text!!.toString()
                val identifier = identifierEditText.text!!.toString()
                val person = mPresenter!!.createPerson(firstName, lastName)
                val provider: Provider
                if (editProvider == null) {
                    provider = mPresenter!!.createNewProvider(person, identifier)!!
                    val matchingProviders = mPresenter!!.getMatchingProviders(existingProviders, provider)
                    if (matchingProviders?.size!! > 0) {
                        showMatchingProvidersDialog(matchingProviders, provider)
                    } else {
                        setProviderResult(provider)
                    }
                } else {
                    provider = mPresenter!!.editExistingProvider(editProvider, person, identifier)!!
                    setProviderResult(provider)
                }
            }
        }
        cancelButton.setOnClickListener { v: View? -> requireActivity().finish() }
    }
    }

    override fun validateFields(): Boolean {
        with(binding) {
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
            } else if (!validateText(getInput(firstNameEditText), ViewUtils.ILLEGAL_CHARACTERS)) {
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
            } else if (!validateText(getInput(lastNameEditText), ViewUtils.ILLEGAL_CHARACTERS)) {
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
            return true
        }
    }

    override fun setPresenter(presenter: AddProviderContract.Presenter?) {
        mPresenter = presenter
    }

    private fun showMatchingProvidersDialog(matchingProviders: List<Provider?>?, provider: Provider?) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(requireActivity().getString(R.string.title_dialog_matching_provider))
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.custom_matching_provider_alert_dialog, null)
        builder.setView(dialogView)
        val recyclerView: RecyclerView = dialogView.findViewById(R.id.custom_matching_provider_rv)
        recyclerView.setHasFixedSize(true)
        val linearLayoutManager = LinearLayoutManager(activity)
        recyclerView.layoutManager = linearLayoutManager
        val adapter = MatchingProviderRecyclerViewAdapter(requireActivity(), matchingProviders as List<Provider>)
        recyclerView.adapter = adapter
        builder.setPositiveButton(requireActivity().getString(R.string.dialog_matching_provider_positive_btn)) { dialog: DialogInterface?, which: Int -> setProviderResult(provider) }.setNegativeButton(requireActivity().getString(R.string.dialog_button_cancel)) { dialog: DialogInterface?, which: Int -> }
        val dialog = builder.create()
        dialog.show()
    }

    /**
     * This will set the Intent result with new/existing providers
     *
     * @param provider
     */
    private fun setProviderResult(provider: Provider?) {
        val intent = Intent()
        intent.putExtra(ApplicationConstants.BundleKeys.PROVIDER_ID_BUNDLE, provider)
        requireActivity().setResult(Activity.RESULT_OK, intent)
        requireActivity().finish()
    }

    companion object {
        fun newInstance(): AddProviderFragment {
            return AddProviderFragment()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}