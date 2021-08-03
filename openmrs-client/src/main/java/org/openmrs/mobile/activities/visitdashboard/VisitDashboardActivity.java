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
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBar;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.ACBaseActivity;
import org.openmrs.mobile.activities.dialog.CustomFragmentDialog;
import org.openmrs.mobile.bundle.CustomDialogBundle;
import org.openmrs.mobile.databinding.ActivityVisitDashboardBinding;
import com.openmrs.android_sdk.utilities.ApplicationConstants;

public class VisitDashboardActivity extends ACBaseActivity {
    public VisitDashboardPresenter presenter;
    public Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityVisitDashboardBinding binding= ActivityVisitDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setElevation(0);
            getSupportActionBar().setTitle(R.string.visit_dashboard_label);
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
        presenter = new VisitDashboardPresenter(visitDashboardFragment, intent.getLongExtra(ApplicationConstants.BundleKeys.VISIT_ID, 0));
        presenter.updatePatientName();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        this.menu = menu;
        presenter.checkIfVisitActive();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
            case R.id.actionFillForm:
                presenter.fillForm();
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
