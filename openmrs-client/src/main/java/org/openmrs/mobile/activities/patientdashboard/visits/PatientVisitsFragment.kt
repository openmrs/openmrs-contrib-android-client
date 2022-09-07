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
package org.openmrs.mobile.activities.patientdashboard.visits

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.openmrs.android_sdk.library.models.OperationType.PatientVisitStarting
import com.openmrs.android_sdk.library.models.OperationType.PatientVisitsFetching
import com.openmrs.android_sdk.library.models.Result
import com.openmrs.android_sdk.library.models.Visit
import com.openmrs.android_sdk.utilities.ApplicationConstants
import com.openmrs.android_sdk.utilities.ApplicationConstants.BundleKeys.PATIENT_ID_BUNDLE
import com.openmrs.android_sdk.utilities.NetworkUtils.isOnline
import com.openmrs.android_sdk.utilities.ToastUtil.error
import com.openmrs.android_sdk.utilities.ToastUtil.notify
import dagger.hilt.android.AndroidEntryPoint
import org.openmrs.mobile.R
import org.openmrs.mobile.activities.BaseFragment
import org.openmrs.mobile.activities.patientdashboard.PatientDashboardActivity
import org.openmrs.mobile.activities.visitdashboard.VisitDashboardActivity
import org.openmrs.mobile.databinding.FragmentPatientVisitBinding
import org.openmrs.mobile.utilities.makeGone
import org.openmrs.mobile.utilities.makeVisible
import org.openmrs.mobile.utilities.observeOnce

@AndroidEntryPoint
class PatientVisitsFragment : BaseFragment() {
    private var _binding: FragmentPatientVisitBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PatientDashboardVisitsViewModel by viewModels()

    private lateinit var patientDashboardActivity: PatientDashboardActivity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        patientDashboardActivity = context as PatientDashboardActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPatientVisitBinding.inflate(inflater, null, false)

        setupAdapter()
        setupObserver()


        return binding.root
    }

    override fun onResume() {
        super.onResume()
        fetchPatientVisits()
    }

    private fun setupAdapter() {
        with(binding.patientVisitRecyclerView) {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(activity)
            adapter = PatientVisitsRecyclerViewAdapter(this@PatientVisitsFragment, emptyList())
        }
    }

    private fun setupObserver() {
        viewModel.result.observe(viewLifecycleOwner, Observer { result ->
            when (result) {
                is Result.Loading -> {
                    when (result.operationType) {
                        PatientVisitStarting -> showStartVisitProgressDialog()
                        else -> {
                        }
                    }
                }
                is Result.Success -> {
                    dismissCurrentDialog()
                    when (result.operationType) {
                        PatientVisitsFetching -> showVisitsList(result.data)
                        PatientVisitStarting -> goToVisitDashboard(result.data[0].id!!)
                        else -> {
                        }
                    }
                }
                is Result.Error -> {
                    dismissCurrentDialog()
                    when (result.operationType) {
                        PatientVisitsFetching -> showErrorFetchingVisits()
                        PatientVisitStarting -> error(getString(R.string.visit_start_error))
                        else -> {
                        }
                    }
                }
                else -> throw IllegalStateException()
            }
        })
    }

    private fun fetchPatientVisits() {
        viewModel.fetchVisitsData()
    }

    fun startVisit() {
        viewModel.startVisit()
    }

    private fun showVisitsList(visits: List<Visit>) {
        with(binding) {
            if (visits.isEmpty()) {
                patientVisitRecyclerView.makeGone()
                emptyVisitsList.makeVisible()
            } else {
                (binding.patientVisitRecyclerView.adapter as PatientVisitsRecyclerViewAdapter).updateList(visits)
                patientVisitRecyclerView.makeVisible()
                emptyVisitsList.makeGone()
            }
        }
    }

    private fun showErrorFetchingVisits() {
        with(binding) {
            patientVisitRecyclerView.makeGone()
            emptyVisitsList.makeVisible()
            emptyVisitsList.text = getString(R.string.get_patient_from_database_error)
            error(getString(R.string.get_patient_from_database_error))
        }
    }

    private fun showStartVisitStatus() {
        if (!isOnline()) notify(getString(R.string.offline_mode_not_supported))
        else if (viewModel.getPatient().isDeceased) {
            error(getString(R.string.cannot_start_visit_for_deceased))
        } else {
            viewModel.hasActiveVisit().observeOnce(viewLifecycleOwner, Observer { hasActiveVisit ->
                with(patientDashboardActivity.supportActionBar!!) {
                    if (hasActiveVisit) patientDashboardActivity.showStartVisitImpossibleDialog(title)
                    else patientDashboardActivity.showStartVisitDialog(title)
                }
            })
        }
    }

    private fun showStartVisitProgressDialog() {
        patientDashboardActivity.showProgressDialog(R.string.action_starting_visit)
    }

    private fun dismissCurrentDialog() {
        patientDashboardActivity.dismissCustomFragmentDialog()
    }

    fun goToVisitDashboard(visitID: Long) {
        Intent(activity, VisitDashboardActivity::class.java).apply {
            putExtra(ApplicationConstants.BundleKeys.VISIT_ID, visitID)
            startActivity(this)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.patients_visit_tab_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.actionStartVisit -> showStartVisitStatus()
            else -> {
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(patientId: String): PatientVisitsFragment {
            val fragment = PatientVisitsFragment()
            fragment.arguments = bundleOf(Pair(PATIENT_ID_BUNDLE, patientId))
            return fragment
        }
    }
}
