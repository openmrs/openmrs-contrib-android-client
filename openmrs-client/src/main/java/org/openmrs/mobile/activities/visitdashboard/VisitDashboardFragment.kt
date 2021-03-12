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
package org.openmrs.mobile.activities.visitdashboard

import android.app.Activity
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ExpandableListView
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.snackbar.Snackbar.SnackbarLayout
import org.openmrs.mobile.R
import org.openmrs.mobile.activities.ACBaseFragment
import org.openmrs.mobile.activities.formlist.FormListActivity
import org.openmrs.mobile.application.OpenMRS
import org.openmrs.mobile.models.Encounter
import org.openmrs.mobile.utilities.ApplicationConstants
import org.openmrs.mobile.utilities.ToastUtil
import org.openmrs.mobile.utilities.ToastUtil.error
import org.openmrs.mobile.utilities.ToastUtil.showLongToast
import java.util.*

class VisitDashboardFragment : ACBaseFragment<VisitDashboardContract.Presenter?>(), VisitDashboardContract.View {
    private var mExpandableListView: ExpandableListView? = null
    private var mEmptyListView: TextView? = null
    private var root: View? = null
    private var snackbar: Snackbar? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        root = inflater.inflate(R.layout.fragment_visit_dashboard, container, false)
        mEmptyListView = root?.findViewById(R.id.visitDashboardEmpty)
        mExpandableListView = root?.findViewById(R.id.visitDashboardExpList)
        mExpandableListView?.emptyView = mEmptyListView
        setEmptyListVisibility(false)
        return root
    }

    override fun startCaptureVitals(patientId: Long) {
        try {
            val intent = Intent(this.activity, FormListActivity::class.java)
            intent.putExtra(ApplicationConstants.BundleKeys.PATIENT_ID_BUNDLE, patientId)
            startActivity(intent)
        } catch (e: Exception) {
            showLongToast(this.requireActivity(), ToastUtil.ToastType.ERROR, R.string.failed_to_open_vitals_form)
            OpenMRS.getInstance().openMRSLogger.d(e.toString())
        }
    }

    override fun moveToPatientDashboard() {
        val intent = Intent()
        requireActivity().setResult(Activity.RESULT_OK, intent)
        requireActivity().finish()
    }

    override fun updateList(visitEncounters: List<Encounter?>?) {
        val displayableEncounterTypes = ApplicationConstants.EncounterTypes.ENCOUNTER_TYPES_DISPLAYS
        val displayableEncounterTypesArray = HashSet(listOf(*displayableEncounterTypes))
        val displayableEncounters: MutableList<Encounter?> = ArrayList()
        for (encounter in visitEncounters!!) {
            var encounterTypeDisplay = encounter!!.encounterType!!.display
            encounterTypeDisplay = encounterTypeDisplay!!.split("\\(".toRegex()).toTypedArray()[0].trim { it <= ' ' }
            if (displayableEncounterTypesArray.contains(encounterTypeDisplay)) {
                encounter.encounterType!!.display = encounterTypeDisplay.split("\\(".toRegex()).toTypedArray()[0].trim { it <= ' ' }
                displayableEncounters.add(encounter)
            }
        }
        setupSnackBar()
        if (displayableEncounters.size == 0) {
            snackbar!!.show()
        } else {
            snackbar!!.dismiss()
        }
        val expandableListAdapter = this.activity?.let { VisitExpandableListAdapter(it, displayableEncounters) }
        mExpandableListView!!.setAdapter(expandableListAdapter)
        mExpandableListView!!.setGroupIndicator(null)
    }

    private fun setupSnackBar() {
        snackbar = Snackbar.make(root!!, ApplicationConstants.EMPTY_STRING, Snackbar.LENGTH_INDEFINITE)
        val customSnackBarView = layoutInflater.inflate(R.layout.snackbar, null)
        val snackBarLayout = snackbar!!.view as SnackbarLayout
        snackBarLayout.setPadding(0, 0, 0, 0)
        val noticeField = customSnackBarView.findViewById<TextView>(R.id.snackbar_text)
        noticeField.setText(R.string.snackbar_empty_visit_list)
        val dismissButton = customSnackBarView.findViewById<TextView>(R.id.snackbar_action_button)
        dismissButton.setText(R.string.snackbar_select)
        val typeface = Typeface.createFromAsset(requireActivity().assets, ApplicationConstants.TypeFacePathConstants.ROBOTO_MEDIUM)
        dismissButton.typeface = typeface
        dismissButton.setOnClickListener { mPresenter!!.fillForm() }
        snackBarLayout.addView(customSnackBarView, 0)
    }

    override fun setEmptyListVisibility(visibility: Boolean) {
        if (visibility) {
            mEmptyListView!!.visibility = View.VISIBLE
        } else {
            mEmptyListView!!.visibility = View.GONE
        }
    }

    override fun setActionBarTitle(name: String?) {
        (activity as VisitDashboardActivity?)!!.supportActionBar!!.title = name
    }

    override fun setActiveVisitMenu() {
        val menu = (activity as VisitDashboardActivity?)!!.menu
        requireActivity().menuInflater.inflate(R.menu.active_visit_menu, menu)
    }

    override fun showErrorToast(message: String?) {
        error(message!!)
    }

    override fun showErrorToast(messageId: Int) {
        error(getString(messageId))
    }

    override fun onResume() {
        super.onResume()
        mPresenter!!.subscribe()
    }

    companion object {
        fun newInstance(): VisitDashboardFragment {
            return VisitDashboardFragment()
        }
    }
}