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

package org.openmrs.mobile.activities.providermanagerdashboard;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.SearchView;

import com.openmrs.android_sdk.utilities.StringUtils;
import com.google.android.material.snackbar.Snackbar;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.ACBaseActivity;
import org.openmrs.mobile.databinding.ActivityProviderManagementBinding;

public class ProviderManagerDashboardActivity extends ACBaseActivity {
    ProviderManagerDashboardFragment providerManagerDashboardFragment;
    private SearchView searchView;
    private String query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityProviderManagementBinding binding = ActivityProviderManagementBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setElevation(0);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.provider_manager);
        }

        // Create fragment
        providerManagerDashboardFragment =
            (ProviderManagerDashboardFragment) getSupportFragmentManager().findFragmentById(binding.providerManagementContentFrame.getId());
        if (providerManagerDashboardFragment == null) {
            providerManagerDashboardFragment = ProviderManagerDashboardFragment.newInstance();
        }
        if (!providerManagerDashboardFragment.isActive()) {
            addFragmentToActivity(getSupportFragmentManager(),
                providerManagerDashboardFragment, binding.providerManagementContentFrame.getId());
        }

        ProviderManagerDashboardPresenter mPresenter = new ProviderManagerDashboardPresenter(providerManagerDashboardFragment);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        int id = item.getItemId();
        switch (id) {
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
    public void showNoInternetConnectionSnackbar() {
        mSnackbar = Snackbar.make(providerManagerDashboardFragment.addProviderFab,
            getString(R.string.no_internet_connection_message), Snackbar.LENGTH_INDEFINITE);
        View sbView = mSnackbar.getView();
        TextView textView = sbView.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        mSnackbar.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.provider_manager_menu, menu);

        // Search function
        MenuItem searchMenuItem = menu.findItem(R.id.actionSearchLocal);
        searchView = (SearchView) searchMenuItem.getActionView();

        if (StringUtils.notEmpty(query)) {
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
                providerManagerDashboardFragment.filterProviders(query);
                return true;
            }
        });

        return true;
    }
}
