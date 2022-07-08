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
package org.openmrs.mobile.activities.matchingpatients

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.openmrs.android_sdk.library.models.OperationType.PatientMerging
import com.openmrs.android_sdk.library.models.OperationType.PatientRegistering
import com.openmrs.android_sdk.library.models.Patient
import com.openmrs.android_sdk.library.models.Result.Success
import com.openmrs.android_sdk.library.models.Result.Error
import com.openmrs.android_sdk.utilities.ApplicationConstants
import com.openmrs.android_sdk.utilities.DateUtils.convertTime
import com.openmrs.android_sdk.utilities.PatientAndMatchingPatients
import com.openmrs.android_sdk.utilities.ToastUtil.error
import com.openmrs.android_sdk.utilities.ToastUtil.success
import dagger.hilt.android.AndroidEntryPoint
import org.openmrs.mobile.R
import org.openmrs.mobile.activities.BaseFragment
import org.openmrs.mobile.databinding.FragmentMatchingPatientsBinding
import org.openmrs.mobile.utilities.makeGone
import java.util.Queue

@AndroidEntryPoint
class MatchingPatientsFragment : BaseFragment() {
    private var _binding: FragmentMatchingPatientsBinding? = null
    private val binding get() = _binding!!

    private lateinit var matchesAdapter: MergePatientsRecycleViewAdapter

    private val viewModel: MatchingPatientsViewModel by viewModels()

    lateinit var allPatientsAndMatches: Queue<PatientAndMatchingPatients>
    lateinit var currentPatientAndMatches: PatientAndMatchingPatients

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMatchingPatientsBinding.inflate(inflater, container, false)

        allPatientsAndMatches = arguments?.get(ApplicationConstants.BundleKeys.PATIENTS_AND_MATCHES) as Queue<PatientAndMatchingPatients>

        setupAdapter()
        showNextPatientsDataOrFinish()
        setListeners()
        setupObserver()

        return binding.root
    }

    private fun setupAdapter() {
        matchesAdapter = MergePatientsRecycleViewAdapter(activity, emptyList(), Patient())
        with(binding.recyclerView){
            layoutManager = LinearLayoutManager(context)
            adapter = matchesAdapter
        }
    }

    private fun showNextPatientsDataOrFinish() {
        if (allPatientsAndMatches.isNotEmpty()) {
            with(allPatientsAndMatches.poll()!!) {
                currentPatientAndMatches = this
                setPatientInfo(patient)
                setMatchingPatients(patient, matchingPatientList)
            }
        } else {
            finishActivity()
        }
    }

    private fun setListeners() {
        with(binding) {
            registerNewPatientButton.setOnClickListener { viewModel.registerNewPatient(currentPatientAndMatches.patient) }
            mergePatientsButton.setOnClickListener {
                if (matchesAdapter.selectedPatient == null) error(getString(R.string.no_patient_selected))
                else viewModel.mergePatients(matchesAdapter.selectedPatient, currentPatientAndMatches.patient)
            }
        }
    }

    private fun setupObserver() {
        viewModel.result.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Success -> {
                    when (it.operationType) {
                        PatientRegistering -> success(getString(R.string.patient_register_success))
                        PatientMerging -> success(getString(R.string.patient_merge_success))
                    }
                    showNextPatientsDataOrFinish()
                }
                is Error -> {
                    when (it.operationType) {
                        PatientRegistering -> error(getString(R.string.patient_register_fail))
                        PatientMerging -> error(getString(R.string.patient_merge_fail))
                    }
                    showNextPatientsDataOrFinish()
                }
            }
        })
    }

    private fun setMatchingPatients(patient: Patient, matchingPatients: List<Patient>) {
        matchesAdapter.updateList(patient, matchingPatients)
    }

    private fun setPatientInfo(patient: Patient) {
        with(binding) {
            givenName.text = patient.name.givenName
            middleName.text = patient.name.middleName
            familyName.text = patient.name.familyName
            if ("M" == patient.gender) {
                gender.text = getString(R.string.male)
            } else {
                gender.text = getString(R.string.female)
            }
            birthDate.text = convertTime(convertTime(patient.birthdate)!!)
            if (patient.address.address1 != null) {
                address1.text = patient.address.address1
            } else {
                address1.makeGone()
                addr2Separator.makeGone()
                addr2Hint.makeGone()
            }
            if (patient.address.address2 != null) {
                address2.text = patient.address.address2
            } else {
                address2.makeGone()
                addr2Separator.makeGone()
                addr2Hint.makeGone()
            }
            if (patient.address.cityVillage != null) {
                cityAutoComplete.text = patient.address.cityVillage
            } else {
                cityAutoComplete.makeGone()
                citySeparator.makeGone()
                cityHint.makeGone()
            }
            if (patient.address.stateProvince != null) {
                stateAutoComplete.text = patient.address.stateProvince
            } else {
                stateAutoComplete.makeGone()
                stateSeparator.makeGone()
                stateHint.makeGone()
            }
            if (patient.address.country != null) {
                country.text = patient.address.country
            } else {
                country.makeGone()
                countrySeparator.makeGone()
                countryHint.makeGone()
            }
            if (patient.address.postalCode != null) {
                postalCode.text = patient.address.postalCode
            } else {
                postalCode.makeGone()
                postalCodeSeparator.makeGone()
                postalCodeHint.makeGone()
            }
        }
    }

    private fun finishActivity() {
        requireActivity().finish()
    }

    companion object {
        fun newInstance(matchingPatientsList: Queue<PatientAndMatchingPatients>): MatchingPatientsFragment {
            val fragment = MatchingPatientsFragment()
            fragment.arguments = bundleOf(Pair(ApplicationConstants.BundleKeys.PATIENTS_AND_MATCHES, matchingPatientsList))
            return fragment
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
