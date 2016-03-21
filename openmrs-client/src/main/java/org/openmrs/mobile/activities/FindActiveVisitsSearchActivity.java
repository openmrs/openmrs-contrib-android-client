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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.openmrs.mobile.R;
import org.openmrs.mobile.adapters.ActiveVisitsRecyclerViewAdapter;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.dao.VisitDAO;
import org.openmrs.mobile.utilities.FontsUtil;

public class FindActiveVisitsSearchActivity extends ACBaseActivity {
    private String mQuery;
    private MenuItem mFindActiveVisitItem;
    private static ActiveVisitsRecyclerViewAdapter mAdapter;
    private RecyclerView visitsRecyclerView;
    private TextView mEmptyList;
    private ProgressBar mSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_visits);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mSpinner = (ProgressBar) findViewById(R.id.visitsListViewLoading);
        visitsRecyclerView = (RecyclerView) findViewById(R.id.visitsRecyclerView);
        visitsRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        visitsRecyclerView.setLayoutManager(linearLayoutManager);

        mEmptyList = (TextView) findViewById(R.id.emptyVisitsListViewLabel);
        mEmptyList.setText(getString(R.string.search_visits_no_results));

        FontsUtil.setFont((ViewGroup) findViewById(android.R.id.content));
        if (getIntent().getAction() == null) {
            getIntent().setAction(Intent.ACTION_SEARCH);
            handleIntent(getIntent());
        } else if (mAdapter != null) {
            visitsRecyclerView.setAdapter(mAdapter);
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
            mSpinner.setVisibility(View.VISIBLE);
            mAdapter = new ActiveVisitsRecyclerViewAdapter(this,new VisitDAO().findActiveVisitsByPatientNameLike(mQuery));
            if (new VisitDAO().findActiveVisitsByPatientNameLike(mQuery).isEmpty()){
                mEmptyList.setVisibility(View.VISIBLE);
                mSpinner.setVisibility(View.GONE);
                visitsRecyclerView.setVisibility(View.GONE);
            }
            visitsRecyclerView.setAdapter(mAdapter);
            mSpinner.setVisibility(View.GONE);

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
}