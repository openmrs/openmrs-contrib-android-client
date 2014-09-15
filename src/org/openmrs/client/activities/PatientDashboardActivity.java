package org.openmrs.client.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.view.Menu;

import org.openmrs.client.R;
import org.openmrs.client.activities.fragments.PatientDetailsFragment;
import org.openmrs.client.activities.fragments.PatientDiagnosisFragment;
import org.openmrs.client.activities.fragments.PatientVisitsFragment;
import org.openmrs.client.activities.fragments.PatientVitalsFragment;
import org.openmrs.client.bundle.PatientDashboardBundle;
import org.openmrs.client.dao.PatientDAO;
import org.openmrs.client.dao.VisitDAO;
import org.openmrs.client.models.Patient;
import org.openmrs.client.models.Visit;
import org.openmrs.client.utilities.ApplicationConstants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PatientDashboardActivity extends ACBaseActivity implements ActionBar.TabListener {

    private Patient mPatient;
    private List<Visit> mPatientVisits;

    private ViewPager mViewPager;
    private PatientDashboardPagerAdapter mPatientDashboardPagerAdapter;

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
        mPatientVisits = new VisitDAO().getVisitsByPatientUUID(mPatient.getId());

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
        getSupportActionBar().setTitle(mPatient.getDisplay());
        return true;
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
            PatientDashboardBundle bundle = new PatientDashboardBundle();
            switch (i) {
                case TabHost.DETAILS_TAB_POS:
                    bundle.setPatient(mPatient);
                    return PatientDetailsFragment.newInstance(bundle);
                case TabHost.DIAGNOSIS_TAB_POS:
                    return new PatientDiagnosisFragment();
                case TabHost.VISITS_TAB_POS:
                    bundle.setPatientVisits(mPatientVisits);
                    return PatientVisitsFragment.newInstance(bundle);
                case TabHost.VITALS_TAB_POS:
                    return new PatientVitalsFragment();
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
