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
package org.openmrs.mobile.activities.providermanagerdashboard.addprovider

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import org.openmrs.mobile.R
import org.openmrs.mobile.activities.providermanagerdashboard.addprovider.MatchingProviderRecyclerViewAdapter.SimilarProviderViewHolder
import org.openmrs.mobile.models.Provider

class MatchingProviderRecyclerViewAdapter(private val context: Context, private var mItems: List<Provider>) : RecyclerView.Adapter<SimilarProviderViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimilarProviderViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.row_matching_provider, parent, false)
        return SimilarProviderViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: SimilarProviderViewHolder, position: Int) {
        val provider = mItems[position]
        if (provider.person!!.display != null) {
            holder.mName.text = provider.person!!.display
        }
        if (provider.identifier != null) {
            holder.mIdentifier.text = provider.identifier
        }

        // TODO open provider dashboard for clicked provider
        holder.providerDetailsCL.setOnClickListener { view: View? -> }
    }

    override fun getItemCount(): Int {
        return mItems.size
    }

    inner class SimilarProviderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mIdentifier: TextView
        val mName: TextView
        val providerDetailsCL: ConstraintLayout

        init {
            providerDetailsCL = itemView.findViewById(R.id.providerManagementCL)
            mIdentifier = itemView.findViewById(R.id.providerManagementIdentifier)
            mName = itemView.findViewById(R.id.providerManagementName)
        }
    }

    fun setItems(mItems: List<Provider>) {
        this.mItems = mItems
    }
}