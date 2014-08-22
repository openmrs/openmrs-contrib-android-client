package org.openmrs.client.activities;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import org.openmrs.client.R;
import org.openmrs.client.fragments.PatientFragment;

public class FindPatientsActivity extends ACBaseActivity implements PatientFragment.OnFragmentInteractionListener {

    private String mQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_patients);
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
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        if (MenuItemCompat.expandActionView(mFindPatientMenuItem)) {
            SearchView searchView  =   (SearchView) MenuItemCompat.getActionView(mFindPatientMenuItem);
            searchView.setQuery(mQuery, false);
            searchView.clearFocus();
        }
        return true;
    }


    @Override
    public void onFragmentInteraction(String id) {

    }
}
