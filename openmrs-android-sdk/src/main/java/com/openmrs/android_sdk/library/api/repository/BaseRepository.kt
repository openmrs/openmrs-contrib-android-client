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
package com.openmrs.android_sdk.library.api.repository

import android.content.Context
import androidx.work.WorkManager
import com.openmrs.android_sdk.library.OpenMRSLogger
import com.openmrs.android_sdk.library.api.RestApi
import com.openmrs.android_sdk.library.databases.AppDatabase
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * The type Base repository.
 */
@Singleton
open class BaseRepository {
    /**
     * The Context.
     * @see Context
     */
    @Inject
    @ApplicationContext
    lateinit var context: Context

    /**
     * The Rest api
     * @see RestApi
     */
    @Inject
    lateinit var restApi: RestApi

    /**
     * The Database instance
     * @see AppDatabase
     */
    @Inject
    lateinit var db: AppDatabase

    /**
     * The Work manager.
     * @see WorkManager
     */
    @Inject
    lateinit var workManager: WorkManager

    /**
     * The Logger.
     * @see OpenMRSLogger
     */
    @Inject
    lateinit var logger: OpenMRSLogger
}
