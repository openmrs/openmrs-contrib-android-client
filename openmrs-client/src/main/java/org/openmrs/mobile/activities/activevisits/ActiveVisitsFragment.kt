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
package org.openmrs.mobile.activities.activevisits

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
import com.openmrs.android_sdk.library.models.Result
import com.openmrs.android_sdk.library.models.Visit
import dagger.hilt.android.AndroidEntryPoint
import org.openmrs.mobile.R
import org.openmrs.mobile.activities.BaseFragment
import org.openmrs.mobile.databinding.FragmentActiveVisitsBinding
import org.openmrs.mobile.utilities.makeGone
import org.openmrs.mobile.utilities.makeInvisible
import org.openmrs.mobile.utilities.makeVisible

@AndroidEntryPoint
class ActiveVisitsFragment : BaseFragment() {
    private var _binding: FragmentActiveVisitsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ActiveVisitsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentActiveVisitsBinding.inflate(inflater, container, false)

        val linearLayoutManager = LinearLayoutManager(this.activity)
        with(binding) {
            visitsRecyclerView.setHasFixedSize(true)
            visitsRecyclerView.layoutManager = linearLayoutManager
            visitsRecyclerView.adapter = ActiveVisitsRecyclerViewAdapter(requireContext(), ArrayList())

            setupObserver()
            fetchActiveVisits()

            swipeLayout.setOnRefreshListener {
                fetchActiveVisits()
                swipeLayout.isRefreshing = false
            }
        }
        return binding.root
    }

    private fun setupObserver() {
        viewModel.result.observe(viewLifecycleOwner, Observer { result ->
            when (result) {
                is Result.Loading -> showLoading()
                is Result.Success -> showVisitsList(result.data)
                else -> showError()
            }
        })
    }

    private fun fetchActiveVisits() {
        viewModel.fetchActiveVisits()
    }

    private fun fetchActiveVisits(query: String) {
        viewModel.fetchActiveVisits(query)
    }

    private fun showLoading() {
        with(binding) {
            progressBar.makeInvisible()
            visitsRecyclerView.makeGone()
        }
    }

    private fun showVisitsList(visits: List<Visit>) {
        with(binding) {
            progressBar.makeGone()
            if (visits.isEmpty()) {
                visitsRecyclerView.makeGone()
                showEmptyListText()
            } else {
                visitsRecyclerView.adapter = ActiveVisitsRecyclerViewAdapter(requireContext(), visits)
                visitsRecyclerView.makeVisible()
                hideEmptyListText()
            }
        }
    }

    private fun showError() {
        with(binding) {
            progressBar.makeGone()
            visitsRecyclerView.makeGone()
        }
        showEmptyListText()
    }

    private fun showEmptyListText() {
        binding.emptyVisitsListViewLabel.makeVisible()
        binding.emptyVisitsListViewLabel.text = getString(R.string.search_visits_no_results)
    }

    private fun hideEmptyListText() {
        binding.emptyVisitsListViewLabel.makeGone()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.find_visits_menu, menu)

        val findVisitView = menu.findItem(R.id.actionSearchLocalVisits).actionView as SearchView

        findVisitView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                findVisitView.clearFocus()
                return true
            }

            override fun onQueryTextChange(query: String): Boolean {
                if (query.isNotEmpty()) fetchActiveVisits(query)
                else fetchActiveVisits()

                return true
            }
        })
    }

    companion object {
        fun newInstance(): ActiveVisitsFragment {
            return ActiveVisitsFragment()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
