package com.example.openmrs_android_sdk.library;

import android.content.Context;

import androidx.annotation.Nullable;

public class OpenmrsAndroid {
    private volatile static Context instance;
    private static String baseUrl;
    private OpenmrsAndroid(){}

    public static void initializeSdk(Context applicationContext){
        if(instance==null) {
            synchronized (OpenmrsAndroid.class){
                if(instance==null){
                    instance=applicationContext;
                    System.out.println(instance.toString());
                }
            }
        }
    }

    public static @Nullable Context getContext(){
        return instance;
    }
}
