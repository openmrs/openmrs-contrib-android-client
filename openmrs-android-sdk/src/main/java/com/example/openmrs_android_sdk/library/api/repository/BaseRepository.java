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

package com.example.openmrs_android_sdk.library.api.repository;

import android.content.Context;

import androidx.work.WorkManager;

import com.example.openmrs_android_sdk.library.OpenMRSLogger;
import com.example.openmrs_android_sdk.library.OpenmrsAndroid;
import com.example.openmrs_android_sdk.library.api.RestApi;
import com.example.openmrs_android_sdk.library.api.RestServiceBuilder;
import com.example.openmrs_android_sdk.library.databases.AppDatabase;

public abstract class BaseRepository {
    protected Context context;
    protected RestApi restApi;
    protected AppDatabase db;
    protected WorkManager workManager;
    protected OpenMRSLogger logger;

    public BaseRepository() {
        this.context = OpenmrsAndroid.getInstance();
        this.restApi = RestServiceBuilder.createService(RestApi.class);
        this.db = AppDatabase.getDatabase(context);
        this.workManager = WorkManager.getInstance(context);
        this.logger = new OpenMRSLogger();
    }

    public BaseRepository(RestApi restApi, OpenMRSLogger logger) {
        this.logger = logger;
        this.restApi = restApi;
        this.context = OpenmrsAndroid.getInstance();
    }
}
