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
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.openmrs.android_sdk.library.models.Patient
import com.openmrs.android_sdk.utilities.ApplicationConstants
import com.openmrs.android_sdk.utilities.StringUtils
import com.google.android.material.snackbar.Snackbar
import org.openmrs.mobile.R
import org.openmrs.mobile.activities.ACBaseFragment
import org.openmrs.mobile.activities.formlist.FormListActivity
import org.openmrs.mobile.databinding.FragmentFormEntryPatientListBinding

class FormEntryPatientListFragment : ACBaseFragment<FormEntryPatientListContract.Presenter>(), FormEntryPatientListContract.View {
    private var _binding: FragmentFormEntryPatientListBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = FragmentFormEntryPatientListBinding.inflate(inflater, container, false)
        val linearLayoutManager = LinearLayoutManager(activity)

        with(binding) {
            patientRecyclerView.setHasFixedSize(true)
            patientRecyclerView.layoutManager = linearLayoutManager
            swipeLayout.setOnRefreshListener {
                refreshUI()
                swipeLayout.isRefreshing = false
            }
        }
        return binding.root
    }

    private fun refreshUI() {
        binding.formEntryListInitialProgressBar.visibility = View.VISIBLE
        mPresenter?.updatePatientsList()
    }

    override fun updateAdapter(patientList: List<Patient?>?) {
        val adapter = FormEntryPatientListAdapter(this, patientList)
        adapter.notifyDataSetChanged()
        binding.patientRecyclerView.adapter = adapter
    }

    override fun updateListVisibility(isVisible: Boolean, emptyListTextStringId: Int, replacementWord: String?) {
        with(binding) {
            formEntryListInitialProgressBar.visibility = View.GONE
            if (isVisible) {
                patientRecyclerView.visibility = View.VISIBLE
                emptyPatientList.visibility = View.GONE
            } else {
                patientRecyclerView.visibility = View.GONE
                emptyPatientList.visibility = View.VISIBLE
            }
            if (StringUtils.isBlank(replacementWord)) {
                emptyPatientList.text = getString(emptyListTextStringId)
            } else {
                emptyPatientList.text = getString(emptyListTextStringId, replacementWord)
            }
        }
    }

    override fun startEncounterForPatient(selectedPatientID: Long?) {
        val intent = Intent(this.activity, FormListActivity::class.java)
        intent.putExtra(ApplicationConstants.BundleKeys.PATIENT_ID_BUNDLE, selectedPatientID)
        startActivity(intent)
    }

    fun showSnackbarInactivePatients(v: View?) {
        val snackbar = Snackbar.make(v!!, R.string.snackbar_nonvisitting_patients, Snackbar.LENGTH_LONG)
        val view = snackbar.view
        val tv = view.findViewById<TextView>(R.id.snackbar_text)
        tv.setTextColor(Color.WHITE)
        snackbar.show()
    }

    companion object {
        fun newInstance(): FormEntryPatientListFragment {
            return FormEntryPatientListFragment()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}