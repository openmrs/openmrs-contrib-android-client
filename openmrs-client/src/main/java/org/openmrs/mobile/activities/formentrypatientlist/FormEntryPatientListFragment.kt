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
package org.openmrs.mobile.activities.formentrypatientlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.openmrs.android_sdk.library.models.Patient
import com.openmrs.android_sdk.library.models.Result
import com.openmrs.android_sdk.utilities.ToastUtil
import dagger.hilt.android.AndroidEntryPoint
import org.openmrs.mobile.R
import org.openmrs.mobile.activities.BaseFragment
import org.openmrs.mobile.databinding.FragmentFormEntryPatientListBinding
import org.openmrs.mobile.utilities.makeGone
import org.openmrs.mobile.utilities.makeVisible

@AndroidEntryPoint
class FormEntryPatientListFragment : BaseFragment() {
    private var _binding: FragmentFormEntryPatientListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FormEntryPatientListViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentFormEntryPatientListBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)

        setupAdapter()
        setupObserver()
        fetchPatientList()

        return binding.root
    }

    private fun setupAdapter() = with(binding.patientRecyclerView) {
        setHasFixedSize(true)
        layoutManager = LinearLayoutManager(activity)
        adapter = FormEntryPatientListAdapter(this@FormEntryPatientListFragment, emptyList())
        binding.swipeLayout.setOnRefreshListener {
            fetchPatientList()
            binding.swipeLayout.isRefreshing = false
        }
    }

    private fun setupObserver() {
        viewModel.result.observe(viewLifecycleOwner, Observer { result ->
            when (result) {
                is Result.Loading -> showLoading()
                is Result.Success -> showPatientList(result.data)
                is Result.Error -> showError()
                else -> throw IllegalStateException()
            }
        })
    }

    private fun fetchPatientList(query: String? = null) {
        viewModel.fetchSavedPatientsWithActiveVisits(query)
    }

    private fun showLoading() = with(binding) {
        formEntryListInitialProgressBar.makeVisible()
        patientRecyclerView.makeGone()
        emptyPatientList.makeGone()
    }

    private fun showPatientList(patients: List<Patient>) = with(binding) {
        formEntryListInitialProgressBar.makeGone()
        if (patients.isEmpty()) {
            emptyPatientList.text = if (viewModel.mQuery.isNullOrEmpty()) getString(R.string.search_visits_no_results)
            else getString(R.string.search_patient_no_result_for_query, viewModel.mQuery)
            patientRecyclerView.makeGone()
            emptyPatientList.makeVisible()
        } else {
            (patientRecyclerView.adapter as FormEntryPatientListAdapter).updateList(patients)
            patientRecyclerView.makeVisible()
            emptyPatientList.makeGone()
        }
    }

    private fun showError() = with(binding) {
        formEntryListInitialProgressBar.makeGone()
        patientRecyclerView.makeGone()
        emptyPatientList.makeGone()
        ToastUtil.error(getString(R.string.search_visits_no_results))
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.form_entry_patient_list_menu, menu)
        val searchView = menu.findItem(R.id.actionSearchRemoteFormEntry).actionView as SearchView

        // Search function
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(query: String): Boolean {
                fetchPatientList(query)
                return true
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(): FormEntryPatientListFragment {
            return FormEntryPatientListFragment()
        }
    }
}
