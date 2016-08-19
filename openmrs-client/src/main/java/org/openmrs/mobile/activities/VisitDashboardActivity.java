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

package org.openmrs.mobile.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;
import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.fragments.CustomFragmentDialog;
import org.openmrs.mobile.adapters.VisitExpandableListAdapter;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.bundle.CustomDialogBundle;
import org.openmrs.mobile.dao.EncounterDAO;
import org.openmrs.mobile.dao.PatientDAO;
import org.openmrs.mobile.dao.VisitDAO;
import org.openmrs.mobile.intefaces.VisitDashboardCallbackListener;
import org.openmrs.mobile.models.retrofit.Encounter;
import org.openmrs.mobile.models.Visit;
import org.openmrs.mobile.net.FormsManager;
import org.openmrs.mobile.net.VisitsManager;
import org.openmrs.mobile.net.helpers.VisitsHelper;
import org.openmrs.mobile.models.retrofit.Patient;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.DateUtils;
import org.openmrs.mobile.utilities.FontsUtil;
import org.openmrs.mobile.utilities.ToastUtil;

import java.util.List;

public class VisitDashboardActivity extends ACBaseActivity implements VisitDashboardCallbackListener {

    private ExpandableListView mExpandableListView;
    private VisitExpandableListAdapter mExpandableListAdapter;
    private List<Encounter> mVisitEncounters;
    private TextView mEmptyListView;
    private Visit mVisit;
    private String mPatientName;
    private Patient mPatient;

    private VisitsManager mVisitsManager;
    private FormsManager mFormsManager;

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

        mVisit = new VisitDAO().getVisitsByID(intent.getLongExtra(ApplicationConstants.BundleKeys.VISIT_ID, 0));
        mPatient = new PatientDAO().findPatientByID(String.valueOf(mVisit.getPatientID()));

        mPatientName = mPatient.getPerson().getName().getNameString();
        mVisitEncounters = mVisit.getEncounters();

        mEmptyListView = (TextView) findViewById(R.id.visitDashboardEmpty);
        FontsUtil.setFont(mEmptyListView, FontsUtil.OpenFonts.OPEN_SANS_BOLD);
        mExpandableListView = (ExpandableListView) findViewById(R.id.visitDashboardExpList);
        mExpandableListView.setEmptyView(mEmptyListView);
    }

    @Override
    protected void onResume() {
        mVisitsManager = new VisitsManager();
        mFormsManager = new FormsManager();
        if (!mVisitEncounters.isEmpty()) {
            mEmptyListView.setVisibility(View.GONE);
        }
        mExpandableListAdapter = new VisitExpandableListAdapter(this, mVisitEncounters);
        mExpandableListView.setAdapter(mExpandableListAdapter);
        mExpandableListView.setGroupIndicator(null);
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
            case R.id.actionFillForm:
                if(mPatient.getUuid()!=null)
                {
                    startCaptureVitals();
                }
                else
                    ToastUtil.error("Patient not yet registered, cannot create encounter.");
                break;
            case R.id.actionEndVisit:
                this.showEndVisitDialog();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public void updateEncounterList() {
        mVisitEncounters.clear();
        mExpandableListAdapter.notifyDataSetChanged();
        mVisit.setEncounters(new EncounterDAO().findEncountersByVisitID(mVisit.getId()));
        mVisitEncounters.addAll(mVisit.getEncounters());
        mExpandableListAdapter.notifyDataSetChanged();
    }

    public void endVisit() {
        mVisitsManager.endVisitByUUID(
                VisitsHelper.createEndVisitsByUUIDListener(mVisit.getUuid(), mPatient.getId(), mVisit.getId(), this));
    }

    private void startCaptureVitals() {
        try {
            Intent intent = new Intent(this, FormListActivity.class);
            intent.putExtra(ApplicationConstants.BundleKeys.PATIENT_ID_BUNDLE, mPatient.getId());
            startActivity(intent);
        } catch (Exception e) {
            ToastUtil.showLongToast(this, ToastUtil.ToastType.ERROR, R.string.failed_to_open_vitals_form);
            OpenMRS.getInstance().getOpenMRSLogger().d(e.toString());
        }
    }

    private void showEndVisitDialog() {
        CustomDialogBundle bundle = new CustomDialogBundle();
        bundle.setTitleViewMessage(getString(R.string.end_visit_dialog_title));
        bundle.setTextViewMessage(getString(R.string.end_visit_dialog_message));
        bundle.setRightButtonAction(CustomFragmentDialog.OnClickAction.END_VISIT);
        bundle.setRightButtonText(getString(R.string.dialog_button_ok));
        bundle.setLeftButtonAction(CustomFragmentDialog.OnClickAction.DISMISS);
        bundle.setLeftButtonText(getString(R.string.dialog_button_cancel));
        createAndShowDialog(bundle, ApplicationConstants.DialogTAG.END_VISIT_DIALOG_TAG);
    }

    public void moveToPatientDashboard() {
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
    }
}
