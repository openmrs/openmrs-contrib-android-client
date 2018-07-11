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
package org.openmrs.mobile.activities.manageappointmentblocks;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.ACBaseActivity;
import org.openmrs.mobile.application.OpenMRS;

public class ManageAppointmentBlocksActivity extends ACBaseActivity {
    public ManageAppointmentBlocksContract.Presenter mPresenter;
    private MenuItem mAddBlocksMenuItem;
    public MenuItem menuItem;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_appointment_blocks);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setElevation(0);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Create fragment
        ManageAppointmentBlocksFragment manageAppointmentBlocksFragment =
                (ManageAppointmentBlocksFragment) getSupportFragmentManager().findFragmentById(R.id.manageAppointmentBlocksContentFrame);
        if (manageAppointmentBlocksFragment == null) {
            manageAppointmentBlocksFragment = ManageAppointmentBlocksFragment.newInstance();
        }
        if (!manageAppointmentBlocksFragment.isActive()) {
            addFragmentToActivity(getSupportFragmentManager(),
                    manageAppointmentBlocksFragment, R.id.manageAppointmentBlocksContentFrame);
        }

        mPresenter = new ManageAppointmentBlocksPresenter(manageAppointmentBlocksFragment);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.manage_services_menu, menu);
        final SearchView findServicesView;
        mAddBlocksMenuItem = menu.findItem(R.id.actionAddServices);
        enableAddServices(OpenMRS.getInstance().getSyncState());

        MenuItem mFindServicesItem = menu.findItem(R.id.actionSearchService);
            findServicesView = (SearchView) mFindServicesItem.getActionView();
        menuItem = mAddBlocksMenuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                                                                       @Override
                                                                       public boolean onMenuItemClick(MenuItem item) {
                                                                           ManageAppointmentBlocksFragment manageAppointmentBlocksFragment =(ManageAppointmentBlocksFragment) getSupportFragmentManager().findFragmentById(R.id.manageAppointmentBlocksContentFrame);
                                                                           manageAppointmentBlocksFragment.newForm();
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


                return true;
            }
        });
        return true;
    }



    private void enableAddServices(boolean enabled) {
        int resId = enabled ? R.drawable.ic_add : R.drawable.ic_add_disabled;
        mAddBlocksMenuItem.setEnabled(enabled);
        mAddBlocksMenuItem.setIcon(resId);
    }

}
