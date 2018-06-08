package org.openmrs.mobile.activities.providermanager;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.ACBaseActivity;
import org.openmrs.mobile.activities.lastviewedpatients.LastViewedPatientsActivity;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.StringUtils;

public class ProviderManagementActivity extends ACBaseActivity {
    private ProviderManagementPresenter mPresenter;
    private SearchView searchView;
    private String query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provider_management);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setElevation(0);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Create fragment
        ProviderManagementFragment providerManagementFragment =
                (ProviderManagementFragment) getSupportFragmentManager().findFragmentById(R.id.providerManagementContentFrame);
        if (providerManagementFragment == null) {
            providerManagementFragment = ProviderManagementFragment.newInstance();
        }
        if (!providerManagementFragment.isActive()) {
            addFragmentToActivity(getSupportFragmentManager(),
                    providerManagementFragment, R.id.providerManagementContentFrame);
        }

        if(savedInstanceState != null){

            mPresenter = new ProviderManagementPresenter(providerManagementFragment);
        } else {
            mPresenter = new ProviderManagementPresenter(providerManagementFragment);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        int id = item.getItemId();
        switch (id) {
////            case R.id.syncbutton:
//////                enableAddPatient(OpenMRS.getInstance().getSyncState());
////                break;
////            case R.id.actionAddPatients:
////                Intent intent = new Intent(this, LastViewedPatientsActivity.class);
////                startActivity(intent);
//                break;
            case android.R.id.home:
                onBackPressed();
                break;
            default:
                // Do nothing
                break;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.provider_manager_menu, menu);

//        mAddPatientMenuItem = menu.findItem(R.id.actionAddPatients);
//        enableAddPatient(OpenMRS.getInstance().getSyncState());

        // Search function
        MenuItem searchMenuItem = menu.findItem(R.id.actionSearchLocal);
        searchView = (SearchView) searchMenuItem.getActionView();

        if(StringUtils.notEmpty(query)){
            searchMenuItem.expandActionView();
            searchView.setQuery(query, true);
            searchView.clearFocus();
        }
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String query) {
//                mPresenter.setQuery(query);
//                mPresenter.updateLocalPatientsList();
                return true;
            }
        });

        return true;
    }
}
