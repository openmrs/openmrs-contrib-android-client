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

package org.openmrs.mobile.activities.manageappointment;

import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.ACBaseActivity;
import org.openmrs.mobile.application.OpenMRS;

public class ManageAppointmentActivity extends ACBaseActivity {

    private ManageAppointmentContract.Presenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_manage_appointment);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Create fragment
        ManageAppointmentFragment manageAppointmentFragment =
                (ManageAppointmentFragment) getSupportFragmentManager().findFragmentById(R.id.manageAppointmentContentFrame);
        if (manageAppointmentFragment == null) {
            manageAppointmentFragment = ManageAppointmentFragment.newInstance();
        }
        if (!manageAppointmentFragment.isActive()) {
            addFragmentToActivity(getSupportFragmentManager(),
                    manageAppointmentFragment, R.id.manageAppointmentContentFrame);
        }

        // Create the presenter
        mPresenter = new ManageAppointmentPresenter(manageAppointmentFragment);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.manage_appointment_menu, menu);
        final SearchView findAppointmentView;
        MenuItem mFindAppointmentItem = menu.findItem(R.id.actionSearchRemoteAppointmentEntry);
        if (OpenMRS.getInstance().isRunningHoneycombVersionOrHigher()) {
            findAppointmentView = (SearchView) mFindAppointmentItem.getActionView();
        } else {
            findAppointmentView = (SearchView) MenuItemCompat.getActionView(mFindAppointmentItem);
        }

        // Search function
        findAppointmentView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                findAppointmentView.clearFocus();
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

}
