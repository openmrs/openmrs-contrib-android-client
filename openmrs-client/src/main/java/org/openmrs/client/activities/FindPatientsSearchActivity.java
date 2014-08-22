package org.openmrs.client.activities;

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
import android.widget.ListView;
import android.widget.TextView;

import org.openmrs.client.R;
import org.openmrs.client.adapters.FindPatientArrayAdapter;
import org.openmrs.client.models.Patient;
import org.openmrs.client.net.FindPatientsManager;

import java.util.ArrayList;

public class FindPatientsSearchActivity extends ACBaseActivity {

    private String mQuery;
    private MenuItem mFindPatientMenuItem;
    private ArrayList<Patient> mPatientsList;
    private FindPatientArrayAdapter mAdapter;
    private ListView mPatientsListView;
    private TextView mEmptyList;
    private static final String EMPTY_LIST_SEARCH = "Searching...";
    private static final String EMPTY_LIST_NOT_FOUND = "No results found for query";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_patients);

        mPatientsListView = (ListView) findViewById(R.id.patient_list_view);
        mEmptyList = (TextView) findViewById(R.id.empty_patient_list_view);
        mPatientsListView.setEmptyView(mEmptyList);

        getIntent().setAction(Intent.ACTION_SEARCH);
        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            mEmptyList.setText(EMPTY_LIST_SEARCH);
            mAdapter = new FindPatientArrayAdapter(this, new ArrayList<Patient>());
            mPatientsListView.setAdapter(mAdapter);
            mQuery = intent.getStringExtra(SearchManager.QUERY);
            FindPatientsManager fpm = new FindPatientsManager(this);
            fpm.findPatient(mQuery);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.find_patients_menu, menu);

        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        SearchView findPatientView;

        mFindPatientMenuItem = menu.findItem(R.id.action_search);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            findPatientView = (SearchView) mFindPatientMenuItem.getActionView();
        } else {
            findPatientView = (SearchView) MenuItemCompat.getActionView(mFindPatientMenuItem);
        }

        SearchableInfo info = searchManager.getSearchableInfo(getComponentName());
        findPatientView.setSearchableInfo(info);

        return true;
    }

    public void setPatientsList(ArrayList<Patient> patientsList) {
        mPatientsList = patientsList;
        if (patientsList.size() == 0) {
            mEmptyList.setText(EMPTY_LIST_NOT_FOUND + " \"" + mQuery + "\"");
        }
        mAdapter = new FindPatientArrayAdapter(this, mPatientsList);
        mPatientsListView.setAdapter(mAdapter);
    }

    public void updatePatientsData() {
        mAdapter = new FindPatientArrayAdapter(this, mPatientsList);
        mPatientsListView.setAdapter(mAdapter);
    }
}
