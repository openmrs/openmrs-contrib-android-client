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
import androidx.recyclerview.widget.LinearLayoutManager
import org.openmrs.mobile.R
import org.openmrs.mobile.activities.ACBaseFragment
import org.openmrs.mobile.databinding.FragmentMatchingPatientsBinding
import org.openmrs.mobile.models.Patient
import org.openmrs.mobile.utilities.DateUtils.convertTime
import org.openmrs.mobile.utilities.ToastUtil.error
import org.openmrs.mobile.utilities.ToastUtil.notify

class MatchingPatientsFragment : ACBaseFragment<MatchingPatientsContract.Presenter>(), MatchingPatientsContract.View {
    private lateinit var binding: FragmentMatchingPatientsBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentMatchingPatientsBinding.inflate(inflater, container, false)

        setListeners()

        return binding.root
    }

    private fun setListeners() {
        with(binding) {
            registerNewPatientButton.setOnClickListener { mPresenter!!.registerNewPatient() }
            mergePatientsButton.setOnClickListener { mPresenter!!.mergePatients() }
        }
    }

    override fun showPatientsData(patient: Patient, matchingPatients: List<Patient>) {
        setPatientInfo(patient)
        setMatchingPatients(patient, matchingPatients)
    }

    override fun enableMergeButton() {
        binding.mergePatientsButton.isEnabled = true
    }

    override fun disableMergeButton() {
        binding.mergePatientsButton.isEnabled = false
    }

    override fun notifyUser(stringId: Int) {
        notify(getString(stringId))
    }

    override fun finishActivity() {
        requireActivity().finish()
    }

    override fun showErrorToast(message: String?) {
        message?.let { error(it) }
    }

    private fun setMatchingPatients(patient: Patient, matchingPatients: List<Patient>) {
        with(binding) {
            recyclerView.layoutManager = LinearLayoutManager(context)
            recyclerView.adapter = MergePatientsRecycleViewAdapter(requireActivity(), mPresenter, matchingPatients, patient)
        }
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
                address1.visibility = View.GONE
                addr2Separator.visibility = View.GONE
                addr2Hint.visibility = View.GONE
            }
            if (patient.address.address2 != null) {
                address2.text = patient.address.address2
            } else {
                address2.visibility = View.GONE
                addr2Separator.visibility = View.GONE
                addr2Hint.visibility = View.GONE
            }
            if (patient.address.cityVillage != null) {
                cityAutoComplete.text = patient.address.cityVillage
            } else {
                cityAutoComplete.visibility = View.GONE
                citySeparator.visibility = View.GONE
                cityHint.visibility = View.GONE
            }
            if (patient.address.stateProvince != null) {
                stateAutoComplete.text = patient.address.stateProvince
            } else {
                stateAutoComplete.visibility = View.GONE
                stateSeparator.visibility = View.GONE
                stateHint.visibility = View.GONE
            }
            if (patient.address.country != null) {
                country.text = patient.address.country
            } else {
                country.visibility = View.GONE
                countrySeparator.visibility = View.GONE
                countryHint.visibility = View.GONE
            }
            if (patient.address.postalCode != null) {
                postalCode.text = patient.address.postalCode
            } else {
                postalCode.visibility = View.GONE
                postalCodeSeparator.visibility = View.GONE
                postalCodeHint.visibility = View.GONE
            }

        }
    }

    companion object {
        fun newInstance(): MatchingPatientsFragment {
            return MatchingPatientsFragment()
        }
    }
}