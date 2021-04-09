/*  * The contents of this file are subject to the OpenMRS Public License  * Version 1.0 (the "License"); you may not use this file except in  * compliance with the License. You may obtain a copy of the License at  * http://license.openmrs.org  *  * Software distributed under the License is distributed on an "AS IS"  * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the  * License for the specific language governing rights and limitations  * under the License.  *  * Copyright (C) OpenMRS, LLC.  All Rights Reserved.  */ 
package org.openmrs.mobile.activities.addeditpatient

import android.app.Activity
import android.content.Intent
import android.graphics.Paint
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.common.base.Objects
import org.openmrs.mobile.activities.patientdashboard.PatientDashboardActivity
import org.openmrs.mobile.api.repository.VisitRepository
import org.openmrs.mobile.dao.PatientDAO
import org.openmrs.mobile.databinding.RowSimilarPatientBinding
import org.openmrs.mobile.models.Patient
import org.openmrs.mobile.utilities.ApplicationConstants
import org.openmrs.mobile.utilities.DateUtils.convertTime
import rx.android.schedulers.AndroidSchedulers

class SimilarPatientsRecyclerViewAdapter(private val mContext: Activity, private val patientList: List<Patient>, private val newPatient: Patient) : RecyclerView.Adapter<SimilarPatientsRecyclerViewAdapter.PatientViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PatientViewHolder {
        val binding = RowSimilarPatientBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PatientViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PatientViewHolder, position: Int) {
        val patient = patientList[position]
        setPatientName(holder, patient)
        setGender(holder, patient)
        setBirthdate(holder, patient)
        setPatientAdres(holder, patient)
        holder.binding.root.setOnClickListener { view ->
            if (!PatientDAO().isUserAlreadySaved(patient.uuid)) {
                downloadPatient(patient)
            }
            val intent = Intent(mContext, PatientDashboardActivity::class.java)
            intent.putExtra(ApplicationConstants.BundleKeys.PATIENT_ID_BUNDLE, getPatientId(patient))
            mContext.startActivity(intent)
            mContext.finish()
        }
    }

    private fun getPatientId(patient: Patient): String {
        return PatientDAO().findPatientByUUID(patient.uuid).id.toString()
    }

    override fun getItemCount(): Int {
        return patientList.size
    }

    inner class PatientViewHolder(val binding: RowSimilarPatientBinding) : RecyclerView.ViewHolder(binding.root)

    private fun downloadPatient(patient: Patient) {
        PatientDAO().savePatient(patient).observeOn(AndroidSchedulers.mainThread()).subscribe { id: Long? ->
            VisitRepository().syncVisitsData(patient)
            VisitRepository().syncLastVitals(patient.uuid)
        }
    }

    private fun setBirthdate(holder: PatientViewHolder, patient: Patient) {
        try {
            holder.binding.patientBirthDate.text = convertTime(convertTime(patient.birthdate)!!)
            if (Objects.equal(patient.birthdate, newPatient.birthdate)) {
                setStyleForMatchedPatientFields(holder.binding.patientBirthDate)
            }
        } catch (e: Exception) {
            holder.binding.patientBirthDate.text = " "
        }
    }

    private fun setGender(holder: PatientViewHolder, patient: Patient) {
        if (null != patient.gender) {
            holder.binding.patientGender.text = patient.gender
            if (Objects.equal(patient.gender, newPatient.gender)) {
                setStyleForMatchedPatientFields(holder.binding.patientGender)
            }
        }
    }

    private fun setPatientAdres(holder: PatientViewHolder, patient: Patient) {
        if (null != patient.address.address1) {
            holder.binding.patientAddres.text = patient.address.address1
            if (Objects.equal(patient.address.address1, newPatient.address.address1)) {
                setStyleForMatchedPatientFields(holder.binding.patientAddres)
            }
        }
        if (null != patient.address.postalCode) {
            holder.binding.patientPostalCode.text = patient.address.postalCode
            if (Objects.equal(patient.address.postalCode, newPatient.address.postalCode)) {
                setStyleForMatchedPatientFields(holder.binding.patientPostalCode)
            }
        }
        if (null != patient.address.cityVillage) {
            holder.binding.patientCity.text = patient.address.cityVillage
            if (Objects.equal(patient.address.cityVillage, newPatient.address.cityVillage)) {
                setStyleForMatchedPatientFields(holder.binding.patientCity)
            }
        }
        if (null != patient.address.country) {
            holder.binding.patientCountry.text = patient.address.country
            if (Objects.equal(patient.address.country, newPatient.address.country)) {
                setStyleForMatchedPatientFields(holder.binding.patientCountry)
            }
        }
    }

    private fun setPatientName(holder: PatientViewHolder, patient: Patient) {
        if (null != patient.name.givenName) {
            holder.binding.patientGivenName.text = patient.name.givenName

            if (Objects.equal(patient.name.givenName, newPatient.name.givenName)) {
                setStyleForMatchedPatientFields(holder.binding.patientGivenName)
            }
        }
        if (null != patient.name.middleName) {
            holder.binding.patientMiddleName.text = patient.name.middleName

            if (Objects.equal(patient.name.middleName, newPatient.name.middleName)) {
                setStyleForMatchedPatientFields(holder.binding.patientMiddleName)
            }
        }
        if (null != patient.name.familyName) {
            holder.binding.patientFamilyName.text = patient.name.familyName
            if (Objects.equal(patient.name.familyName, newPatient.name.familyName)) {
                setStyleForMatchedPatientFields(holder.binding.patientFamilyName)
            }
        }
    }

    private fun setStyleForMatchedPatientFields(textView: TextView) {
        textView.setTypeface(null, Typeface.BOLD)
        textView.paintFlags = textView.paintFlags or Paint.UNDERLINE_TEXT_FLAG
    }
}
