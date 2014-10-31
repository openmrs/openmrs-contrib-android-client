package org.openmrs.client.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;

import org.openmrs.client.R;
import org.openmrs.client.activities.fragments.PatientDetailsFragment;
import org.openmrs.client.activities.fragments.PatientDiagnosisFragment;
import org.openmrs.client.activities.fragments.PatientVisitsFragment;
import org.openmrs.client.activities.fragments.PatientVitalsFragment;
import org.openmrs.client.dao.PatientDAO;
import org.openmrs.client.models.Patient;
import org.openmrs.client.net.FindPatientsManager;
import org.openmrs.client.net.FindVisitsManager;
import org.openmrs.client.utilities.ApplicationConstants;
import org.openmrs.client.utilities.ToastUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PatientDashboardActivity extends ACBaseActivity implements ActionBar.TabListener {

    private Patient mPatient;
    private ViewPager mViewPager;
    private PatientDashboardPagerAdapter mPatientDashboardPagerAdapter;
    private ProgressDialog mDialog;

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

        Bundle patientBundle = getIntent().getExtras();
        mPatient = new PatientDAO().findPatientByUUID(patientBundle.getString(ApplicationConstants.BundleKeys.PATIENT_ID_BUNDLE));

        mPatientDashboardPagerAdapter = new PatientDashboardPagerAdapter(getSupportFragmentManager(), tabHosts);
        initViewPager();
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
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.actionSynchronize:
                synchronizePatient();
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
        mDialog = new ProgressDialog(this);
        mDialog.setMessage(getString(R.string.action_synchronize_patients));
        mDialog.setIndeterminate(false);
        mDialog.setCancelable(false);
        mDialog.show();
        new FindPatientsManager(this).getFullPatientData(mPatient.getUuid());
    }

    public void updatePatientDetailsData(final Patient patient) {
        if (new PatientDAO().updatePatient(mPatient.getId(), patient)) {
            final FindVisitsManager fvm = new FindVisitsManager(this);
            Thread thread = new Thread() {
                @Override
                public void run() {
                    mPatient = new PatientDAO().findPatientByUUID(mPatient.getUuid());

                    PatientDetailsFragment fragment = (PatientDetailsFragment) getSupportFragmentManager().getFragments().get(PatientDashboardActivity.TabHost.DETAILS_TAB_POS);
                    fragment.reloadPatientData(mPatient);

                    fvm.findActiveVisitsForPatientByUUID(patient.getUuid(), mPatient.getId());
                }
            };
            thread.start();

        } else {
            stopLoader(true);
        }
    }

    public void updatePatientVisitsData(boolean errorOccurred) {
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        for (int i = 0; i < fragments.size(); i++) {
            recreateFragmentView(fragments.get(i));
        }
        stopLoader(errorOccurred);
    }

    public void stopLoader(boolean errorOccurred) {
        mDialog.dismiss();
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
    }

    private void recreateFragmentView(final Fragment fragment) {
        FragmentTransaction fragTransaction = getSupportFragmentManager().beginTransaction();
        fragTransaction.detach(fragment);
        fragTransaction.attach(fragment);
        fragTransaction.commit();
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
                    return PatientVisitsFragment.newInstance(mPatient.getId());
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

    private final class TabHost {
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
