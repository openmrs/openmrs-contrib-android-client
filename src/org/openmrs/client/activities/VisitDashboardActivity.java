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

package org.openmrs.client.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;

import org.openmrs.client.R;
import org.openmrs.client.activities.fragments.CustomFragmentDialog;
import org.openmrs.client.adapters.VisitExpandableListAdapter;
import org.openmrs.client.bundle.CustomDialogBundle;
import org.openmrs.client.dao.PatientDAO;
import org.openmrs.client.dao.VisitDAO;
import org.openmrs.client.models.Encounter;
import org.openmrs.client.models.Patient;
import org.openmrs.client.models.Visit;
import org.openmrs.client.net.VisitsManager;
import org.openmrs.client.utilities.ApplicationConstants;
import org.openmrs.client.utilities.DateUtils;
import org.openmrs.client.utilities.FontsUtil;

import java.util.List;

public class VisitDashboardActivity extends ACBaseActivity {

    private ExpandableListView mExpandableListView;
    private VisitExpandableListAdapter mExpandableListAdapter;
    private List<Encounter> mVisitEncounters;
    private TextView mEmptyListView;
    private Visit mVisit;
    private String mPatientName;
    private Patient mPatient;
    private VisitsManager mVisitsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visit_dashboard);
        mVisitsManager = new VisitsManager(this);
        Intent intent = getIntent();

        mVisit = new VisitDAO().getVisitsByID(intent.getLongExtra(ApplicationConstants.BundleKeys.VISIT_ID, 0));
        mPatient = new PatientDAO().findPatientByID(String.valueOf(mVisit.getPatientID()));

        mPatientName = intent.getStringExtra(ApplicationConstants.BundleKeys.PATIENT_NAME);
        mVisitEncounters = mVisit.getEncounters();

        mEmptyListView = (TextView) findViewById(R.id.visitDashboardEmpty);
        FontsUtil.setFont(mEmptyListView, FontsUtil.OpenFonts.OPEN_SANS_BOLD);
        mExpandableListView = (ExpandableListView) findViewById(R.id.visitDashboardExpList);
        mExpandableListView.setEmptyView(mEmptyListView);
    }

    @Override
    protected void onResume() {
        if (!mVisitEncounters.isEmpty()) {
            mEmptyListView.setVisibility(View.GONE);
            mExpandableListAdapter = new VisitExpandableListAdapter(this, mVisitEncounters);
            mExpandableListView.setAdapter(mExpandableListAdapter);
            mExpandableListView.setGroupIndicator(null);
        }
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (DateUtils.ZERO.equals(mVisit.getStopDate())) {
            getMenuInflater().inflate(R.menu.active_visit_menu, menu);
        }
        getSupportActionBar().setSubtitle(mPatientName);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
            case R.id.actionEndVisit:
                this.showEndVisitDialog();
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    public void endVisit() {
        mVisitsManager.inactivateVisitByUUID(mVisit.getUuid(), mPatient.getId());
    }

    private void showEndVisitDialog() {
        CustomDialogBundle bundle = new CustomDialogBundle();
        bundle.setTitleViewMessage(getString(R.string.end_visit_dialog_title));
        bundle.setTextViewMessage(getString(R.string.end_visit_dialog_message));
        bundle.setRightButtonAction(CustomFragmentDialog.OnClickAction.END_VISIT);
        bundle.setRightButtonText(getString(R.string.end_visit_dialog_button));
        bundle.setLeftButtonAction(CustomFragmentDialog.OnClickAction.DISMISS);
        bundle.setLeftButtonText(getString(R.string.dialog_button_cancel));
        createAndShowDialog(bundle, ApplicationConstants.DialogTAG.END_VISIT_DIALOG_TAG);
    }

    public void moveToPatientDashboard() {
        Intent intent = new Intent(this, PatientDashboardActivity.class);
        intent.putExtra(ApplicationConstants.BundleKeys.PATIENT_ID_BUNDLE, mPatient.getUuid());
        this.startActivity(intent);
    }
}
