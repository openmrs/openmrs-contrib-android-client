package org.openmrs.client.databases;

import org.openmrs.client.application.OpenMRS;

public class OpenMRSDBOpenHelper {
    private static OpenMRSDBOpenHelper sInstance;

    private final PatientSQLiteHelper mPatientSQLiteHelper;

    public OpenMRSDBOpenHelper() {
        mPatientSQLiteHelper = new PatientSQLiteHelper(OpenMRS.getInstance());
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
        mPatientSQLiteHelper.close();
    }

    public PatientSQLiteHelper getPatientSQLiteHelper() {
        return mPatientSQLiteHelper;
    }

}
