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

package org.openmrs.mobile.activities;

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
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.openmrs.mobile.R;
import org.openmrs.mobile.adapters.ActiveVisitsArrayAdapter;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.dao.VisitDAO;
import org.openmrs.mobile.utilities.FontsUtil;

public class FindActiveVisitsSearchActivity extends ACBaseActivity {
    private String mQuery;
    private MenuItem mFindActiveVisitItem;
    private static ActiveVisitsArrayAdapter mAdapter;
    private ListView mVisitsListView;
    private TextView mEmptyList;
    private ProgressBar mSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_visits);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mSpinner = (ProgressBar) findViewById(R.id.visitsListViewLoading);
        mVisitsListView = (ListView) findViewById(R.id.visitsListView);
        mEmptyList = (TextView) findViewById(R.id.emptyVisitsListViewLabel);
        mEmptyList.setText(getString(R.string.search_visits_no_results));
        mVisitsListView.setEmptyView(mEmptyList);

        FontsUtil.setFont((ViewGroup) findViewById(android.R.id.content));
        if (getIntent().getAction() == null) {
            getIntent().setAction(Intent.ACTION_SEARCH);
            handleIntent(getIntent());
        } else if (mAdapter != null) {
            mVisitsListView.setAdapter(mAdapter);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    @Override
    public void onBackPressed() {
        if (mFindActiveVisitItem != null) {
            MenuItemCompat.collapseActionView(mFindActiveVisitItem);
        }
        super.onBackPressed();
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            mQuery = intent.getStringExtra(SearchManager.QUERY);
            mEmptyList.setVisibility(View.GONE);
            mVisitsListView.setEmptyView(mSpinner);
            mAdapter = new ActiveVisitsArrayAdapter(this, R.layout.find_visits_row, new VisitDAO().findActiveVisitsByPatientNameLike(mQuery));
            mVisitsListView.setAdapter(mAdapter);
            stopLoader();
            if (mFindActiveVisitItem != null) {
                MenuItemCompat.collapseActionView(mFindActiveVisitItem);
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

        mFindActiveVisitItem = menu.findItem(R.id.actionSearch);
        if (OpenMRS.getInstance().isRunningHoneycombVersionOrHigher()) {
            findPatientView = (SearchView) mFindActiveVisitItem.getActionView();
        } else {
            findPatientView = (SearchView) MenuItemCompat.getActionView(mFindActiveVisitItem);
        }

        SearchableInfo info = searchManager.getSearchableInfo(getComponentName());
        findPatientView.setSearchableInfo(info);
        findPatientView.setIconifiedByDefault(false);
        return true;
    }

    public void stopLoader() {
        mEmptyList.setText(getString(R.string.search_visits_no_results));
        mSpinner.setVisibility(View.GONE);
        mVisitsListView.setEmptyView(mEmptyList);
    }
}
