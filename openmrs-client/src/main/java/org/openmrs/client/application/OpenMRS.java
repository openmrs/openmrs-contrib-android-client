package org.openmrs.client.application;

import android.app.Application;
import android.content.SharedPreferences;
import org.openmrs.client.utilities.ApplicationConstants;

public class OpenMRS extends Application {

    private static OpenMRS instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static OpenMRS getInstance() {
        return instance;
    }

    public void setUsername(String username) {
        SharedPreferences.Editor editor = getSharedPreferences(ApplicationConstants.OpenMRSSharedPreferenceNames.SHARED_PREFERENCES_NAME,
                MODE_PRIVATE).edit();
        editor.putString(ApplicationConstants.USER_NAME, username);
        editor.commit();
    }

    public void setServerUrl(String serverUrl) {
        SharedPreferences.Editor editor = getSharedPreferences(ApplicationConstants.OpenMRSSharedPreferenceNames.SHARED_PREFERENCES_NAME,
                MODE_PRIVATE).edit();
        editor.putString(ApplicationConstants.SERVER_URL, serverUrl);
        editor.commit();
    }

    public void setSessionToken(String serverUrl) {
        SharedPreferences.Editor editor = getSharedPreferences(ApplicationConstants.OpenMRSSharedPreferenceNames.SHARED_PREFERENCES_NAME,
                MODE_PRIVATE).edit();
        editor.putString(ApplicationConstants.SESSION_TOKEN, serverUrl);
        editor.commit();
    }

    public String getUsername() {
        SharedPreferences sp = getSharedPreferences(ApplicationConstants.OpenMRSSharedPreferenceNames.SHARED_PREFERENCES_NAME,
                MODE_PRIVATE);
        return sp.getString(ApplicationConstants.USER_NAME, ApplicationConstants.EMPTY_STRING);
    }

    public String getServerUrl() {
        SharedPreferences sp = getSharedPreferences(ApplicationConstants.OpenMRSSharedPreferenceNames.SHARED_PREFERENCES_NAME,
                MODE_PRIVATE);
        return sp.getString(ApplicationConstants.SERVER_URL, ApplicationConstants.EMPTY_STRING);
    }

    public String getSessionToken() {
        SharedPreferences sp = getSharedPreferences(ApplicationConstants.OpenMRSSharedPreferenceNames.SHARED_PREFERENCES_NAME,
                MODE_PRIVATE);
        return sp.getString(ApplicationConstants.SESSION_TOKEN, ApplicationConstants.EMPTY_STRING);
    }
}
