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

package org.openmrs.mobile.application;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Configuration;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import net.sqlcipher.database.SQLiteDatabase;
import org.odk.collect.android.application.Collect;
import org.openmrs.mobile.api.FormListService;
import org.openmrs.mobile.databases.OpenMRSDBOpenHelper;
import org.openmrs.mobile.models.Location;
import org.openmrs.mobile.models.retrofit.EncounterType;
import org.openmrs.mobile.models.retrofit.Encountercreate;
import org.openmrs.mobile.models.retrofit.Form;
import org.openmrs.mobile.models.retrofit.FormResource;
import org.openmrs.mobile.models.retrofit.Link;
import org.openmrs.mobile.models.retrofit.Obscreate;
import org.openmrs.mobile.models.retrofit.Page;
import org.openmrs.mobile.models.retrofit.Question;
import org.openmrs.mobile.models.retrofit.QuestionOptions;
import org.openmrs.mobile.models.retrofit.Resource;
import org.openmrs.mobile.models.retrofit.Section;
import org.openmrs.mobile.security.SecretKeyGenerator;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.StringUtils;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OpenMRS extends Collect {
    private static OpenMRS instance;
    private OpenMRSLogger mLogger;
    private static RequestQueue mRequestQueue;

    @Override
    public void onCreate() {
        initializeSQLCipher();
        super.onCreate();
        instance = this;
        if (mExternalDirectoryPath == null) {
            mExternalDirectoryPath = this.getExternalFilesDir(null).toString();
            overrideODKDirs();
        }
        OpenMRS.createODKDirs();
        mLogger = new OpenMRSLogger();
        generateKey();
        OpenMRSDBOpenHelper.init();
        initializeDB();

        Intent i=new Intent(this,FormListService.class);
        startService(i);
    }

    protected void initializeDB() {
        Configuration.Builder configurationBuilder = new Configuration.Builder(this);
        configurationBuilder.addModelClasses(Link.class);
        configurationBuilder.addModelClasses(FormResource.class);
        configurationBuilder.addModelClasses(EncounterType.class);
        configurationBuilder.addModelClasses(Encountercreate.class);
        configurationBuilder.addModelClasses(Obscreate.class);



        ActiveAndroid.initialize(configurationBuilder.create());
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> request) {
        getRequestQueue().add(request);
    }

    public static OpenMRS getInstance() {
        return instance;
    }

    public SharedPreferences getOpenMRSSharedPreferences() {
        return getSharedPreferences(ApplicationConstants.OpenMRSSharedPreferenceNames.SHARED_PREFERENCES_NAME,
                MODE_PRIVATE);
    }

    public void setFirstLogin(boolean firstLogin) {
        SharedPreferences.Editor editor = getOpenMRSSharedPreferences().edit();
        editor.putBoolean(ApplicationConstants.UserKeys.FIRST_LOGIN, firstLogin);
        editor.commit();
    }

    public void setUsername(String username) {
        SharedPreferences.Editor editor = getOpenMRSSharedPreferences().edit();
        editor.putString(ApplicationConstants.UserKeys.USER_NAME, username);
        editor.commit();
    }

    public void setPassword(String password) {
        SharedPreferences.Editor editor = getOpenMRSSharedPreferences().edit();
        editor.putString(ApplicationConstants.UserKeys.PASSWORD, password);
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


    public void setLocations(List<Location> locations) {
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        String jsonList = gson.toJson(locations);
        SharedPreferences.Editor editor = getOpenMRSSharedPreferences().edit();
        editor.putString(ApplicationConstants.CACHED_LOCATIONS, jsonList);
        editor.commit();
    }

    public void setVisitTypeUUID(String visitTypeUUID) {
        SharedPreferences.Editor editor = getOpenMRSSharedPreferences().edit();
        editor.putString(ApplicationConstants.VISIT_TYPE_UUID, visitTypeUUID);
        editor.commit();
    }

    public boolean getFirstLogin() {
        SharedPreferences prefs = getOpenMRSSharedPreferences();
        return prefs.getBoolean(ApplicationConstants.UserKeys.FIRST_LOGIN, false);
    }

    public String getUsername() {
        SharedPreferences prefs = getOpenMRSSharedPreferences();
        return prefs.getString(ApplicationConstants.UserKeys.USER_NAME, ApplicationConstants.EMPTY_STRING);
    }

    public String getPassword() {
        SharedPreferences prefs = getOpenMRSSharedPreferences();
        return prefs.getString(ApplicationConstants.UserKeys.PASSWORD, ApplicationConstants.EMPTY_STRING);
    }

    public String getServerUrl() {
        SharedPreferences prefs = getOpenMRSSharedPreferences();
        return prefs.getString(ApplicationConstants.SERVER_URL, ApplicationConstants.DEFAULT_OPEN_MRS_URL);
    }

    public String getSessionToken() {
        SharedPreferences prefs = getOpenMRSSharedPreferences();
        return prefs.getString(ApplicationConstants.SESSION_TOKEN, ApplicationConstants.EMPTY_STRING);
    }

    public String getLastSessionToken() {
        SharedPreferences prefs = getOpenMRSSharedPreferences();
        return prefs.getString(ApplicationConstants.LAST_SESSION_TOKEN, ApplicationConstants.EMPTY_STRING);
    }

    public String getAuthorizationToken() {
        SharedPreferences prefs = getOpenMRSSharedPreferences();
        return prefs.getString(ApplicationConstants.AUTHORIZATION_TOKEN, ApplicationConstants.EMPTY_STRING);
    }

    public String getLocation() {
        SharedPreferences prefs = getOpenMRSSharedPreferences();
        return prefs.getString(ApplicationConstants.LOCATION, ApplicationConstants.EMPTY_STRING);
    }

    public List<Location> getLocations() {
        SharedPreferences prefs = getOpenMRSSharedPreferences();
        String jsonList = prefs.getString(ApplicationConstants.CACHED_LOCATIONS, ApplicationConstants.EMPTY_STRING);
        if (jsonList.equals(ApplicationConstants.EMPTY_STRING))
            return null;
        Gson gson = new Gson();
        Type dataType = new TypeToken<List<Location>>(){}.getType();
        return gson.fromJson(jsonList, dataType);
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

    public void setDefaultFormLoadID(String xFormName, String xFormID) {
        SharedPreferences.Editor editor = getOpenMRSSharedPreferences().edit();
        editor.putString(xFormName, xFormID);
        editor.commit();
    }

    public String getDefaultFormLoadID(String xFormName) {
        SharedPreferences prefs = getOpenMRSSharedPreferences();
        return prefs.getString(xFormName, ApplicationConstants.EMPTY_STRING);
    }

    public void setCurrentUserInformation(Map<String, String> userInformation) {
        SharedPreferences.Editor editor = getOpenMRSSharedPreferences().edit();
        for (Map.Entry<String, String> entry : userInformation.entrySet()) {
            editor.putString(entry.getKey(), entry.getValue());
        }
        editor.commit();
    }

    public Map<String, String> getCurrentLoggedInUserInfo() {
        SharedPreferences prefs = getOpenMRSSharedPreferences();
        Map<String, String> infoMap = new HashMap<String, String>();
        infoMap.put(ApplicationConstants.UserKeys.USER_PERSON_NAME, prefs.getString(ApplicationConstants.UserKeys.USER_PERSON_NAME, ApplicationConstants.EMPTY_STRING));
        infoMap.put(ApplicationConstants.UserKeys.USER_UUID, prefs.getString(ApplicationConstants.UserKeys.USER_UUID, ApplicationConstants.EMPTY_STRING));
        return infoMap;
    }

    private void clearCurrentLoggedInUserInfo() {
        SharedPreferences prefs = OpenMRS.getInstance().getOpenMRSSharedPreferences();
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(ApplicationConstants.UserKeys.USER_PERSON_NAME);
        editor.remove(ApplicationConstants.UserKeys.USER_UUID);
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

    public void clearUserPreferencesData() {
        SharedPreferences prefs = OpenMRS.getInstance().getOpenMRSSharedPreferences();
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(ApplicationConstants.LAST_SESSION_TOKEN,
                prefs.getString(ApplicationConstants.SESSION_TOKEN, ApplicationConstants.EMPTY_STRING));
        editor.remove(ApplicationConstants.SESSION_TOKEN);
        editor.remove(ApplicationConstants.AUTHORIZATION_TOKEN);
        clearCurrentLoggedInUserInfo();
        editor.commit();
    }


}
