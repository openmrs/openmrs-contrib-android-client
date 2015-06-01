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
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.ToggleButton;

import org.openmrs.mobile.R;
import org.openmrs.mobile.adapters.SettingsArrayAdapter;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.models.SettingsListItemDTO;
import org.openmrs.mobile.net.AuthorizationManager;
import org.openmrs.mobile.net.OfflineRequestManager;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.NetworkUtils;
import org.openmrs.mobile.utilities.ToastUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends ACBaseActivity {
    private static final int ONE_KB = 1024;

    private ListView mSettingsListView;
    private List<SettingsListItemDTO> mListItem;
    private OpenMRS mOpenMRS = OpenMRS.getInstance();

    private AuthorizationManager mAuthorizationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        mOpenMRSLogger.d("onCreate");
        mSettingsListView = (ListView) findViewById(R.id.settingsListView);
        setListView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (null == mAuthorizationManager) {
            mAuthorizationManager = new AuthorizationManager();
        }
    }

    public void setListView() {
        fillList();
        SettingsArrayAdapter mAdapter = new SettingsArrayAdapter(this, mListItem);
        mSettingsListView.setAdapter(mAdapter);
    }

    private void fillList() {
        mListItem = new ArrayList<SettingsListItemDTO>();
        mListItem.add(new SettingsListItemDTO(getResources().getString(R.string.settings_downloadForms)));
        mListItem.add(new SettingsListItemDTO(getResources().getString(R.string.settings_online_mode), true));

        mListItem.add(new SettingsListItemDTO(getString(R.string.settings_synchronize_requests, mOpenMRS.getOfflineRequestQueue().size())));

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
                getString(R.string.settings_file_size, size)));

        String versionName = ApplicationConstants.EMPTY_STRING;
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

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public void onStateChanged(View view) {
        boolean isChecked;
        if (OpenMRS.getInstance().isRunningIceCreamVersionOrHigher()) {
            isChecked = ((Switch) view).isChecked();
        } else {
            isChecked = ((ToggleButton) view).isChecked();
        }
        if (isChecked) {
            if (NetworkUtils.isNetworkAvailable(this)) {
                ToastUtil.showShortToast(this, ToastUtil.ToastType.SUCCESS, getString(R.string.settings_online_mode_on));
                sendAllOldRequestOneByOne();
            } else {
                if (OpenMRS.getInstance().isRunningIceCreamVersionOrHigher()) {
                    ((Switch) view).setChecked(false);
                } else {
                    ((ToggleButton) view).setChecked(false);
                }
                ToastUtil.showShortToast(this, ToastUtil.ToastType.ERROR, getString(R.string.settings_online_mode_not_available));
            }
        } else {
            ToastUtil.showShortToast(this, ToastUtil.ToastType.WARNING, getString(R.string.settings_online_mode_off));
        }
        mOpenMRS.setOnlineMode(isChecked);
        mOpenMRS.setRequestQueueActive(mOpenMRS.getRequestQueue(), isChecked);
        setListView();
        onResume();
    }

    public void sendAllOldRequestOneByOne() {
        OfflineRequestManager orm = new OfflineRequestManager(this);
        orm.sendAllOldRequestOneByOne();
    }
}
