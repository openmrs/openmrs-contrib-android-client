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
package org.openmrs.mobile.activities.providermanagerdashboard

import android.content.Intent
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
import com.openmrs.android_sdk.library.models.Provider
import com.openmrs.android_sdk.library.models.Result
import com.openmrs.android_sdk.utilities.ToastUtil
import dagger.hilt.android.AndroidEntryPoint
import org.openmrs.mobile.R
import org.openmrs.mobile.activities.BaseFragment
import org.openmrs.mobile.activities.addeditprovider.AddEditProviderActivity
import org.openmrs.mobile.databinding.FragmentProviderManagementBinding
import org.openmrs.mobile.utilities.makeGone
import org.openmrs.mobile.utilities.makeVisible

@AndroidEntryPoint
class ProviderManagerDashboardFragment : BaseFragment() {
    private var _binding: FragmentProviderManagementBinding? = null
    private val binding: FragmentProviderManagementBinding get() = _binding!!

    private val viewModel: ProviderManagerDashboardViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentProviderManagementBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)

        setupAdapter()
        setupObserver()
        setupListeners()
        fetchProviders()

        return binding.root
    }

    private fun setupAdapter() = with(binding.providerManagementRecyclerView) {
        setHasFixedSize(true)
        layoutManager = LinearLayoutManager(requireContext())
        adapter = ProviderManagerDashboardRecyclerViewAdapter(this@ProviderManagerDashboardFragment, emptyList())
    }

    private fun setupListeners() = with(binding) {
        // Swipe to refresh
        swipeLayout.setOnRefreshListener {
            fetchProviders()
            swipeLayout.isRefreshing = false
        }

        // Add provider floating action button
        providerManagementFragAddFAB.setOnClickListener {
            startActivity(Intent(activity, AddEditProviderActivity::class.java))
        }
    }

    private fun setupObserver() {
        viewModel.result.observe(viewLifecycleOwner, Observer { result ->
            when (result) {
                is Result.Loading -> showLoading()
                is Result.Success -> showProvidersList(result.data)
                is Result.Error -> showError()
                else -> throw IllegalStateException()
            }
        })
    }

    private fun fetchProviders(query: String? = null) {
        viewModel.fetchProviders(query)
    }

    private fun showLoading() = with(binding) {
        providerManagementRecyclerView.makeGone()
        emptyProviderManagementList.makeGone()
        providerManagementInitialProgressBar.makeVisible()
    }

    private fun showProvidersList(providers: List<Provider>) = with(binding) {
        providerManagementInitialProgressBar.makeGone()
        if (providers.isEmpty()) {
            emptyProviderManagementList.text = getString(R.string.providers_fetching_no_results)
            emptyProviderManagementList.makeVisible()
            providerManagementRecyclerView.makeGone()
        } else {
            (providerManagementRecyclerView.adapter as ProviderManagerDashboardRecyclerViewAdapter)
                    .updateList(providers)
            providerManagementRecyclerView.makeVisible()
            emptyProviderManagementList.makeGone()
        }
    }

    private fun showError() = with(binding) {
        providerManagementInitialProgressBar.makeGone()
        emptyProviderManagementList.makeGone()
        providerManagementRecyclerView.makeGone()
        ToastUtil.error(getString(R.string.providers_fetching_error))
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.provider_manager_menu, menu)
        val searchView = menu.findItem(R.id.actionSearchLocal).actionView as SearchView

        // Search function
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(query: String): Boolean {
                fetchProviders(query)
                return true
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(): ProviderManagerDashboardFragment = ProviderManagerDashboardFragment()
    }
}
