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

package org.openmrs.mobile.activities.visitdashboard;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.ACBaseActivity;
import org.openmrs.mobile.activities.dialog.CustomFragmentDialog;
import org.openmrs.mobile.bundle.CustomDialogBundle;
import org.openmrs.mobile.utilities.ApplicationConstants;

public class VisitDashboardActivity extends ACBaseActivity {

    public VisitDashboardPresenter mPresenter;

    public Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visit_dashboard);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);


        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Intent intent = getIntent();

        // Create fragment
        VisitDashboardFragment visitDashboardFragment =
                (VisitDashboardFragment) getSupportFragmentManager().findFragmentById(R.id.visitDashboardContentFrame);
        if (visitDashboardFragment == null) {
            visitDashboardFragment = VisitDashboardFragment.newInstance();
        }
        if (!visitDashboardFragment.isActive()) {
            addFragmentToActivity(getSupportFragmentManager(),
                    visitDashboardFragment, R.id.visitDashboardContentFrame);
        }

        // Create the presenter
        mPresenter = new VisitDashboardPresenter(visitDashboardFragment, intent.getLongExtra(ApplicationConstants.BundleKeys.VISIT_ID, 0));
        mPresenter.updatePatientName();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        this.menu = menu;
        mPresenter.checkIfVisitActive();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
            case R.id.actionFillForm:
                mPresenter.fillForm();
                break;
            case R.id.actionEndVisit:
                CustomDialogBundle bundle = new CustomDialogBundle();
                bundle.setTitleViewMessage(getString(R.string.end_visit_dialog_title));
                bundle.setTextViewMessage(getString(R.string.end_visit_dialog_message));
                bundle.setRightButtonAction(CustomFragmentDialog.OnClickAction.END_VISIT);
                bundle.setRightButtonText(getString(R.string.dialog_button_ok));
                bundle.setLeftButtonAction(CustomFragmentDialog.OnClickAction.DISMISS);
                bundle.setLeftButtonText(getString(R.string.dialog_button_cancel));
                createAndShowDialog(bundle, ApplicationConstants.DialogTAG.END_VISIT_DIALOG_TAG);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }
}
