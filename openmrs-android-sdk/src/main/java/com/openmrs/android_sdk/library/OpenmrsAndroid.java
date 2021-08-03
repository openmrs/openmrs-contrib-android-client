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

package com.openmrs.android_sdk.library;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.annotation.Nullable;

import com.openmrs.android_sdk.utilities.ApplicationConstants;

import org.mindrot.jbcrypt.BCrypt;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

/**
 * The type Openmrs android.
 */
public class OpenmrsAndroid {
    private volatile static Context instance;
    private static final String OPENMRS_DIR_NAME = "OpenMRS";
    private static final String OPENMRS_DIR_PATH = File.separator + OPENMRS_DIR_NAME;
    private static String externalDirectoryPath;
    private static String secretKey;

    private static OpenMRSLogger logger = new OpenMRSLogger();

    private OpenmrsAndroid() {
    }

    /**
     * Initialize sdk.
     *
     * @param applicationContext the application context
     */
    public static void initializeSdk(Context applicationContext) {
        if (instance == null) {
            synchronized (OpenmrsAndroid.class) {
                if (instance == null) {
                    instance = applicationContext;
                    if (externalDirectoryPath == null) {
                        externalDirectoryPath = applicationContext.getExternalFilesDir(null).toString();
                    }
                    System.out.println(instance.toString());
                }
            }
        }
    }

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static @Nullable
    Context getInstance() {
        return instance;
    }

    /**
     * Gets open mrs dir.
     *
     * @return the open mrs dir
     */
    public static String getOpenMRSDir() {
        return externalDirectoryPath + OPENMRS_DIR_PATH;
    }

    /**
     * Gets open mrs logger.
     *
     * @return the open mrs logger
     */
    public static OpenMRSLogger getOpenMRSLogger() {
        return logger;
    }

    /**
     * Delete secret key.
     */
    public static void deleteSecretKey() {
        secretKey = null;
    }

    /**
     * Gets open mrs shared preferences.
     *
     * @return the open mrs shared preferences
     */
    public static SharedPreferences getOpenMRSSharedPreferences() {
        return instance.getSharedPreferences(ApplicationConstants.OpenMRSSharedPreferenceNames.SHARED_PREFERENCES_NAME,
                MODE_PRIVATE);
    }

    /**
     * Sets password and hashed password.
     *
     * @param password the password
     */
    public static void setPasswordAndHashedPassword(String password) {
        SharedPreferences.Editor editor = getOpenMRSSharedPreferences().edit();
        String salt = BCrypt.gensalt(ApplicationConstants.DEFAULT_BCRYPT_ROUND);
        String hashedPassword = BCrypt.hashpw(password, salt);
        editor.putString(ApplicationConstants.UserKeys.PASSWORD, password);
        editor.putString(ApplicationConstants.UserKeys.HASHED_PASSWORD, hashedPassword);
        editor.apply();
    }

    /**
     * Gets first time.
     *
     * @return the first time
     */
    public static Boolean getFirstTime() {
        SharedPreferences prefs = getOpenMRSSharedPreferences();
        return prefs.getBoolean(ApplicationConstants.UserKeys.FIRST_TIME, ApplicationConstants.FIRST);
    }

    /**
     * Sets user first time.
     *
     * @param firstLogin the first login
     */
    public static void setUserFirstTime(boolean firstLogin) {
        SharedPreferences.Editor editor = OpenmrsAndroid.getOpenMRSSharedPreferences().edit();
        editor.putBoolean(ApplicationConstants.UserKeys.FIRST_TIME, firstLogin);
        editor.apply();
    }

    /**
     * Gets password.
     *
     * @return the password
     */
    public static String getPassword() {
        SharedPreferences prefs = getOpenMRSSharedPreferences();
        return prefs.getString(ApplicationConstants.UserKeys.PASSWORD, ApplicationConstants.EMPTY_STRING);
    }

    /**
     * Sets password.
     *
     * @param password the password
     */
    public static void setPassword(String password) {
        SharedPreferences.Editor editor = getOpenMRSSharedPreferences().edit();
        editor.putString(ApplicationConstants.UserKeys.PASSWORD, password);
        editor.apply();
    }

    /**
     * Gets hashed password.
     *
     * @return the hashed password
     */
    public static String getHashedPassword() {
        SharedPreferences prefs = getOpenMRSSharedPreferences();
        return prefs.getString(ApplicationConstants.UserKeys.HASHED_PASSWORD, ApplicationConstants.EMPTY_STRING);
    }

    /**
     * Sets hashed password.
     *
     * @param hashedPassword the hashed password
     */
    public static void setHashedPassword(String hashedPassword) {
        SharedPreferences.Editor editor = getOpenMRSSharedPreferences().edit();
        editor.putString(ApplicationConstants.UserKeys.HASHED_PASSWORD, hashedPassword);
        editor.apply();
    }

