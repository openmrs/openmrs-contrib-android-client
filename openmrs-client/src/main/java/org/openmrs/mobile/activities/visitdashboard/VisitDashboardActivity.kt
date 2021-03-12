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

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import org.openmrs.mobile.R
import org.openmrs.mobile.activities.ACBaseActivity
import org.openmrs.mobile.activities.dialog.CustomFragmentDialog
import org.openmrs.mobile.bundle.CustomDialogBundle
import org.openmrs.mobile.utilities.ApplicationConstants

class VisitDashboardActivity : ACBaseActivity() {
    @JvmField
    var mPresenter: VisitDashboardPresenter? = null
    @JvmField
    var menu: Menu? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_visit_dashboard)
        val actionBar = supportActionBar
        if (actionBar != null) {
            supportActionBar!!.elevation = 0f
            supportActionBar!!.setTitle(R.string.visit_dashboard_label)
        }
        val intent = intent

        // Create fragment
        var visitDashboardFragment = supportFragmentManager.findFragmentById(R.id.visitDashboardContentFrame) as VisitDashboardFragment?
        if (visitDashboardFragment == null) {
            visitDashboardFragment = VisitDashboardFragment.newInstance()
        }
        if (!visitDashboardFragment!!.isActive) {
            addFragmentToActivity(supportFragmentManager,
                    visitDashboardFragment, R.id.visitDashboardContentFrame)
        }

        // Create the presenter
        mPresenter = VisitDashboardPresenter(visitDashboardFragment, intent.getLongExtra(ApplicationConstants.BundleKeys.VISIT_ID, 0))
        mPresenter!!.updatePatientName()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        this.menu = menu
        mPresenter!!.checkIfVisitActive()
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            R.id.actionFillForm -> mPresenter!!.fillForm()
            R.id.actionEndVisit -> {
                val bundle = CustomDialogBundle()
                bundle.titleViewMessage = getString(R.string.end_visit_dialog_title)
                bundle.textViewMessage = getString(R.string.end_visit_dialog_message)
                bundle.rightButtonAction = CustomFragmentDialog.OnClickAction.END_VISIT
                bundle.rightButtonText = getString(R.string.dialog_button_ok)
                bundle.leftButtonAction = CustomFragmentDialog.OnClickAction.DISMISS
                bundle.leftButtonText = getString(R.string.dialog_button_cancel)
                createAndShowDialog(bundle, ApplicationConstants.DialogTAG.END_VISIT_DIALOG_TAG)
            }
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }
}