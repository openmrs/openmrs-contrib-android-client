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

import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.ACBaseFragment;
import org.openmrs.mobile.activities.logs.LogsActivity;
import org.openmrs.mobile.services.ConceptDownloadService;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.ToastUtil;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class SettingsFragment extends ACBaseFragment<SettingsContract.Presenter> implements SettingsContract.View {

    private BroadcastReceiver bReceiver;

    private TextView conceptsInDbTextView;
    private ImageButton downloadConceptsButton;
    private SwitchCompat darkModeSwitch;

    private View root;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_settings, container, false);

        bReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mPresenter.updateConceptsInDBTextView();
            }
        };

        setUpConceptsView();
        return root;
    }

    @Override
    public void onPause() {
        super.onPause();
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
        if(text.equals("0")){
            downloadConceptsButton.setEnabled(true);
            ToastUtil.showLongToast(getActivity(),
                    ToastUtil.ToastType.WARNING,
                    R.string.settings_no_concepts_toast);
        }else{
            downloadConceptsButton.setEnabled(false);
        }
        conceptsInDbTextView.setText(text);
    }

    @Override
    public void addLogsInfo(long logSize, String logFilename) {
        LinearLayout logsLl = root.findViewById(R.id.frag_settings_logs_ll);
        TextView logsDesc1Tv = root.findViewById(R.id.frag_settings_logs_desc1_tv);
        TextView logsDesc2Tv = root.findViewById(R.id.frag_settings_logs_desc2_tv);

        logsDesc1Tv.setText(logFilename);
        logsDesc2Tv.setText(getContext().getString(R.string.settings_frag_size) + logSize + "kB");
        logsLl.setOnClickListener(view ->{
            Intent i = new Intent(view.getContext() , LogsActivity.class);
            startActivity(i);
        });
    }

    @Override
    public void setUpConceptsView() {
        conceptsInDbTextView = root.findViewById(R.id.frag_settings_concepts_count_tv);

        downloadConceptsButton = root.findViewById(R.id.frag_settings_concepts_download_btn);

        downloadConceptsButton.setOnClickListener(view -> {
            downloadConceptsButton.setEnabled(false);
            Intent startIntent = new Intent(getActivity(), ConceptDownloadService.class);
            startIntent.setAction(ApplicationConstants.ServiceActions.START_CONCEPT_DOWNLOAD_ACTION);
            Objects.requireNonNull(getActivity()).startService(startIntent);
        });
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

        TextView appName = root.findViewById(R.id.frag_settings_app_name_tv);
        TextView version = root.findViewById(R.id.frag_settings_version_tv);

        appName.setText(getResources().getString(R.string.app_name));
        version.setText(versionName + getContext().getString(R.string.frag_settings_build) + buildVersion);
    }

    @Override
    public void addPrivacyPolicyInfo() {
        LinearLayout privacyPolicyTv = root.findViewById(R.id.frag_settings_privacy_policy_ll);
        privacyPolicyTv.setOnClickListener(view ->{
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(view.getContext().getString(R.string.url_privacy_policy)));
            startActivity(i);
        });
    }

    @Override
    public void rateUs() {
        LinearLayout rateUsLL = root.findViewById(R.id.frag_settings_rate_us_ll);
        rateUsLL.setOnClickListener(v -> {
            Uri uri = Uri.parse("market://details?id=" + ApplicationConstants.PACKAGE_NAME);
            Intent intent = new Intent(Intent.ACTION_VIEW,uri);

            // Ignore Playstore backstack, on back press will take us back to our app
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                    Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK);

            try{
                startActivity(intent);
            }catch (ActivityNotFoundException e){
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://play.google.com/store/apps/details?id=" + ApplicationConstants.PACKAGE_NAME)));
            }
        });

    }

    @Override
    public void setDarkMode() {
        darkModeSwitch = root.findViewById(R.id.frag_settings_dark_mode_switch);
        darkModeSwitch.setChecked(mPresenter.isDarkModeActivated());

        darkModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            mPresenter.setDarkMode(isChecked);
            getActivity().recreate();
        });
    }

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

}