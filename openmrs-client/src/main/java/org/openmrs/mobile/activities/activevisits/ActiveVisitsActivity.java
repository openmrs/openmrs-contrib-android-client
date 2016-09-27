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

package org.openmrs.mobile.activities.activevisits;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.ACBaseActivity;
import org.openmrs.mobile.application.OpenMRS;

public class ActiveVisitsActivity extends ACBaseActivity {

    private ActiveVisitsContract.Presenter mPresenter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_visits);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setElevation(0);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Create fragment
        ActiveVisitsFragment activeVisitsFragment =
                (ActiveVisitsFragment) getSupportFragmentManager().findFragmentById(R.id.activeVisitContentFrame);
        if (activeVisitsFragment == null) {
            activeVisitsFragment = ActiveVisitsFragment.newInstance();
        }
        addFragmentToActivity(getSupportFragmentManager(),
                activeVisitsFragment, R.id.activeVisitContentFrame);

        // Create the presenter
        mPresenter = new ActiveVisitPresenter(activeVisitsFragment);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.find_visits_menu, menu);
        final SearchView findVisitView;
        MenuItem mFindVisitItem = menu.findItem(R.id.actionSearchLocalVisits);
        if (OpenMRS.getInstance().isRunningHoneycombVersionOrHigher()) {
            findVisitView = (SearchView) mFindVisitItem.getActionView();
        } else {
            findVisitView = (SearchView) MenuItemCompat.getActionView(mFindVisitItem);
        }

        findVisitView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                findVisitView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                mPresenter.updateVisitsInDatabaseList(query);
                return true;
            }
        });
        return true;
    }


}
