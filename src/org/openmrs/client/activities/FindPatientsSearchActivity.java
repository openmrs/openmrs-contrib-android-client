package org.openmrs.client.activities;

import android.annotation.TargetApi;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.openmrs.client.R;
import org.openmrs.client.adapters.PatientArrayAdapter;
import org.openmrs.client.application.OpenMRS;
import org.openmrs.client.models.Patient;
import org.openmrs.client.net.FindPatientsManager;
import org.openmrs.client.utilities.FontsUtil;
import org.openmrs.client.utilities.PatientCacheHelper;

import java.util.ArrayList;
import java.util.List;

public class FindPatientsSearchActivity extends ACBaseActivity {
    private static String mLastQuery;
    private MenuItem mFindPatientMenuItem;
    private static PatientArrayAdapter mAdapter;
    private ListView mPatientsListView;
    private TextView mEmptyList;
    private ProgressBar mSpinner;
    private static boolean searching;
    private static int lastSearchId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_find_patients);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mSpinner = (ProgressBar) findViewById(R.id.patientListViewLoading);
        mPatientsListView = (ListView) findViewById(R.id.patientListView);
        mEmptyList = (TextView) findViewById(R.id.emptyPatientListView);

        FontsUtil.setFont((ViewGroup) findViewById(android.R.id.content));
        if (getIntent().getAction() == null || searching) {
            getIntent().setAction(Intent.ACTION_SEARCH);
            handleIntent(getIntent());
        } else if (mAdapter != null) {
            mPatientsListView.setAdapter(mAdapter);
            if (mAdapter.getCount() == 0) {
                mEmptyList.setText(getString(R.string.search_patient_no_result_for_query, mLastQuery));
                mPatientsListView.setEmptyView(mEmptyList);
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    @Override
    public void onBackPressed() {
        if (mFindPatientMenuItem != null) {
            MenuItemCompat.collapseActionView(mFindPatientMenuItem);
        }
        super.onBackPressed();
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            searching = true;
            lastSearchId++;
            mEmptyList.setVisibility(View.GONE);
            mPatientsListView.setEmptyView(mSpinner);
            mAdapter = new PatientArrayAdapter(this, R.layout.find_patients_row, new ArrayList<Patient>());
            mPatientsListView.setAdapter(mAdapter);
            mLastQuery = intent.getStringExtra(SearchManager.QUERY);
            PatientCacheHelper.clearCache();
            PatientCacheHelper.setId(lastSearchId);
            FindPatientsManager fpm = new FindPatientsManager(this);
            fpm.findPatient(mLastQuery, lastSearchId);

            if (mFindPatientMenuItem != null) {
                MenuItemCompat.collapseActionView(mFindPatientMenuItem);
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.find_patients_menu, menu);

        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        SearchView findPatientView;

        mFindPatientMenuItem = menu.findItem(R.id.actionSearch);
        if (OpenMRS.getInstance().isRunningHoneycombVersionOrHigher()) {
            findPatientView = (SearchView) mFindPatientMenuItem.getActionView();
        } else {
            findPatientView = (SearchView) MenuItemCompat.getActionView(mFindPatientMenuItem);
        }

        SearchableInfo info = searchManager.getSearchableInfo(getComponentName());
        findPatientView.setSearchableInfo(info);
        findPatientView.setIconifiedByDefault(false);
        return true;
    }

    public void updatePatientsData(int searchId) {
        if (lastSearchId == searchId) {
            List<Patient> patientsList = PatientCacheHelper.getCachedPatients();
            if (patientsList.size() == 0) {
                mEmptyList.setText(getString(R.string.search_patient_no_result_for_query, mLastQuery));
                mSpinner.setVisibility(View.GONE);
                mPatientsListView.setEmptyView(mEmptyList);
            }
            mAdapter = new PatientArrayAdapter(this, R.layout.find_patients_row, patientsList);
            mPatientsListView.setAdapter(mAdapter);
            searching = false;
        }
    }

    public void stopLoader(int searchId) {
        if (lastSearchId == searchId) {
            searching = false;
            mEmptyList.setText(getString(R.string.search_patient_no_result_for_query, mLastQuery));
            mSpinner.setVisibility(View.GONE);
            mPatientsListView.setEmptyView(mEmptyList);
        }
    }
}
