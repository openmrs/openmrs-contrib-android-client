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
package org.openmrs.mobile.activities.patientdashboard.vitals

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.openmrs.android_sdk.library.models.Encounter
import com.openmrs.android_sdk.library.models.OperationType.PatientVitalsFetching
import com.openmrs.android_sdk.library.models.Result
import com.openmrs.android_sdk.utilities.ApplicationConstants.BundleKeys.ENCOUNTERTYPE
import com.openmrs.android_sdk.utilities.ApplicationConstants.BundleKeys.ENCOUNTER_UUID
import com.openmrs.android_sdk.utilities.ApplicationConstants.BundleKeys.FORM_FIELDS_LIST_BUNDLE
import com.openmrs.android_sdk.utilities.ApplicationConstants.BundleKeys.FORM_NAME
import com.openmrs.android_sdk.utilities.ApplicationConstants.BundleKeys.PATIENT_ID_BUNDLE
import com.openmrs.android_sdk.utilities.ApplicationConstants.BundleKeys.VALUEREFERENCE
import com.openmrs.android_sdk.utilities.DateUtils
import com.openmrs.android_sdk.utilities.DateUtils.convertTime
import com.openmrs.android_sdk.utilities.ToastUtil.notify
import dagger.hilt.android.AndroidEntryPoint
import org.openmrs.mobile.R
import org.openmrs.mobile.activities.BaseFragment
import org.openmrs.mobile.activities.formdisplay.FormDisplayActivity
import org.openmrs.mobile.application.OpenMRSInflater
import org.openmrs.mobile.bundle.FormFieldsWrapper
import org.openmrs.mobile.databinding.FragmentPatientVitalsBinding
import org.openmrs.mobile.utilities.makeGone
import org.openmrs.mobile.utilities.makeVisible
import org.openmrs.mobile.utilities.observeOnce

@AndroidEntryPoint
class PatientVitalsFragment : BaseFragment() {
    private var _binding: FragmentPatientVitalsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PatientDashboardVitalsViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPatientVitalsBinding.inflate(inflater, null, false)

        setupObserver()

        binding.formEditIcon.setOnClickListener { fetchEncounterAndStartFormEdit() }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        fetchLastVitals()
    }

    private fun setupObserver() {
        viewModel.result.observe(viewLifecycleOwner, Observer { result ->
            when (result) {
                is Result.Loading -> {
                }
                is Result.Success -> {
                    when (result.operationType) {
                        PatientVitalsFetching -> showEncounterVitals(result.data)
                        else -> {
                        }
                    }
                }
                is Result.Error -> {
                    showNoVitals()
                }
                else -> throw IllegalStateException()
            }
        })
    }

    private fun fetchLastVitals() {
        viewModel.fetchLastVitals()
    }

    private fun fetchEncounterAndStartFormEdit() {
        viewModel.fetchLastVitalsEncounter().observeOnce(viewLifecycleOwner, Observer { encounter ->
            if (encounter != null) startFormDisplayActivity(encounter)
        })
    }

    private fun showEncounterVitals(encounter: Encounter) {
        if (encounter.observations.isNotEmpty()) {
            with(binding) {
                lastVitalsDate.text = convertTime(encounter.encounterDatetime!!, DateUtils.DATE_WITH_TIME_FORMAT)
                vitalsDetailsContent.removeAllViews()
                val openMRSInflater = OpenMRSInflater(layoutInflater)
                for (obs in encounter.observations) {
                    openMRSInflater.addKeyValueStringView(vitalsDetailsContent, obs.display, obs.displayValue)
                }
            }
        } else {
            showNoVitals()
        }
    }

    private fun showNoVitals() {
        with(binding) {
            lastVitalsLayout.makeGone()
            vitalsDetailsContent.makeGone()
            lastVitalsNoneLabel.makeVisible()
        }
    }

    private fun startFormDisplayActivity(encounter: Encounter) {
        val form = encounter.form
        if (form != null) {
            val intent = Intent(context, FormDisplayActivity::class.java)
            intent.putExtra(FORM_NAME, form.name)
            intent.putExtra(PATIENT_ID_BUNDLE, encounter.patient!!.id)
            intent.putExtra(VALUEREFERENCE, form.valueReference)
            intent.putExtra(ENCOUNTER_UUID, encounter.uuid)
            intent.putExtra(ENCOUNTERTYPE, encounter.encounterType!!.uuid)
            intent.putExtra(FORM_FIELDS_LIST_BUNDLE, FormFieldsWrapper.create(encounter))
            startActivity(intent)
        } else {
            notify(getString(R.string.form_error))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(patientId: String): PatientVitalsFragment {
            val fragment = PatientVitalsFragment()
            fragment.arguments = bundleOf(Pair(PATIENT_ID_BUNDLE, patientId))
            return fragment
        }
    }
}
