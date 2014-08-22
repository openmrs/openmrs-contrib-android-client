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
import org.openmrs.client.database.PatientDataSource;
import org.openmrs.client.models.Patient;

import java.util.List;

public class FindPatientsActivity extends ACBaseActivity {

    private String mQuery;
    private MenuItem mFindPatientMenuItem;
    private FindPatientArrayAdapter mAdapter;
    private ListView mPatientsListView;
    private PatientDataSource mDatasource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_patients);

        mDatasource = PatientDataSource.getDataSource(this);
        mDatasource.open("openMRS");

        List<Patient> values = mDatasource.getAllPatients();

        mPatientsListView = (ListView) findViewById(R.id.patient_list_view);
        TextView emptyList = (TextView) findViewById(R.id.empty_patient_list_view);
        emptyList.setText("No patients in database");
        mPatientsListView.setEmptyView(emptyList);

        mAdapter = new FindPatientArrayAdapter(this, values);
        mPatientsListView.setAdapter(mAdapter);

        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            mQuery = intent.getStringExtra(SearchManager.QUERY);
            Intent searchIntent = new Intent(this, FindPatientsSearchActivity.class);
            searchIntent.putExtra(SearchManager.QUERY, mQuery);
            startActivityForResult(searchIntent, 1);
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
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            findPatientView = (SearchView) mFindPatientMenuItem.getActionView();
        } else {
            findPatientView = (SearchView) MenuItemCompat.getActionView(mFindPatientMenuItem);
        }

        SearchableInfo info = searchManager.getSearchableInfo(getComponentName());
        findPatientView.setSearchableInfo(info);

        return true;
    }
}
