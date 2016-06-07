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

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import org.openmrs.mobile.R;
import org.openmrs.mobile.adapters.SettingsRecyclerViewAdapter;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.models.SettingsListItemDTO;
import org.openmrs.mobile.net.AuthorizationManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends ACBaseActivity {
    private static final int ONE_KB = 1024;

    private RecyclerView settingsRecyclerView;
    private List<SettingsListItemDTO> mListItem = new ArrayList<SettingsListItemDTO>();

    private AuthorizationManager mAuthorizationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);


        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        }
        mOpenMRSLogger.d("onCreate");
        fillList();
        settingsRecyclerView = (RecyclerView) findViewById(R.id.settingsRecyclerView);
        settingsRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        settingsRecyclerView.setLayoutManager(linearLayoutManager);

        SettingsRecyclerViewAdapter mAdapter = new SettingsRecyclerViewAdapter(this, mListItem);
        settingsRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (null == mAuthorizationManager) {
            mAuthorizationManager = new AuthorizationManager();
        }
    }

    private void fillList() {
//        mListItem.add(new SettingsListItemDTO(getResources().getString(R.string.settings_downloadForms)));

        long size = 0;
        String filename = OpenMRS.getInstance().getOpenMRSDir()
                + File.separator + mOpenMRSLogger.getLogFilename();
        try {
            File file = new File(filename);
            size = file.length();
            size = size / ONE_KB;
            mOpenMRSLogger.i("File Path : " + file.getPath() + ", File size: " + size + " KB");
        } catch (Exception e) {
            mOpenMRSLogger.w("File not found");
        }

        mListItem.add(new SettingsListItemDTO(getResources().getString(R.string.settings_logs),
                filename,
                "Size: " + size + "kB"));

        String versionName = "";
        int buildVersion = 0;

        try {
            versionName = this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName;

            ApplicationInfo ai = getPackageManager().getApplicationInfo(this.getPackageName(), PackageManager.GET_META_DATA);
            buildVersion = ai.metaData.getInt("buildVersion");
        } catch (PackageManager.NameNotFoundException e) {
            mOpenMRSLogger.e("Failed to load meta-data, NameNotFound: " + e.getMessage());
        } catch (NullPointerException e) {
            mOpenMRSLogger.e("Failed to load meta-data, NullPointer: " + e.getMessage());
        }

        mListItem.add(new SettingsListItemDTO(getResources().getString(R.string.settings_about),
                getResources().getString(R.string.app_name),
                versionName + " Build: " + buildVersion));
    }
}
