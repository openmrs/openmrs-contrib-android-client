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
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.openmrs.android_sdk.library.dao.PatientDAO
import com.openmrs.android_sdk.library.models.Visit
import com.openmrs.android_sdk.utilities.ApplicationConstants
import com.openmrs.android_sdk.utilities.DateUtils
import org.openmrs.mobile.R
import org.openmrs.mobile.activities.visitdashboard.VisitDashboardActivity

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
            if (patient.photo != null) {
                visitViewHolder.mGender.setImageBitmap(patient.photo)
            } else {
                if (patient.gender == ApplicationConstants.MALE) {
                    visitViewHolder.mGender.setImageResource(R.drawable.patient_male)
                } else {
                    visitViewHolder.mGender.setImageResource(R.drawable.patient_female)
                }
            }
        }
        try {
            visitViewHolder.mStartVisitDate.text = DateUtils.convertTime(visit?.startDatetime)?.let { DateUtils.convertTime(it) }
        } catch (e: Exception) {
            visitViewHolder.mStartVisitDate.text = " "
        }
        visitViewHolder.mConstraintLayout.setOnClickListener {
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
        val mGender: ImageView = itemView.findViewById(R.id.findVisitsPatientGender)
        val mStartVisitDate: TextView = itemView.findViewById(R.id.findVisitsPatientStartVisitDate)
        val mVisitPlace: TextView = itemView.findViewById(R.id.findVisitsPlace)
        val mConstraintLayout: ConstraintLayout = itemView.findViewById(R.id.findVisitContainerLL)

        fun clearAnimation() {
            mConstraintLayout.clearAnimation()
        }

    }

}