    /**
     * Gets username.
     *
     * @return the username
     */
    public static String getUsername() {
        SharedPreferences prefs = getOpenMRSSharedPreferences();
        return prefs.getString(ApplicationConstants.UserKeys.USER_NAME, ApplicationConstants.EMPTY_STRING);
    }

    /**
     * Sets username.
     *
     * @param username the username
     */
    public static void setUsername(String username) {
        SharedPreferences.Editor editor = getOpenMRSSharedPreferences().edit();
        editor.putString(ApplicationConstants.UserKeys.USER_NAME, username);
        editor.apply();
    }

    /**
     * Is user logged online boolean.
     *
     * @return the boolean
     */
    public static boolean isUserLoggedOnline() {
        SharedPreferences prefs = getOpenMRSSharedPreferences();
        return prefs.getBoolean(ApplicationConstants.UserKeys.LOGIN, false);
    }

    /**
     * Sets user logged online.
     *
     * @param firstLogin the first login
     */
    public static void setUserLoggedOnline(boolean firstLogin) {
        SharedPreferences.Editor editor = getOpenMRSSharedPreferences().edit();
        editor.putBoolean(ApplicationConstants.UserKeys.LOGIN, firstLogin);
        editor.apply();
    }

    /**
     * Gets server url.
     *
     * @return the server url
     */
    public static String getServerUrl() {
        SharedPreferences prefs = getOpenMRSSharedPreferences();
        return prefs.getString(ApplicationConstants.SERVER_URL, ApplicationConstants.DEFAULT_OPEN_MRS_URL);
    }

    /**
     * Sets server url.
     *
     * @param serverUrl the server url
     */
    public static void setServerUrl(String serverUrl) {
        SharedPreferences.Editor editor = getOpenMRSSharedPreferences().edit();
        editor.putString(ApplicationConstants.SERVER_URL, serverUrl);
        editor.apply();
    }

    /**
     * Gets last login server url.
     *
     * @return the last login server url
     */
    public static String getLastLoginServerUrl() {
        SharedPreferences prefs = getOpenMRSSharedPreferences();
        return prefs.getString(ApplicationConstants.LAST_LOGIN_SERVER_URL, ApplicationConstants.EMPTY_STRING);
    }

    /**
     * Sets last login server url.
     *
     * @param url the url
     */
    public static void setLastLoginServerUrl(String url) {
        SharedPreferences.Editor editor = getOpenMRSSharedPreferences().edit();
        editor.putString(ApplicationConstants.LAST_LOGIN_SERVER_URL, url);
        editor.apply();
    }

    /**
     * Gets session token.
     *
     * @return the session token
     */
    public static String getSessionToken() {
        SharedPreferences prefs = getOpenMRSSharedPreferences();
        return prefs.getString(ApplicationConstants.SESSION_TOKEN, ApplicationConstants.EMPTY_STRING);
    }

    /**
     * Sets session token.
     *
     * @param serverUrl the server url
     */
    public static void setSessionToken(String serverUrl) {
        SharedPreferences.Editor editor = getOpenMRSSharedPreferences().edit();
        editor.putString(ApplicationConstants.SESSION_TOKEN, serverUrl);
        editor.apply();
    }

    /**
     * Gets last session token.
     *
     * @return the last session token
     */
    public static String getLastSessionToken() {
        SharedPreferences prefs = getOpenMRSSharedPreferences();
        return prefs.getString(ApplicationConstants.LAST_SESSION_TOKEN, ApplicationConstants.EMPTY_STRING);
    }

    /**
     * Gets location.
     *
     * @return the location
     */
    public static String getLocation() {
        SharedPreferences prefs = getOpenMRSSharedPreferences();
        return prefs.getString(ApplicationConstants.LOCATION, ApplicationConstants.EMPTY_STRING);
    }

    /**
     * Sets location.
     *
     * @param location the location
     */
    public static void setLocation(String location) {
        SharedPreferences.Editor editor = getOpenMRSSharedPreferences().edit();
        editor.putString(ApplicationConstants.LOCATION, location);
        editor.apply();
    }

    /**
     * Gets visit type uuid.
     *
     * @return the visit type uuid
     */
    public static String getVisitTypeUUID() {
        SharedPreferences prefs = getOpenMRSSharedPreferences();
        return prefs.getString(ApplicationConstants.VISIT_TYPE_UUID, ApplicationConstants.EMPTY_STRING);
    }

