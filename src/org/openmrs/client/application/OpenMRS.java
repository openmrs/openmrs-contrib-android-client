package org.openmrs.client.application;

import android.app.Application;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Environment;

import net.sqlcipher.database.SQLiteDatabase;

import org.openmrs.client.databases.OpenMRSDBOpenHelper;
import org.openmrs.client.security.SecretKeyGenerator;
import org.openmrs.client.utilities.ApplicationConstants;

public class OpenMRS extends Application {

    private static OpenMRS instance;
    private OpenMRSLogger mLogger;

    @Override
    public void onCreate() {
        initializeSQLCipher();
        super.onCreate();
        instance = this;
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
        return Environment.getExternalStorageDirectory() + "/OpenMRS/";
    }

    public boolean isRunningHoneycombVersionOrHigher() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
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
}
