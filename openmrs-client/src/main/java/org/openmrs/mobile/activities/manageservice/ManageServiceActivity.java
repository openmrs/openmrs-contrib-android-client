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
package org.openmrs.mobile.activities.manageservice;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.ACBaseActivity;
import org.openmrs.mobile.application.OpenMRS;


public class ManageServiceActivity extends ACBaseActivity {
    public ManageServiceContract.Presenter mPresenter;
    private MenuItem mAddServicesMenuItem;
    public MenuItem menuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_service);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setElevation(0);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Create fragment
        ManageServiceFragment manageServiceFragment =
                (ManageServiceFragment) getSupportFragmentManager().findFragmentById(R.id.manageServiceContentFrame);
        if (manageServiceFragment == null) {
            manageServiceFragment = ManageServiceFragment.newInstance();
        }
        if (!manageServiceFragment.isActive()) {
            addFragmentToActivity(getSupportFragmentManager(),
                    manageServiceFragment, R.id.manageServiceContentFrame);
        }

        mPresenter = new ManageServicePresenter(manageServiceFragment,mOpenMRS);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.manage_services_menu, menu);
        final SearchView findServicesView;
        mAddServicesMenuItem = menu.findItem(R.id.actionAddServices);
        enableAddServices(OpenMRS.getInstance().getSyncState());

        MenuItem mFindServicesItem = menu.findItem(R.id.actionSearchService);
        findServicesView = (SearchView) mFindServicesItem.getActionView();

        menuItem = mAddServicesMenuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                                                                       @Override
                                                                       public boolean onMenuItemClick(MenuItem item) {
                                                                           ManageServiceFragment manageServiceFragment =(ManageServiceFragment) getSupportFragmentManager().findFragmentById(R.id.manageServiceContentFrame);
                                                                           manageServiceFragment.newDialog();
                                                                           return true;
                                                                       }
                                                                   }

        );
        // Search function
        findServicesView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                findServicesView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                mPresenter.setQuery(query);

                return true;
            }
        });
        return true;
    }



    private void enableAddServices(boolean enabled) {
        int resId = enabled ? R.drawable.ic_add : R.drawable.ic_add_disabled;
        mAddServicesMenuItem.setEnabled(enabled);
        mAddServicesMenuItem.setIcon(resId);
    }

}