    /**
     * Sets visit type uuid.
     *
     * @param visitTypeUUID the visit type uuid
     */
    public static void setVisitTypeUUID(String visitTypeUUID) {
        SharedPreferences.Editor editor = getOpenMRSSharedPreferences().edit();
        editor.putString(ApplicationConstants.VISIT_TYPE_UUID, visitTypeUUID);
        editor.apply();
    }

    private void createSecretKey() {
        secretKey = BCrypt.hashpw(OpenmrsAndroid.getUsername() + ApplicationConstants.DB_PASSWORD_LITERAL_PEPPER + OpenmrsAndroid.getPassword(), ApplicationConstants.DB_PASSWORD_BCRYPT_PEPPER);
    }

    /**
     * Gets secret key.
     *
     * @return the secret key
     */
    public String getSecretKey() {
        if (secretKey == null) {
            createSecretKey();
        }
        return secretKey;
    }

    /**
     * Gets sync state.
     *
     * @return the sync state
     */
    public static boolean getSyncState() {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(instance);
        return prefs.getBoolean("sync", true);
    }


    /**
     * Sets sync state.
     *
     * @param enabled the enabled
     */
    public static void setSyncState(boolean enabled) {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(instance);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("sync", enabled);
        editor.apply();
    }

    /**
     * Sets current user information.
     *
     * @param userInformation the user information
     */
    public static void setCurrentUserInformation(Map<String, String> userInformation) {
        SharedPreferences.Editor editor = getOpenMRSSharedPreferences().edit();
        for (Map.Entry<String, String> entry : userInformation.entrySet()) {
            editor.putString(entry.getKey(), entry.getValue());
        }
        editor.apply();
    }

    /**
     * Gets current logged in user info.
     *
     * @return the current logged in user info
     */
    public Map<String, String> getCurrentLoggedInUserInfo() {
        SharedPreferences prefs = getOpenMRSSharedPreferences();
        Map<String, String> infoMap = new HashMap<>();
        infoMap.put(ApplicationConstants.UserKeys.USER_PERSON_NAME, prefs.getString(ApplicationConstants.UserKeys.USER_PERSON_NAME, ApplicationConstants.EMPTY_STRING));
        infoMap.put(ApplicationConstants.UserKeys.USER_UUID, prefs.getString(ApplicationConstants.UserKeys.USER_UUID, ApplicationConstants.EMPTY_STRING));
        return infoMap;
    }

    /**
     * Clear current logged in user info.
     */
    public static void clearCurrentLoggedInUserInfo() {
        SharedPreferences prefs = OpenmrsAndroid.getOpenMRSSharedPreferences();
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(ApplicationConstants.UserKeys.USER_PERSON_NAME);
        editor.remove(ApplicationConstants.UserKeys.USER_UUID);
        editor.apply();
    }

    /**
     * Clear user preferences data.
     */
    public static void clearUserPreferencesData() {
        SharedPreferences prefs = OpenmrsAndroid.getOpenMRSSharedPreferences();
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(ApplicationConstants.LAST_SESSION_TOKEN,
                prefs.getString(ApplicationConstants.SESSION_TOKEN, ApplicationConstants.EMPTY_STRING));
        editor.remove(ApplicationConstants.SESSION_TOKEN);
        editor.remove(ApplicationConstants.AUTHORIZATION_TOKEN);
        OpenmrsAndroid.clearCurrentLoggedInUserInfo();
        editor.remove(ApplicationConstants.UserKeys.PASSWORD);
        OpenmrsAndroid.deleteSecretKey();
        editor.apply();
    }

    /**
     * Sets default form load id.
     *
     * @param xFormName the x form name
     * @param xFormID   the x form id
     */
    public void setDefaultFormLoadID(String xFormName, String xFormID) {
        SharedPreferences.Editor editor = getOpenMRSSharedPreferences().edit();
        editor.putString(xFormName, xFormID);
        editor.apply();
    }

    /**
     * Gets default form load id.
     *
     * @param xFormName the x form name
     * @return the default form load id
     */
    public String getDefaultFormLoadID(String xFormName) {
        SharedPreferences prefs = getOpenMRSSharedPreferences();
        return prefs.getString(xFormName, ApplicationConstants.EMPTY_STRING);
    }

    /**
     * Gets authorization token.
     *
     * @return the authorization token
     */
    public String getAuthorizationToken() {
        SharedPreferences prefs = getOpenMRSSharedPreferences();
        return prefs.getString(ApplicationConstants.AUTHORIZATION_TOKEN, ApplicationConstants.EMPTY_STRING);
    }

    /**
     * Sets authorization token.
     *
     * @param authorization the authorization
     */
    public void setAuthorizationToken(String authorization) {
        SharedPreferences.Editor editor = getOpenMRSSharedPreferences().edit();
        editor.putString(ApplicationConstants.AUTHORIZATION_TOKEN, authorization);
        editor.apply();
    }



}
