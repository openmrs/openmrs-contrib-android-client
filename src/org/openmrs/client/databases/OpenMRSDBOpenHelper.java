package org.openmrs.client.databases;

import org.openmrs.client.application.OpenMRS;

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
