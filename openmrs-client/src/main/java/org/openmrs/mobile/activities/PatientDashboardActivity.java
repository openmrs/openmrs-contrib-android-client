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
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.yanzm.mth.MaterialTabHost;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.fragments.CustomFragmentDialog;
import org.openmrs.mobile.activities.fragments.PatientDetailsFragment;
import org.openmrs.mobile.activities.fragments.PatientDiagnosisFragment;
import org.openmrs.mobile.activities.fragments.PatientVisitsFragment;
import org.openmrs.mobile.activities.fragments.PatientVitalsFragment;
import org.openmrs.mobile.dao.PatientDAO;
import org.openmrs.mobile.net.FindPatientsManager;
import org.openmrs.mobile.net.VisitsManager;
import org.openmrs.mobile.net.helpers.FindPatientsHelper;
import org.openmrs.mobile.net.helpers.VisitsHelper;
import org.openmrs.mobile.models.retrofit.Patient;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.NetworkUtils;
import org.openmrs.mobile.utilities.TabUtil;
import org.openmrs.mobile.utilities.ToastUtil;

import java.util.List;

public class PatientDashboardActivity extends ACBaseActivity implements ActionBar.TabListener {

    private static final int REQUEST_CODE_FOR_VISIT = 1;
    private Patient mPatient;
    private ViewPager mViewPager;
    private PatientDashboardPagerAdapter mPatientDashboardPagerAdapter;
    private boolean mProgressDialog;
    private DialogAction mDialogAction;
    private View rootView;
    private Snackbar snackbar;

    public static final int DETAILS_TAB_POS = 0;
    public static final int DIAGNOSIS_TAB_POS = 1;
    public static final int VISITS_TAB_POS = 2;
    public static final int VITALS_TAB_POS = 3;

    public enum DialogAction {
        SYNCHRONIZE, ADD_VISIT
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.patient_dashboard_layout);
        getSupportActionBar().setElevation(0);
        rootView=findViewById(R.id.rootview);

        Bundle patientBundle = savedInstanceState;
        if (null != patientBundle) {
            patientBundle.getString(ApplicationConstants.BundleKeys.PATIENT_ID_BUNDLE);
        } else {
            patientBundle = getIntent().getExtras();
        }
        if (patientBundle.getBoolean(ApplicationConstants.BundleKeys.PROGRESS_BAR)) {
            showProgressDialog(R.string.action_synchronize_patients, DialogAction.SYNCHRONIZE);
        }
        mPatient = new PatientDAO().findPatientByUUID(patientBundle.getString(ApplicationConstants.BundleKeys.PATIENT_ID_BUNDLE));
        new VisitsManager().getLastVitals(
                VisitsHelper.createLastVitalsListener(mPatient.getUuid()));
        mPatientDashboardPagerAdapter = new PatientDashboardPagerAdapter(getSupportFragmentManager());
        initViewPager();

