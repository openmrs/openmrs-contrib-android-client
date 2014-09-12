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
    private String mQuery;
    private MenuItem mFindPatientMenuItem;
    private PatientArrayAdapter mAdapter;
    private ListView mPatientsListView;
    private TextView mEmptyList;
    private ProgressBar mSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_patients);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mSpinner = (ProgressBar) findViewById(R.id.patient_list_view_loading);
        mPatientsListView = (ListView) findViewById(R.id.patient_list_view);
        mEmptyList = (TextView) findViewById(R.id.empty_patient_list_view);
        mPatientsListView.setEmptyView(mEmptyList);

        FontsUtil.setFont((ViewGroup) findViewById(android.R.id.content));
        getIntent().setAction(Intent.ACTION_SEARCH);
        handleIntent(getIntent());
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
            mEmptyList.setVisibility(View.GONE);
            mPatientsListView.setEmptyView(mSpinner);
            mAdapter = new PatientArrayAdapter(this, R.layout.find_patients_row, new ArrayList<Patient>());
            mPatientsListView.setAdapter(mAdapter);
            mQuery = intent.getStringExtra(SearchManager.QUERY);
            PatientCacheHelper.clearCache();
            FindPatientsManager fpm = new FindPatientsManager(this);
            fpm.findPatient(mQuery);

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

        mFindPatientMenuItem = menu.findItem(R.id.action_search);
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

    public void updatePatientsData() {
        List<Patient> patientsList = PatientCacheHelper.getCachedPatients();
        if (patientsList.size() == 0) {
            mEmptyList.setText(getString(R.string.search_patient_no_result_for_query, mQuery));
            mSpinner.setVisibility(View.GONE);
            mPatientsListView.setEmptyView(mEmptyList);
        }
        mAdapter = new PatientArrayAdapter(this, R.layout.find_patients_row, patientsList);
        mPatientsListView.setAdapter(mAdapter);
    }

    public void stopLoader() {
        mEmptyList.setText(getString(R.string.search_patient_no_result_for_query, mQuery));
        mSpinner.setVisibility(View.GONE);
        mPatientsListView.setEmptyView(mEmptyList);
    }
}
