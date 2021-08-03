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

package com.openmrs.android_sdk.library.api.repository;

import android.content.Context;

import androidx.work.WorkManager;

import com.openmrs.android_sdk.library.OpenMRSLogger;
import com.openmrs.android_sdk.library.OpenmrsAndroid;
import com.openmrs.android_sdk.library.api.RestApi;
import com.openmrs.android_sdk.library.api.RestServiceBuilder;
import com.openmrs.android_sdk.library.databases.AppDatabase;

/**
 * The type Base repository.
 */
public abstract class BaseRepository {
    /**
     * The Context.
     * @see Context
     */
    protected Context context;
    /**
     * The Rest api
     * @see RestApi
     */
    protected RestApi restApi;
    /**
     * The Database instance
     * @see AppDatabase
     */
    protected AppDatabase db;
    /**
     * The Work manager.
     * @see WorkManager
     */
    protected WorkManager workManager;
    /**
     * The Logger.
     * @see OpenMRSLogger
     */
    protected OpenMRSLogger logger;

    /**
     * Instantiates a new Base repository.
     */
    public BaseRepository() {
        this.context = OpenmrsAndroid.getInstance();
        this.restApi = RestServiceBuilder.createService(RestApi.class);
        this.db = AppDatabase.getDatabase(context);
        this.workManager = WorkManager.getInstance(context);
        this.logger = new OpenMRSLogger();
    }

    /**
     * Instantiates a new Base repository.
     *
     * @param restApi the rest api
     * @param logger  the logger
     */
    public BaseRepository(RestApi restApi, OpenMRSLogger logger) {
        this.logger = logger;
        this.restApi = restApi;
        this.context = OpenmrsAndroid.getInstance();
    }
}
