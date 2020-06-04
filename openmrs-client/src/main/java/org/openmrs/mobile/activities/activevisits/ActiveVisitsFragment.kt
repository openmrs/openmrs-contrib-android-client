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
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import org.openmrs.mobile.R
import org.openmrs.mobile.activities.ACBaseFragment
import org.openmrs.mobile.databinding.FragmentActiveVisitsBinding
import org.openmrs.mobile.models.Visit

class ActiveVisitsFragment : ACBaseFragment<ActiveVisitsContract.Presenter>(), ActiveVisitsContract.View {
    private lateinit var binding: FragmentActiveVisitsBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentActiveVisitsBinding.inflate(inflater, container, false)

        val linearLayoutManager = LinearLayoutManager(this.activity)
        binding.visitsRecyclerView.setHasFixedSize(true)
        binding.visitsRecyclerView.layoutManager = linearLayoutManager
        binding.visitsRecyclerView.adapter = ActiveVisitsRecyclerViewAdapter(this.activity!!, ArrayList())

        binding.emptyVisitsListViewLabel.text = getString(R.string.search_visits_no_results)
        binding.emptyVisitsListViewLabel.visibility = View.INVISIBLE
        binding.swipeLayout.setOnRefreshListener {
            refreshUI()
            binding.swipeLayout.isRefreshing = false
        }
        return binding.root
    }

    private fun refreshUI() {
        binding.progressBar.visibility = View.VISIBLE
        mPresenter!!.updateVisitsInDatabaseList()
    }

    override fun updateListVisibility(visitList: List<Visit?>?) {
        binding.progressBar.visibility = View.GONE
        if (visitList!!.isEmpty()) {
            binding.visitsRecyclerView.visibility = View.GONE
            binding.emptyVisitsListViewLabel.visibility = View.VISIBLE
        } else {
            binding.visitsRecyclerView.adapter = ActiveVisitsRecyclerViewAdapter(this.activity!!, visitList)
            binding.visitsRecyclerView.visibility = View.VISIBLE
            binding.emptyVisitsListViewLabel.visibility = View.GONE
        }
    }

    override fun setEmptyListText(stringId: Int) {
        binding.emptyVisitsListViewLabel.text = getString(stringId)
    }

    override fun setEmptyListText(stringId: Int, query: String?) {
        binding.emptyVisitsListViewLabel.text = getString(stringId, query)
    }

    companion object {
        fun newInstance(): ActiveVisitsFragment {
            return ActiveVisitsFragment()
        }
    }
}