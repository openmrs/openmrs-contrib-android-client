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

package org.openmrs.mobile.databases;

import org.openmrs.mobile.application.OpenMRS;

public class OpenMRSDBOpenHelper {
    private static OpenMRSDBOpenHelper sInstance;

    private final DBOpenHelper mDBOpenHelper;

    public OpenMRSDBOpenHelper() {
        mDBOpenHelper = new DBOpenHelper(OpenMRS.getInstance());
    }

    public static void init() {
        if (null == sInstance) {
            sInstance = new OpenMRSDBOpenHelper();
        }
    }

    public static OpenMRSDBOpenHelper getInstance() {
        if (null == sInstance) {
            init();
        }
        return sInstance;
    }

    public void closeDatabases() {
        mDBOpenHelper.close();
    }

    public DBOpenHelper getDBOpenHelper() {
        return mDBOpenHelper;
    }

}
