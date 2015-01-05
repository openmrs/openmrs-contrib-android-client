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

package org.openmrs.client.application;

import android.content.SharedPreferences;
import android.os.Build;

import net.sqlcipher.database.SQLiteDatabase;

import org.odk.collect.android.application.Collect;
import org.openmrs.client.databases.OpenMRSDBOpenHelper;
import org.openmrs.client.security.SecretKeyGenerator;
import org.openmrs.client.utilities.ApplicationConstants;

import java.io.File;

public class OpenMRS extends Collect {

    public static final String OPENMRS_DIR_NAME = "OpenMRS";
    public static final String OPENMRS_DIR_PATH = File.separator + OPENMRS_DIR_NAME;
    private static final String ODK_DIR_PATH = File.separator + "odk";
    private static final String FORMS_DIR_PATH = File.separator + "forms";
    private static final String INSTANCES_DIR_PATH = File.separator + "instances";
    private static final String METADATA_DIR_PATH = File.separator + "metadata";
    private static final String CACHE_DIR_PATH = File.separator + ".cache";

    private static OpenMRS instance;
    private OpenMRSLogger mLogger;
    private String mExternalDirectoryPath;

    @Override
    public void onCreate() {
        initializeSQLCipher();
        super.onCreate();
        instance = this;
        mExternalDirectoryPath = this.getExternalFilesDir(null).toString();
        overrideODKDirs();
        OpenMRS.createODKDirs();
        mLogger = new OpenMRSLogger();
        generateKey();
        OpenMRSDBOpenHelper.init();
    }

    public static OpenMRS getInstance() {
        return instance;
    }

    public SharedPreferences getOpenMRSSharedPreferences() {
        return getSharedPreferences(ApplicationConstants.OpenMRSSharedPreferenceNames.SHARED_PREFERENCES_NAME,
                MODE_PRIVATE);
    }

    public void setUsername(String username) {
        SharedPreferences.Editor editor = getOpenMRSSharedPreferences().edit();
        editor.putString(ApplicationConstants.USER_NAME, username);
        editor.commit();
    }

    public void setServerUrl(String serverUrl) {
        SharedPreferences.Editor editor = getOpenMRSSharedPreferences().edit();
        editor.putString(ApplicationConstants.SERVER_URL, serverUrl);
        editor.commit();
    }

    public void setSessionToken(String serverUrl) {
        SharedPreferences.Editor editor = getOpenMRSSharedPreferences().edit();
        editor.putString(ApplicationConstants.SESSION_TOKEN, serverUrl);
        editor.commit();
    }

    public void setAuthorizationToken(String authorization) {
        SharedPreferences.Editor editor = getOpenMRSSharedPreferences().edit();
        editor.putString(ApplicationConstants.AUTHORIZATION_TOKEN, authorization);
        editor.commit();
    }

    public void setLocation(String location) {
        SharedPreferences.Editor editor = getOpenMRSSharedPreferences().edit();
        editor.putString(ApplicationConstants.LOCATION, location);
        editor.commit();
    }

    public void setVisitTypeUUID(String visitTypeUUID) {
        SharedPreferences.Editor editor = getOpenMRSSharedPreferences().edit();
        editor.putString(ApplicationConstants.VISIT_TYPE_UUID, visitTypeUUID);
        editor.commit();
    }

    public String getUsername() {
        SharedPreferences prefs = getOpenMRSSharedPreferences();
        return prefs.getString(ApplicationConstants.USER_NAME, ApplicationConstants.EMPTY_STRING);
    }

    public String getServerUrl() {
        SharedPreferences prefs = getOpenMRSSharedPreferences();
        return prefs.getString(ApplicationConstants.SERVER_URL, ApplicationConstants.DEFAULT_OPEN_MRS_URL);
    }

    public String getSessionToken() {
        SharedPreferences prefs = getOpenMRSSharedPreferences();
        return prefs.getString(ApplicationConstants.SESSION_TOKEN, ApplicationConstants.EMPTY_STRING);
    }

    public String getAuthorizationToken() {
        SharedPreferences prefs = getOpenMRSSharedPreferences();
        return prefs.getString(ApplicationConstants.AUTHORIZATION_TOKEN, ApplicationConstants.EMPTY_STRING);
    }

    public String getLocation() {
        SharedPreferences prefs = getOpenMRSSharedPreferences();
        return prefs.getString(ApplicationConstants.LOCATION, ApplicationConstants.EMPTY_STRING);
    }

    public String getVisitTypeUUID() {
        SharedPreferences prefs = getOpenMRSSharedPreferences();
        return prefs.getString(ApplicationConstants.VISIT_TYPE_UUID, ApplicationConstants.EMPTY_STRING);
    }

    private void generateKey() {
        // create database key only if not exist
        if (ApplicationConstants.EMPTY_STRING.equals(getSecretKey())) {
            SharedPreferences.Editor editor = getOpenMRSSharedPreferences().edit();
            String key = SecretKeyGenerator.generateKey();
            editor.putString(ApplicationConstants.SECRET_KEY, key);
            editor.commit();
        }
    }

    public String getSecretKey() {
        SharedPreferences prefs = getOpenMRSSharedPreferences();
        return prefs.getString(ApplicationConstants.SECRET_KEY, ApplicationConstants.EMPTY_STRING);
    }

    public OpenMRSLogger getOpenMRSLogger() {
        return mLogger;
    }

    public String getOpenMRSDir() {
        return mExternalDirectoryPath + OPENMRS_DIR_PATH;
    }

    public boolean isRunningHoneycombVersionOrHigher() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
    }

    public boolean isRunningJellyBeanVersionOrHigher() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
    }

    public boolean isRunningKitKatVersionOrHigher() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    }

    private void initializeSQLCipher() {
        SQLiteDatabase.loadLibs(this);
    }

    public void clearUserPreferencesDataWhenLogout() {
        SharedPreferences prefs = OpenMRS.getInstance().getOpenMRSSharedPreferences();
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(ApplicationConstants.SESSION_TOKEN);
        editor.remove(ApplicationConstants.AUTHORIZATION_TOKEN);
        editor.commit();
    }

    public void clearUserPreferencesDataWhenUnauthorized() {
        SharedPreferences prefs = OpenMRS.getInstance().getOpenMRSSharedPreferences();
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(ApplicationConstants.SESSION_TOKEN);
        editor.remove(ApplicationConstants.AUTHORIZATION_TOKEN);
        editor.commit();
    }

    /**
     * Overriding ODK directories path for :
     * - forms
     * - instances
     * - metadata
     */
    public void overrideODKDirs() {
        ODK_ROOT =  mExternalDirectoryPath + OPENMRS_DIR_PATH + ODK_DIR_PATH;
        FORMS_PATH = ODK_ROOT + FORMS_DIR_PATH;
        INSTANCES_PATH = ODK_ROOT + INSTANCES_DIR_PATH;
        METADATA_PATH = ODK_ROOT + METADATA_DIR_PATH;
        CACHE_PATH = ODK_ROOT + CACHE_DIR_PATH;
    }

}
