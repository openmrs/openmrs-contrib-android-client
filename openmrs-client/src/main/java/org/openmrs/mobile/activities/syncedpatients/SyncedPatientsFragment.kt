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
package org.openmrs.mobile.activities.syncedpatients

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.openmrs.android_sdk.library.models.Patient
import com.openmrs.android_sdk.library.models.Result
import dagger.hilt.android.AndroidEntryPoint
import org.openmrs.mobile.R
import org.openmrs.mobile.activities.BaseFragment
import org.openmrs.mobile.databinding.FragmentSyncedPatientsBinding
import org.openmrs.mobile.utilities.makeGone
import org.openmrs.mobile.utilities.makeInvisible
import org.openmrs.mobile.utilities.makeVisible
import java.util.ArrayList

@AndroidEntryPoint
class SyncedPatientsFragment : BaseFragment() {
    private var _binding: FragmentSyncedPatientsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SyncedPatientsViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSyncedPatientsBinding.inflate(inflater, container, false)

        val linearLayoutManager = LinearLayoutManager(this.activity)
        with(binding) {
            syncedPatientRecyclerView.setHasFixedSize(true)
            syncedPatientRecyclerView.layoutManager = linearLayoutManager
            syncedPatientRecyclerView.adapter = SyncedPatientsRecyclerViewAdapter(this@SyncedPatientsFragment, ArrayList())

            setupObserver()
            fetchSyncedPatients()

            swipeLayout.setOnRefreshListener {
                fetchSyncedPatients()
                swipeLayout.isRefreshing = false
            }
        }
        return binding.root
    }

    private fun setupObserver() {
        viewModel.result.observe(viewLifecycleOwner, Observer { result ->
            when (result) {
                is Result.Loading -> showLoading()
                is Result.Success -> showPatientsList(result.data)
                else -> showError()
            }
        })
    }

    fun fetchSyncedPatients() {
        viewModel.fetchSyncedPatients()
    }

    fun fetchSyncedPatients(query: String) {
        viewModel.fetchSyncedPatients(query)
    }

    fun deletePatient(patient: Patient) {
        viewModel.deleteSyncedPatient(patient)
    }

    private fun showLoading() {
        with(binding) {
            syncedPatientsInitialProgressBar.makeInvisible()
            syncedPatientRecyclerView.makeGone()
        }
    }

    private fun showPatientsList(patients: List<Patient>) {
        with(binding) {
            syncedPatientsInitialProgressBar.makeGone()
            if (patients.isEmpty()) {
                syncedPatientRecyclerView.makeGone()
                showEmptyListText()
            } else {
                (syncedPatientRecyclerView.adapter as SyncedPatientsRecyclerViewAdapter).updateList(patients)
                syncedPatientRecyclerView.makeVisible()
                hideEmptyListText()
            }
        }
    }

    private fun showError() {
        with(binding) {
            syncedPatientsInitialProgressBar.makeGone()
            syncedPatientRecyclerView.makeGone()
        }
        showEmptyListText()
    }

    private fun showEmptyListText() {
        binding.emptySyncedPatientList.makeVisible()
        binding.emptySyncedPatientList.text = getString(R.string.search_patient_no_results)
    }

    private fun hideEmptyListText() {
        binding.emptySyncedPatientList.makeGone()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(): SyncedPatientsFragment {
            return SyncedPatientsFragment()
        }
    }
}
