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

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.openmrs.mobile.R
import org.openmrs.mobile.activities.visitdashboard.VisitDashboardActivity
import org.openmrs.mobile.dao.PatientDAO
import org.openmrs.mobile.models.Visit
import org.openmrs.mobile.utilities.ApplicationConstants
import org.openmrs.mobile.utilities.DateUtils

class ActiveVisitsRecyclerViewAdapter(private val mContext: Context, private val mVisits: List<Visit?>?) : RecyclerView.Adapter<ActiveVisitsRecyclerViewAdapter.VisitViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VisitViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.row_find_visits, parent, false)
        return VisitViewHolder(itemView)
    }

    override fun onBindViewHolder(visitViewHolder: VisitViewHolder, position: Int) {
        val adapterPos = visitViewHolder.adapterPosition
        val visit = this.mVisits?.get(adapterPos)
        val patient = PatientDAO().findPatientByID(visit?.patient?.id.toString())
        visitViewHolder.mVisitPlace.text = mContext.getString(R.string.visit_in, visit?.location?.display)
        if (null != visit?.patient?.id) {
            val display = "#" + patient.identifier.identifier
            visitViewHolder.mIdentifier.text = display
        }
        if (null != patient.name) {
            visitViewHolder.mDisplayName.text = patient.name.nameString
        }
        if (null != patient.gender) {
            visitViewHolder.mGender.text = patient.gender
        }
        try {
            visitViewHolder.mBirthDate.text = DateUtils.convertTime(DateUtils.convertTime(patient.birthdate))
        } catch (e: Exception) {
            visitViewHolder.mBirthDate.text = " "
        }
        visitViewHolder.mLinearLayout.setOnClickListener {
            val intent = Intent(mContext, VisitDashboardActivity::class.java)
            intent.putExtra(ApplicationConstants.BundleKeys.VISIT_ID, mVisits?.get(adapterPos)?.id)
            mContext.startActivity(intent)
        }
    }

    override fun onViewDetachedFromWindow(holder: VisitViewHolder) {
        holder.clearAnimation()
    }

    override fun getItemCount(): Int {
        return mVisits?.size!!
    }

    class VisitViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mIdentifier: TextView = itemView.findViewById(R.id.findVisitsIdentifier)
        val mDisplayName: TextView = itemView.findViewById(R.id.findVisitsDisplayName)
        val mGender: TextView = itemView.findViewById(R.id.findVisitsPatientGender)
        val mBirthDate: TextView = itemView.findViewById(R.id.findVisitsPatientBirthDate)
        val mVisitPlace: TextView = itemView.findViewById(R.id.findVisitsPlace)
        val mLinearLayout: LinearLayout = itemView.findViewById(R.id.findVisitContainerLL)

        fun clearAnimation() {
            mLinearLayout.clearAnimation()
        }

    }

}