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
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;
import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.fragments.CustomFragmentDialog;
import org.openmrs.mobile.activities.fragments.PatientDetailsFragment;
import org.openmrs.mobile.activities.fragments.PatientDiagnosisFragment;
import org.openmrs.mobile.activities.fragments.PatientVisitsFragment;
import org.openmrs.mobile.activities.fragments.PatientVitalsFragment;
import org.openmrs.mobile.dao.PatientDAO;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.net.FindPatientsManager;
import org.openmrs.mobile.net.VisitsManager;
import org.openmrs.mobile.net.helpers.FindPatientsHelper;
import org.openmrs.mobile.net.helpers.VisitsHelper;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.TabUtil;
import org.openmrs.mobile.utilities.ToastUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PatientDashboardActivity extends ACBaseActivity implements ActionBar.TabListener {

    private static final int REQUEST_CODE_FOR_VISIT = 1;
    private Patient mPatient;
    private ViewPager mViewPager;
    private PatientDashboardPagerAdapter mPatientDashboardPagerAdapter;
    private boolean mProgressDialog;
    private DialogAction mDialogAction;

    public enum DialogAction {
        SYNCHRONIZE, ADD_VISIT
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.patient_dashboard_layout);

        List<TabHost> tabHosts = new ArrayList<TabHost>(Arrays.asList(
                new TabHost(TabHost.DETAILS_TAB_POS, getString(R.string.patient_scroll_tab_details_label)),
                new TabHost(TabHost.DIAGNOSIS_TAB_POS, getString(R.string.patient_scroll_tab_diagnosis_label)),
                new TabHost(TabHost.VISITS_TAB_POS, getString(R.string.patient_scroll_tab_visits_label)),
                new TabHost(TabHost.VITALS_TAB_POS, getString(R.string.patient_scroll_tab_vitals_label))
        ));

        Bundle patientBundle = savedInstanceState;
        if (null != patientBundle) {
            patientBundle.getLong(ApplicationConstants.BundleKeys.PATIENT_ID_BUNDLE);
        } else {
            patientBundle = getIntent().getExtras();
        }
        if (patientBundle.getBoolean(ApplicationConstants.BundleKeys.PROGRESS_BAR)) {
            showProgressDialog(R.string.action_synchronize_patients, DialogAction.SYNCHRONIZE);
        }
        mPatient = new PatientDAO().findPatientByID(patientBundle.getLong(ApplicationConstants.BundleKeys.PATIENT_ID_BUNDLE));
        new VisitsManager().getLastVitals(
                VisitsHelper.createLastVitalsListener(mPatient.getId()));
        mPatientDashboardPagerAdapter = new PatientDashboardPagerAdapter(getSupportFragmentManager(), tabHosts);
        initViewPager();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(ApplicationConstants.BundleKeys.PATIENT_UUID_BUNDLE, mPatient.getUuid());
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
                recreateFragmentView(fragments.get(TabHost.VISITS_TAB_POS));
                break;
            default:
                break;
        }
    }

    public void goToVisitDashboard(Long visitID) {
        Intent intent = new Intent(this, VisitDashboardActivity.class);
        intent.putExtra(ApplicationConstants.BundleKeys.VISIT_ID, visitID);
        intent.putExtra(ApplicationConstants.BundleKeys.PATIENT_NAME, this.getSupportActionBar().getTitle().toString());
        startActivityForResult(intent, REQUEST_CODE_FOR_VISIT);
    }

    private void initViewPager() {
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mPatientDashboardPagerAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });
        for (TabHost tabHost : mPatientDashboardPagerAdapter.getTabHosts()) {
            actionBar.addTab(actionBar.newTab()
                    .setText(tabHost.getTabLabel())
                    .setTabListener(this));
        }
        TabUtil.setHasEmbeddedTabs(actionBar, getWindowManager(), TabUtil.MIN_SCREEN_WIDTH_FOR_PATIENTDASHBOARDACTIVITY);
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
        getMenuInflater().inflate(R.menu.patients_menu, menu);
        getSupportActionBar().setTitle(mPatient.getDisplay());
        getSupportActionBar().setSubtitle("#" + mPatient.getIdentifier());

        if (!mOpenMRS.getOnlineMode()) {
            menu.findItem(R.id.actionSynchronize).getIcon().setAlpha(ApplicationConstants.DISABLED_ICON_ALPHA);
        }
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.actionSynchronize:
                if (mOpenMRS.getOnlineMode()) {
                    synchronizePatient();
                } else {
                    ToastUtil.showShortToast(this, ToastUtil.ToastType.WARNING, R.string.online_mode_disable);
                }
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

    private void synchronizePatient() {
        showProgressDialog(R.string.action_synchronize_patients, DialogAction.SYNCHRONIZE);
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

            PatientDetailsFragment fragment = (PatientDetailsFragment) getSupportFragmentManager().
                    getFragments().get(PatientDashboardActivity.TabHost.DETAILS_TAB_POS);
            fragment.reloadPatientData(mPatient);

            fvm.findVisitsByPatientUUID(
                    VisitsHelper.createVisitsByPatientUUIDListener(patient.getUuid(), mPatient.getId(), this));
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
        mCustomFragmentDialog.dismiss();
        if (mDialogAction == DialogAction.SYNCHRONIZE) {
            mViewPager.setCurrentItem(TabHost.DETAILS_TAB_POS);
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
            recreateFragmentView(fragments.get(TabHost.VISITS_TAB_POS));
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
        private List<TabHost> mTabHosts;

        public PatientDashboardPagerAdapter(FragmentManager fm, List<TabHost> tabHosts) {
            super(fm);
            mTabHosts = tabHosts;
        }

        public List<TabHost> getTabHosts() {
            return mTabHosts;
        }

        @Override
        public Fragment getItem(int i) {
            switch (i) {
                case TabHost.DETAILS_TAB_POS:
                    return PatientDetailsFragment.newInstance(mPatient);
                case TabHost.DIAGNOSIS_TAB_POS:
                    return PatientDiagnosisFragment.newInstance(mPatient.getId());
                case TabHost.VISITS_TAB_POS:
                    return PatientVisitsFragment.newInstance(mPatient);
                case TabHost.VITALS_TAB_POS:
                    return PatientVitalsFragment.newInstance(mPatient.getId());
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return mTabHosts.size();
        }

    }

    public final class TabHost {
        public static final int DETAILS_TAB_POS = 0;
        public static final int DIAGNOSIS_TAB_POS = 1;
        public static final int VISITS_TAB_POS = 2;
        public static final int VITALS_TAB_POS = 3;

        private Integer mTabPosition;
        private String mTabLabel;

        private TabHost(Integer position, String tabLabel) {
            mTabPosition = position;
            mTabLabel = tabLabel;
        }

        public Integer getTabPosition() {
            return mTabPosition;
        }

        public String getTabLabel() {
            return mTabLabel;
        }
    }
}
