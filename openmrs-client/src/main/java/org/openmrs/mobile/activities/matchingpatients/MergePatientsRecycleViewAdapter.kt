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
package org.openmrs.mobile.activities.matchingpatients

import android.app.Activity
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.common.base.Objects
import org.openmrs.mobile.R
import org.openmrs.mobile.databinding.RowSimilarPatientBinding
import org.openmrs.mobile.models.Patient
import org.openmrs.mobile.utilities.DateUtils.convertTime
import org.openmrs.mobile.utilities.ToastUtil.notify

class MergePatientsRecycleViewAdapter(private val mContext: Activity,
                                      private val mPresenter: MatchingPatientsContract.Presenter,
                                      private val patientList: List<Patient>,
                                      private val newPatient: Patient) :
        RecyclerView.Adapter<MergePatientsRecycleViewAdapter.PatientViewHolder>() {

    private var selectedPosition = -1
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PatientViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.row_similar_patient, parent, false)
        return PatientViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PatientViewHolder, position: Int) {
        val patient = patientList[position]

        setPatientName(holder, patient)
        setGender(holder, patient)
        setBirthdate(holder, patient)
        setPatientAdres(holder, patient)
    }

    override fun getItemCount(): Int {
        return patientList.size
    }

    inner class PatientViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = RowSimilarPatientBinding.bind(itemView)

        init {
            with(binding) {
                root.setOnClickListener {
                    if (selectedPosition == -1) {
                        selectedPosition = adapterPosition
                        mPresenter.setSelectedPatient(patientList[selectedPosition])
                        patientsCardView.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.patient_selected_highlight))
                    } else if (selectedPosition == adapterPosition) {
                        selectedPosition = -1
                        mPresenter.removeSelectedPatient()
                        patientsCardView.setCardBackgroundColor(Color.WHITE)
                    } else {
                        notify(mContext.getString(R.string.only_one_patient_can_be_selected_notification_message))
                    }
                }
            }
        }
    }

    private fun setBirthdate(holder: PatientViewHolder, patient: Patient) {
        with(holder.binding) {
            try {
                patientBirthDate.text = convertTime(convertTime(patient.birthdate)!!)
                if (Objects.equal(patient.birthdate, newPatient.birthdate)) {
                    setStyleForMatchedPatientFields(patientBirthDate)
                }
            } catch (e: Exception) {
                patientBirthDate.text = " "
            }
        }
    }

    private fun setGender(holder: PatientViewHolder, patient: Patient) {
        with(holder.binding) {
            if (null != patient.gender) {
                patientGender.text = patient.gender
                if (Objects.equal(patient.gender, newPatient.gender)) {
                    setStyleForMatchedPatientFields(patientGender)
                }
            }
        }
    }

    private fun setPatientAdres(holder: PatientViewHolder, patient: Patient) {
        with(holder.binding) {
            if (null != patient.address.address1) {
                patientAddres.text = patient.address.address1
                if (Objects.equal(patient.address.address1, newPatient.address.address1)) {
                    setStyleForMatchedPatientFields(patientAddres)
                }
            }
            if (null != patient.address.postalCode) {
                patientPostalCode.text = patient.address.postalCode
                if (Objects.equal(patient.address.postalCode, newPatient.address.postalCode)) {
                    setStyleForMatchedPatientFields(patientPostalCode)
                }
            }
            if (null != patient.address.cityVillage) {
                patientCity.text = patient.address.cityVillage
                if (Objects.equal(patient.address.cityVillage, newPatient.address.cityVillage)) {
                    setStyleForMatchedPatientFields(patientCity)
                }
            }
            if (null != patient.address.country) {
                patientCountry.text = patient.address.country
                if (Objects.equal(patient.address.country, newPatient.address.country)) {
                    setStyleForMatchedPatientFields(patientCountry)
                }
            }
        }
    }

    private fun setPatientName(holder: PatientViewHolder, patient: Patient) {
        with(holder.binding) {
            if (null != patient.name.givenName) {
                patientGivenName.text = patient.name.givenName
                if (Objects.equal(patient.name.givenName, newPatient.name.givenName)) {
                    setStyleForMatchedPatientFields(patientGivenName)
                }
            }
            if (null != patient.name.middleName) {
                patientMiddleName.text = patient.name.middleName
                if (Objects.equal(patient.name.middleName, newPatient.name.middleName)) {
                    setStyleForMatchedPatientFields(patientMiddleName)
                }
            }
            if (null != patient.name.familyName) {
                patientFamilyName.text = patient.name.familyName
                if (Objects.equal(patient.name.familyName, newPatient.name.familyName)) {
                    setStyleForMatchedPatientFields(patientFamilyName)
                }
            }
        }
    }

    private fun setStyleForMatchedPatientFields(textView: TextView) {
        textView.setTypeface(null, Typeface.BOLD)
        textView.paintFlags = textView.paintFlags or Paint.UNDERLINE_TEXT_FLAG
    }
}