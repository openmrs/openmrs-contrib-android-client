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

package org.openmrs.mobile.application;

import android.content.Intent;
import android.os.Build;

import androidx.multidex.MultiDexApplication;

import com.openmrs.android_sdk.library.OpenMRSLogger;
import com.openmrs.android_sdk.library.OpenmrsAndroid;

import org.openmrs.mobile.services.FormListService;
import org.openmrs.mobile.services.AuthenticateCheckService;

import java.io.File;

public class OpenMRS extends MultiDexApplication {
    private static final String OPENMRS_DIR_NAME = "OpenMRS";
    private static final String OPENMRS_DIR_PATH = File.separator + OPENMRS_DIR_NAME;
    private static String mExternalDirectoryPath;
    private static OpenMRS instance;
    private OpenMRSLogger mLogger;

    public static OpenMRS getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        OpenmrsAndroid.initializeSdk(this);

        if (mExternalDirectoryPath == null) {
            mExternalDirectoryPath = this.getExternalFilesDir(null).toString();
        }
        mLogger = new OpenMRSLogger();
        Intent i = new Intent(this, FormListService.class);
        startService(i);
        Intent intent = new Intent(this, AuthenticateCheckService.class);
        startService(intent);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    public boolean isRunningKitKatVersionOrHigher() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    }
}
