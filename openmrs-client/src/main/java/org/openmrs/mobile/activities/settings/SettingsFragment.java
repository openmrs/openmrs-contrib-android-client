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


package org.openmrs.mobile.activities.settings;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.ACBaseFragment;
import org.openmrs.mobile.models.SettingsListItemDTO;
import org.openmrs.mobile.services.ConceptDownloadService;
import org.openmrs.mobile.utilities.ApplicationConstants;

import java.util.ArrayList;
import java.util.List;

public class SettingsFragment extends ACBaseFragment<SettingsContract.Presenter> implements SettingsContract.View {

    private List<SettingsListItemDTO> mListItem = new ArrayList<>();
    private RecyclerView settingsRecyclerView;

    private BroadcastReceiver bReceiver;

    private TextView conceptsInDbTextView;
    private ImageButton downloadConceptsButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_settings, container, false);

        bReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mPresenter.updateConceptsInDBTextView();
            }
        };

        settingsRecyclerView = (RecyclerView) root.findViewById(R.id.settingsRecyclerView);
        settingsRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        settingsRecyclerView.setLayoutManager(linearLayoutManager);

        conceptsInDbTextView = ((TextView) root.findViewById(R.id.conceptsInDbTextView));
        downloadConceptsButton = ((ImageButton) root.findViewById(R.id.downloadConceptsButton));

        downloadConceptsButton.setOnClickListener(view -> {
            downloadConceptsButton.setEnabled(false);
            Intent startIntent = new Intent(getActivity(), ConceptDownloadService.class);
            startIntent.setAction(ApplicationConstants.ServiceActions.START_CONCEPT_DOWNLOAD_ACTION);
            getActivity().startService(startIntent);
        });

        return root;
    }

    @Override
    public void onPause() {
        super.onPause();
        mListItem = new ArrayList<>();
        LocalBroadcastManager.getInstance(this.getActivity()).unregisterReceiver(bReceiver);
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.updateConceptsInDBTextView();
        LocalBroadcastManager.getInstance(this.getActivity()).registerReceiver(bReceiver, new IntentFilter(ApplicationConstants.BroadcastActions.CONCEPT_DOWNLOAD_BROADCAST_INTENT_ID));
    }

    @Override
    public void setConceptsInDbText(String text) {
        conceptsInDbTextView.setText(text);
    }

    @Override
    public void addLogsInfo(long logSize, String logFilename) {


        mListItem.add(new SettingsListItemDTO(getResources().getString(R.string.settings_logs),
                logFilename,
                "Size: " + logSize + "kB"));
    }

    @Override
    public void addBuildVersionInfo() {
        String versionName = "";
        int buildVersion = 0;

        PackageManager packageManager = this.getActivity().getPackageManager();
        String packageName = this.getActivity().getPackageName();

        try {
            versionName = packageManager.getPackageInfo(packageName, 0).versionName;
            ApplicationInfo ai = packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA);
            buildVersion = ai.metaData.getInt("buildVersion");
        } catch (PackageManager.NameNotFoundException e) {
            mPresenter.logException("Failed to load meta-data, NameNotFound: " + e.getMessage());
        } catch (NullPointerException e) {
            mPresenter.logException("Failed to load meta-data, NullPointer: " + e.getMessage());
        }

        mListItem.add(new SettingsListItemDTO(getResources().getString(R.string.settings_about),
                getResources().getString(R.string.app_name),
                versionName + " Build: " + buildVersion));
    }

    @Override
    public void applyChanges() {
        SettingsRecyclerViewAdapter adapter = new SettingsRecyclerViewAdapter(mListItem);
        settingsRecyclerView.setAdapter(adapter);
    }

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

}