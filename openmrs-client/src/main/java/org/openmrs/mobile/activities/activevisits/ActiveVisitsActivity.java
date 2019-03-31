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

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.ACBaseActivity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.SearchView;

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
        if (!activeVisitsFragment.isActive()) {
            addFragmentToActivity(getSupportFragmentManager(),
                    activeVisitsFragment, R.id.activeVisitContentFrame);
        }

        // Create the presenter
        mPresenter = new ActiveVisitPresenter(activeVisitsFragment);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.find_visits_menu, menu);
        final SearchView findVisitView;
        MenuItem mFindVisitItem = menu.findItem(R.id.actionSearchLocalVisits);
        findVisitView = (SearchView) mFindVisitItem.getActionView();

        findVisitView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                findVisitView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                if (!query.isEmpty()) {
                    mPresenter.updateVisitsInDatabaseList(query);
                }
                else {
                    mPresenter.updateVisitsInDatabaseList();
                }
                return true;
            }
        });
        return true;
    }


}
