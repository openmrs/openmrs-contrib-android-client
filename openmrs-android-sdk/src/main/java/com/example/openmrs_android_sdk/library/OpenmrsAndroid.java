package com.example.openmrs_android_sdk.library;

import android.content.Context;

import androidx.annotation.Nullable;

import java.io.File;

public class OpenmrsAndroid {
    private volatile static Context instance;
    private static final String OPENMRS_DIR_NAME = "OpenMRS";
    private static final String OPENMRS_DIR_PATH = File.separator + OPENMRS_DIR_NAME;
    private static String externalDirectoryPath;

    private OpenmrsAndroid(){}

    public static void initializeSdk(Context applicationContext){
        if(instance==null) {
            synchronized (OpenmrsAndroid.class){
                if(instance==null){
                    instance=applicationContext;
                    if (externalDirectoryPath == null) {
                        externalDirectoryPath = applicationContext.getExternalFilesDir(null).toString();
                    }
                    System.out.println(instance.toString());
                }
            }
        }
    }

    public static @Nullable Context getInstance(){
        return instance;
    }

    public static String getOpenMRSDir() {
        return externalDirectoryPath + OPENMRS_DIR_PATH;
    }

}