        if (NetworkUtils.isOnline())
            refreshPatient();
        else
        {
            snackbar = Snackbar
                    .make(rootView, "Offline mode. Patient data may not be up to date.", Snackbar.LENGTH_INDEFINITE);
            View view = snackbar.getView();
            TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
            tv.setTextColor(Color.WHITE);
            snackbar.show();
        }


    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(ApplicationConstants.BundleKeys.PATIENT_ID_BUNDLE, mPatient.getUuid());
        outState.putBoolean(ApplicationConstants.BundleKeys.PROGRESS_BAR, mProgressDialog);
    }

    public void visitStarted(long visitID, boolean errorOccurred) {
        this.stopLoader(errorOccurred);
        if (!errorOccurred) {
            goToVisitDashboard(visitID);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        List<Fragment> fragments;
        switch (requestCode) {
            case REQUEST_CODE_FOR_VISIT:
                fragments = getSupportFragmentManager().getFragments();
                recreateFragmentView(fragments.get(VISITS_TAB_POS));
                break;
            default:
                break;
        }
    }

    public void goToVisitDashboard(Long visitID) {
        Intent intent = new Intent(this, VisitDashboardActivity.class);
        intent.putExtra(ApplicationConstants.BundleKeys.VISIT_ID, visitID);
        startActivityForResult(intent, REQUEST_CODE_FOR_VISIT);
    }

    private void initViewPager() {

        MaterialTabHost tabHost = (MaterialTabHost) findViewById(R.id.tabhost);
        tabHost.setType(MaterialTabHost.Type.FullScreenWidth);

        for (int i = 0; i < mPatientDashboardPagerAdapter.getCount(); i++) {
            tabHost.addTab(mPatientDashboardPagerAdapter.getpagetitle(i));
        }

        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mPatientDashboardPagerAdapter);
        mViewPager.addOnPageChangeListener(tabHost);


        tabHost.setOnTabChangeListener(new MaterialTabHost.OnTabChangeListener() {
            @Override
            public void onTabSelected(int position) {
                mViewPager.setCurrentItem(position);
            }
        });


    }

    @Override
    public void onConfigurationChanged(final Configuration config) {
        super.onConfigurationChanged(config);
        TabUtil.setHasEmbeddedTabs(getSupportActionBar(), getWindowManager(), TabUtil.MIN_SCREEN_WIDTH_FOR_PATIENTDASHBOARDACTIVITY);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.patient_dashboard_menu, menu);
        getSupportActionBar().setTitle(mPatient.getPerson().getName().getNameString());
        getSupportActionBar().setSubtitle("#" + mPatient.getIdentifier().getIdentifier());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.actionDelete:
                new PatientDAO().deletePatient(mPatient.getId());
                finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }

    public void synchronizePatient() {
        if(NetworkUtils.isOnline())
        {
            showProgressDialog(R.string.action_synchronize_patients, DialogAction.SYNCHRONIZE);
            new FindPatientsManager().getFullPatientData(
                FindPatientsHelper.createFullPatientDataListener(mPatient.getUuid(), this));
        }
        else
            ToastUtil.error("Cannot sync in offline mode");
    }

    public void refreshPatient() {
        new FindPatientsManager().getFullPatientData(
                FindPatientsHelper.createFullPatientDataListener(mPatient.getUuid(), this));
    }

    public void showProgressDialog(int resId, DialogAction dialogAction) {
        mProgressDialog = true;
        super.showProgressDialog(getString(resId));
        mDialogAction = dialogAction;
    }

    public void updatePatientDetailsData(final Patient patient) {
        if (new PatientDAO().updatePatient(mPatient.getId(), patient)) {
            VisitsManager fvm = new VisitsManager();
            mPatient = new PatientDAO().findPatientByUUID(mPatient.getUuid());

            PatientDetailsFragment fragment = (PatientDetailsFragment) mPatientDashboardPagerAdapter
                    .getRegisteredFragment(PatientDashboardActivity.DETAILS_TAB_POS);
            fragment.reloadPatientData(mPatient);

            fvm.findVisitsByPatientUUID(
                    VisitsHelper.createVisitsByPatientUUIDListener(mPatient.getUuid(), mPatient.getId(), this));
        } else {
            stopLoader(true);
        }
    }

    public void updatePatientVisitsData(boolean errorOccurred) {
        final List<Fragment> fragments = getSupportFragmentManager().getFragments();
        for (final Fragment fragment : fragments) {
            if (null != fragment) {
                this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        recreateFragmentView(fragment);
                    }
                });
            }
        }
        stopLoader(errorOccurred);
    }

    public void stopLoader(boolean errorOccurred) {
        mProgressDialog = false;
        if (mCustomFragmentDialog!=null)
            mCustomFragmentDialog.dismiss();
        if (mDialogAction == DialogAction.SYNCHRONIZE) {
            mViewPager.setCurrentItem(DETAILS_TAB_POS);
            if (!errorOccurred) {
                ToastUtil.showShortToast(this,
                        ToastUtil.ToastType.SUCCESS,
                        R.string.synchronize_patient_successful);
            } else {
                ToastUtil.showShortToast(this,
                        ToastUtil.ToastType.ERROR,
                        R.string.synchronize_patient_error);
            }
        } else if (mDialogAction == DialogAction.ADD_VISIT) {
            List<Fragment> fragments = getSupportFragmentManager().getFragments();
            recreateFragmentView(fragments.get(VISITS_TAB_POS));
            if (!errorOccurred) {
                ToastUtil.showShortToast(this,
                        ToastUtil.ToastType.SUCCESS,
                        R.string.start_visit_successful);
            } else {
                ToastUtil.showShortToast(this,
                        ToastUtil.ToastType.ERROR,
                        R.string.start_visit_error);
            }
        }
    }

    private void recreateFragmentView(final Fragment fragment) {
        FragmentTransaction fragTransaction = getSupportFragmentManager().beginTransaction();
        fragTransaction.detach(fragment);
        fragTransaction.attach(fragment);
        fragTransaction.commitAllowingStateLoss();
        if (fragment instanceof CustomFragmentDialog) {
            mCustomFragmentDialog = (CustomFragmentDialog) fragment;
        }
    }

    public class PatientDashboardPagerAdapter extends FragmentPagerAdapter {

        SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();


        public PatientDashboardPagerAdapter(FragmentManager fm) {
            super(fm);
        }


        @Override
        public Fragment getItem(int i) {
            switch (i) {
                case DETAILS_TAB_POS:
                    return PatientDetailsFragment.newInstance(mPatient);
                case DIAGNOSIS_TAB_POS:
                    return PatientDiagnosisFragment.newInstance(mPatient.getId());
                case VISITS_TAB_POS:
                    return PatientVisitsFragment.newInstance(mPatient);
                case VITALS_TAB_POS:
                    return PatientVitalsFragment.newInstance(mPatient.getUuid());
                default:
                    return null;
            }
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment fragment = (Fragment) super.instantiateItem(container, position);
            registeredFragments.put(position, fragment);
            return fragment;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            registeredFragments.remove(position);
            super.destroyItem(container, position, object);
        }

        public Fragment getRegisteredFragment(int position) {
            return registeredFragments.get(position);
        }

        @Override
        public int getCount() {
            return 4;
        }

        public String getpagetitle( int i)
        {
            switch(i)
            {
                case DETAILS_TAB_POS:
                    return getString(R.string.patient_scroll_tab_details_label).toUpperCase();
                case DIAGNOSIS_TAB_POS:
                    return getString(R.string.patient_scroll_tab_diagnosis_label).toUpperCase();
                case VISITS_TAB_POS:
                    return getString(R.string.patient_scroll_tab_visits_label).toUpperCase();
                case VITALS_TAB_POS:
                    return getString(R.string.patient_scroll_tab_vitals_label).toUpperCase();
                default:
                    return null;

            }
        }

    }

}
