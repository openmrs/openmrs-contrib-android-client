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
package org.openmrs.mobile.activities.lastviewedpatients

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.openmrs.android_sdk.library.models.OperationType.LastViewedPatientsFetching
import com.openmrs.android_sdk.library.models.OperationType.PatientSearching
import com.openmrs.android_sdk.library.models.Patient
import com.openmrs.android_sdk.library.models.Result
import com.openmrs.android_sdk.utilities.ApplicationConstants
import com.openmrs.android_sdk.utilities.NetworkUtils.hasNetwork
import com.openmrs.android_sdk.utilities.ToastUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_last_viewed_patients.view.*
import kotlinx.android.synthetic.main.snackbar.view.*
import org.openmrs.mobile.R
import org.openmrs.mobile.activities.BaseFragment
import org.openmrs.mobile.activities.patientdashboard.PatientDashboardActivity
import org.openmrs.mobile.databinding.FragmentLastViewedPatientsBinding
import org.openmrs.mobile.utilities.makeGone
import org.openmrs.mobile.utilities.makeVisible
import java.util.ArrayList

@AndroidEntryPoint
class LastViewedPatientsFragment : BaseFragment() {
    private var _binding: FragmentLastViewedPatientsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LastViewedPatientsViewModel by viewModels()

    private lateinit var mAdapter: LastViewedPatientRecyclerViewAdapter

    private val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val isLoading = viewModel.result.value is Result.Loading
            if (!recyclerView.canScrollVertically(1) && !isLoading) fetchPatients()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentLastViewedPatientsBinding.inflate(inflater, container, false)

        setupAdapter()
        setupObserver()
        fetchPatients()
        setupSwipeRefresh()

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        ToastUtil.notify(getString(R.string.use_search_to_find_patients))
    }

    private fun setupAdapter() {
        binding.lastViewedPatientRecyclerView.layoutManager = LinearLayoutManager(activity)
        resetLastViewedPatients()
    }

    private fun resetLastViewedPatients() {
        mAdapter = LastViewedPatientRecyclerViewAdapter(activity, ArrayList(), this@LastViewedPatientsFragment)
        binding.lastViewedPatientRecyclerView.adapter = mAdapter
        viewModel.resetPagination()
    }

    private fun setupObserver() {
        viewModel.result.observe(viewLifecycleOwner, Observer { result ->
            when (result) {
                is Result.Loading -> {
                    when (result.operationType) {
                        LastViewedPatientsFetching -> showLoadingPatients()
                        PatientSearching -> showSearchingForPatient()
                    }
                }
                is Result.Success -> {
                    when (result.operationType) {
                        LastViewedPatientsFetching -> showMorePatients(result.data)
                        PatientSearching -> showPatientSearchResult(result.data)
                    }
                }
                is Result.Error -> {
                    when (result.operationType) {
                        LastViewedPatientsFetching -> showErrorFetchingPatients()
                        PatientSearching -> showPatientSearchError()
                    }
                }
                else -> throw IllegalStateException()
            }
        })
    }

    private fun fetchPatients(query: String? = null) {
        with(binding.lastViewedPatientRecyclerView) {
            if (query == null) {
                // Last viewed patients will be paginated
                if (viewModel.startIndex == 0) {
                    setupViewsForLastViewedPatients()
                    addOnScrollListener(scrollListener)
                }
                viewModel.fetchLastViewedPatients()
            } else {
                // Patient searching results will not be paginated
                clearOnScrollListeners()
                viewModel.fetchPatients(query)
            }
        }
    }

    private fun setupSwipeRefresh() {
        binding.swiperefreshLastPatients.setOnRefreshListener {
            if (hasNetwork()) {
                mAdapter.finishActionMode()
                resetLastViewedPatients()
                fetchPatients()
            } else {
                binding.swiperefreshLastPatients.isRefreshing = false
                ToastUtil.error(getString(R.string.no_internet_connection_message))
            }
        }
    }

    private fun setupViewsForLastViewedPatients() {
        with(binding) {
            lastViewedPatientRecyclerView.makeVisible()
            emptyLastViewedPatientList.makeGone()
        }
    }

    private fun showLoadingPatients() {
        with(binding) {
            swiperefreshLastPatients.isEnabled = false
            mAdapter.showLoadingMore()
            lastViewedPatientRecyclerView.smoothScrollToPosition(mAdapter.itemCount - 1)
        }
    }

    private fun showMorePatients(patients: List<Patient>) {
        with(binding) {
            swiperefreshLastPatients.isRefreshing = false
            swiperefreshLastPatients.isEnabled = true
        }
        mAdapter.hideLoadingMore()
        mAdapter.addMoreToList(patients)
    }

    private fun showErrorFetchingPatients() {
        with(binding) {
            swiperefreshLastPatients.isRefreshing = false
            swiperefreshLastPatients.isEnabled = true
        }
        mAdapter.hideLoadingMore()
        ToastUtil.error(getString(R.string.fetch_patients_remote_error))
    }

    private fun showSearchingForPatient() {
        with(binding) {
            swiperefreshLastPatients.isEnabled = false
            lastViewedPatientRecyclerView.makeGone()
            emptyLastViewedPatientList.makeGone()
            patientRecyclerViewLoading.makeVisible()
        }
    }

    private fun showPatientSearchResult(patientList: List<Patient>) {
        with(binding) {
            if (patientList.isEmpty()) {
                emptyLastViewedPatientList.makeVisible()
                lastViewedPatientRecyclerView.makeGone()
            } else {
                emptyLastViewedPatientList.makeGone()
                lastViewedPatientRecyclerView.makeVisible()
                mAdapter.updateList(patientList)
            }
            swiperefreshLastPatients.isRefreshing = false
            swiperefreshLastPatients.isEnabled = true
            patientRecyclerViewLoading.makeGone()
        }
    }

    private fun showPatientSearchError() {
        ToastUtil.error(getString(R.string.search_patient_remote_error))
        with(binding) {
            swiperefreshLastPatients.isRefreshing = false
            swiperefreshLastPatients.isEnabled = true
            patientRecyclerViewLoading.makeGone()
        }
    }

    fun showOpenPatientSnackbar(patientId: Long) {
        val frameLayout = binding.swiperefreshLastPatients.swipe_container
        Snackbar.make(frameLayout, getString(R.string.snackbar_info_patient_downloaded), Snackbar.LENGTH_LONG)
                .apply {
                    setActionTextColor(Color.WHITE)
                    view.snackbar_text.setTextColor(Color.WHITE)
                    setAction(getString(R.string.snackbar_action_open)) { openPatientDashboardActivity(patientId) }
                }
                .show()
    }

    private fun openPatientDashboardActivity(patientId: Long) {
        Intent(context, PatientDashboardActivity::class.java).apply {
            putExtra(ApplicationConstants.BundleKeys.PATIENT_ID_BUNDLE, patientId)
            startActivity(this)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.find_patients_remote_menu, menu)
        val searchPatientView = menu.findItem(R.id.actionSearchRemote).actionView as SearchView

        // Search function
        searchPatientView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                fetchPatients(query)
                return true
            }

            override fun onQueryTextChange(query: String): Boolean {
                // Nothing to do
                return true
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> requireActivity().onBackPressed()
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        // ViewModel loads duplicate data if screen rotated while fetching a portion of paginated patients
        viewModelStore.clear()
    }

    companion object {
        fun newInstance(): LastViewedPatientsFragment {
            return LastViewedPatientsFragment()
        }
    }
}
