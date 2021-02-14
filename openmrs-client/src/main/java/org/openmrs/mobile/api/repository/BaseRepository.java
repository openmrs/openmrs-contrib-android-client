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

package org.openmrs.mobile.api.repository;

import androidx.work.WorkManager;

import org.openmrs.mobile.api.RestApi;
import org.openmrs.mobile.api.RestServiceBuilder;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.application.OpenMRSLogger;
import org.openmrs.mobile.databases.AppDatabase;

public abstract class BaseRepository {
    protected OpenMRS openMrs;
    protected RestApi restApi;
    protected AppDatabase db;
    protected WorkManager workManager;
    protected OpenMRSLogger logger;

    public BaseRepository() {
        this.openMrs = OpenMRS.getInstance();
        this.restApi = RestServiceBuilder.createService(RestApi.class);
        this.db = AppDatabase.getDatabase(openMrs);
        this.workManager = WorkManager.getInstance(openMrs);
        this.logger = new OpenMRSLogger();
    }

    public BaseRepository(OpenMRS openMrs, RestApi restApi) {
        this.openMrs = openMrs;
        this.restApi = restApi;
    }

    public BaseRepository(OpenMRS openMRS, RestApi restApi, OpenMRSLogger logger) {
        this.logger = logger;
        this.openMrs = openMRS;
        this.restApi = restApi;
    }
}
