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

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.ACBaseFragment;
import org.openmrs.mobile.models.SettingsListItemDTO;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class SettingsFragment extends ACBaseFragment<SettingsContract.Presenter> implements SettingsContract.View {

    private List<SettingsListItemDTO> mListItem = new ArrayList<>();
    private TextView mlogsTV;
    private RecyclerView settingsRecyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_settings, container, false);

        settingsRecyclerView = (RecyclerView) root.findViewById(R.id.settingsRecyclerView);
        settingsRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        settingsRecyclerView.setLayoutManager(linearLayoutManager);

        return root;
    }

    @Override
    public void onPause() {
        super.onPause();
        mListItem = new ArrayList<>();
    }

    @Override
    public void addLogsInfo(long logSize, String logFilename,TextView logsTV) {
        mlogsTV = logsTV;
        String aBuffer = "";
        try {
                File myFile = new File(logFilename);
                FileInputStream fIn = new FileInputStream(myFile);
                BufferedReader myReader = new BufferedReader(new InputStreamReader(fIn));
                String aDataRow;
                while ((aDataRow = myReader.readLine()) != null) {
                        aBuffer += aDataRow;
                    }
                myReader.close();
                mlogsTV.setText(aBuffer);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

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
        SettingsRecyclerViewAdapter adapter = new SettingsRecyclerViewAdapter(mListItem , mlogsTV);
        settingsRecyclerView.setAdapter(adapter);
    }

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

}