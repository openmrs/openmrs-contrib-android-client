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

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.openmrs.android_sdk.library.models.Patient
import com.openmrs.android_sdk.utilities.ApplicationConstants
import com.openmrs.android_sdk.utilities.ApplicationConstants.BundleKeys.PATIENT_ID_BUNDLE
import org.openmrs.mobile.R
import org.openmrs.mobile.activities.formlist.FormListActivity

class FormEntryPatientListAdapter(
        private val mContext: FormEntryPatientListFragment,
        private var mItems: List<Patient>
) : RecyclerView.Adapter<FormEntryPatientListAdapter.PatientViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PatientViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.row_patient_details, parent, false)
        return PatientViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PatientViewHolder, position: Int) {
        val adapterPos = holder.adapterPosition
        val patient = this.mItems.get(position)
        val icon = mContext.resources.getDrawable(R.drawable.active_visit_dot)
        icon.setBounds(0, 0, icon.intrinsicHeight, icon.intrinsicWidth)
        holder.mVisitStatus.setCompoundDrawables(icon, null, null, null)
        holder.mVisitStatus.text = mContext.getString(R.string.active_visit_label_capture_vitals)
        holder.mRowLayout.setOnClickListener {
            Intent(mContext.activity, FormListActivity::class.java).apply {
                putExtra(PATIENT_ID_BUNDLE, mItems[adapterPos].id)
                mContext.startActivity(this)
            }
        }

        if (null != patient.identifier) {
            val display = "#" + patient.identifier.identifier
            holder.mIdentifier.text = display
        }
        if (null != patient.name) {
            holder.mDisplayName.text = patient.name.nameString
        }
        if (null != patient.gender) {
            if (patient.photo != null) {
                holder.mGender.setImageBitmap(patient.photo)
            } else {
                if (patient.gender == ApplicationConstants.MALE) {
                    holder.mGender.setImageResource(R.drawable.patient_male)
                } else {
                    holder.mGender.setImageResource(R.drawable.patient_female)
                }
            }
        }
    }

    override fun onViewDetachedFromWindow(holder: PatientViewHolder) {
        holder.clearAnimation()
    }

    override fun getItemCount(): Int {
        return mItems.size
    }

    fun updateList(patients: List<Patient>) {
        mItems = patients
        notifyDataSetChanged()
    }

    class PatientViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mRowLayout: CardView = itemView as CardView
        val mIdentifier: TextView = itemView.findViewById(R.id.syncedPatientIdentifier)
        val mDisplayName: TextView = itemView.findViewById(R.id.syncedPatientDisplayName)
        val mGender: ImageView = itemView.findViewById(R.id.syncedPatientGender)
        val mVisitStatus: TextView = itemView.findViewById(R.id.visitStatusLabel)

        fun clearAnimation() {
            mRowLayout.clearAnimation()
        }
    }

}
