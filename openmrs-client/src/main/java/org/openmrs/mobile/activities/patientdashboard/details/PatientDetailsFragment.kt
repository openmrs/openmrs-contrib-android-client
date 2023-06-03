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
package org.openmrs.mobile.activities.patientdashboard.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.openmrs.android_sdk.library.models.OperationType.PatientFetching
import com.openmrs.android_sdk.library.models.Patient
import com.openmrs.android_sdk.library.models.Result
import com.openmrs.android_sdk.utilities.ApplicationConstants.BundleKeys.PATIENT_ID_BUNDLE
import com.openmrs.android_sdk.utilities.DateUtils.convertTime
import com.openmrs.android_sdk.utilities.StringUtils.notEmpty
import com.openmrs.android_sdk.utilities.StringUtils.notNull
import com.openmrs.android_sdk.utilities.ToastUtil.error
import dagger.hilt.android.AndroidEntryPoint
import org.openmrs.mobile.R
import org.openmrs.mobile.activities.BaseFragment
import org.openmrs.mobile.activities.patientdashboard.PatientDashboardActivity
import org.openmrs.mobile.databinding.FragmentPatientDetailsBinding
import org.openmrs.mobile.utilities.ImageUtils.showPatientPhoto
import org.openmrs.mobile.utilities.makeGone
import org.openmrs.mobile.utilities.makeVisible

@AndroidEntryPoint
class PatientDetailsFragment : BaseFragment() {
    private var _binding: FragmentPatientDetailsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PatientDashboardDetailsViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPatientDetailsBinding.inflate(inflater, null, false)

        setupObserver()
        fetchPatientDetails()

        return binding.root
    }

    private fun setupObserver() {
        viewModel.result.observe(viewLifecycleOwner, Observer { result ->
            when (result) {
                is Result.Loading -> {
                }
                is Result.Success -> {
                    when (result.operationType) {
                        PatientFetching -> showPatientDetails(result.data)
                        else -> {
                        }
                    }
                }
                is Result.Error -> {
                    when (result.operationType) {
                        PatientFetching -> error(getString(R.string.get_patient_from_database_error))
                        else -> {
                        }
                    }
                }
                else -> throw IllegalStateException()
            }

        })
    }

    private fun fetchPatientDetails() {
        viewModel.fetchPatientData()
    }

    private fun showPatientDetails(patient: Patient) {
        with(binding) {
            val patientIdForActionBar: String
            if (patient.identifier.identifier == null)
                patientIdForActionBar = patient.id.toString()
            else
                patientIdForActionBar = patient.identifier.identifier!!

            setMenuTitle(patient.name.nameString, patientIdForActionBar)
            if (isAdded) {
                if ("M" == patient.gender) {
                    patientDetailsGender.text = getString(R.string.male)
                } else {
                    patientDetailsGender.text = getString(R.string.female)
                }
            }
            if (patient.photo != null) {
                val photo = patient.resizedPhoto
                val patientName = patient.name.nameString
                patientPhoto.setImageBitmap(photo)
                patientPhoto.setOnClickListener { showPatientPhoto(requireContext(), photo, patientName) }
            }
            patientDetailsName.text = patient.name.nameString
            val longTime = convertTime(patient.birthdate)
            if (longTime != null) {
                patientDetailsBirthDate.text = convertTime(longTime)
            }
            patient.address?.let {
                addressDetailsStreet.text = it.addressString
                showAddressDetailsViewElement(addressDetailsStateLabel, addressDetailsState, it.stateProvince)
                showAddressDetailsViewElement(addressDetailsCountryLabel, addressDetailsCountry, it.country)
                showAddressDetailsViewElement(addressDetailsPostalCodeLabel, addressDetailsPostalCode, it.postalCode)
                showAddressDetailsViewElement(addressDetailsCityLabel, addressDetailsCity, it.cityVillage)
            }
            if (patient.isDeceased) {
                deceasedView.makeVisible()
                deceasedView.text = getString(R.string.marked_patient_deceased_successfully, patient.causeOfDeath.display)
            }
        }
    }

    private fun showAddressDetailsViewElement(detailsViewLabel: TextView, detailsView: TextView, detailsText: String?) {
        if (notNull(detailsText) && notEmpty(detailsText)) {
            detailsView.text = detailsText
        } else {
            detailsView.makeGone()
            detailsViewLabel.makeGone()
        }
    }

    private fun setMenuTitle(nameString: String, identifier: String) {
        (activity as PatientDashboardActivity).supportActionBar?.apply {
            title = nameString
            subtitle = "#$identifier"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(patientId: String): PatientDetailsFragment {
            val fragment = PatientDetailsFragment()
            fragment.arguments = bundleOf(Pair(PATIENT_ID_BUNDLE, patientId))
            return fragment
        }
    }
}
