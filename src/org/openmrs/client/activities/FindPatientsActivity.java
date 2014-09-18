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
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import org.openmrs.client.R;
import org.openmrs.client.adapters.PatientArrayAdapter;
import org.openmrs.client.application.OpenMRS;
import org.openmrs.client.dao.PatientDAO;
import org.openmrs.client.models.Patient;
import org.openmrs.client.utilities.FontsUtil;

import java.util.List;

public class FindPatientsActivity extends ACBaseActivity {

    private String mQuery;
    private MenuItem mFindPatientMenuItem;
    private PatientArrayAdapter mAdapter;
    private ListView mPatientsListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_patients);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mPatientsListView = (ListView) findViewById(R.id.patient_list_view);
        TextView emptyList = (TextView) findViewById(R.id.empty_patient_list_view);
        emptyList.setText(getString(R.string.search_patient_no_results));
        mPatientsListView.setEmptyView(emptyList);

        FontsUtil.setFont((ViewGroup) findViewById(android.R.id.content));
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

    @Override
    protected void onResume() {
        super.onResume();
        PatientDAO patientDAO = new PatientDAO();
        List<Patient> values = patientDAO.getAllPatients();
        mAdapter = new PatientArrayAdapter(this, R.layout.find_local_patients_row, values);
        mPatientsListView.setAdapter(mAdapter);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            mQuery = intent.getStringExtra(SearchManager.QUERY);
            Intent searchIntent = new Intent(this, FindPatientsSearchActivity.class);
            searchIntent.putExtra(SearchManager.QUERY, mQuery);
            startActivity(searchIntent);
            intent.setAction(null);
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
        return true;
    }
}
