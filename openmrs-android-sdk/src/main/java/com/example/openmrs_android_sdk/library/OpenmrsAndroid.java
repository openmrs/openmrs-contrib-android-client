package com.example.openmrs_android_sdk.library;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.annotation.Nullable;

import com.example.openmrs_android_sdk.utilities.ApplicationConstants;

import org.mindrot.jbcrypt.BCrypt;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class OpenmrsAndroid {
    private volatile static Context instance;
    private static final String OPENMRS_DIR_NAME = "OpenMRS";
    private static final String OPENMRS_DIR_PATH = File.separator + OPENMRS_DIR_NAME;
    private static String externalDirectoryPath;
    private static String secretKey;

    private static OpenMRSLogger logger = new OpenMRSLogger();

    private OpenmrsAndroid() {
    }

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

    public static @Nullable
    Context getInstance() {
        return instance;
    }

    public static String getOpenMRSDir() {
        return externalDirectoryPath + OPENMRS_DIR_PATH;
    }

    public static OpenMRSLogger getOpenMRSLogger() {
        return logger;
    }

    public static void deleteSecretKey() {
        secretKey = null;
    }

    public static SharedPreferences getOpenMRSSharedPreferences() {
        return instance.getSharedPreferences(ApplicationConstants.OpenMRSSharedPreferenceNames.SHARED_PREFERENCES_NAME,
                MODE_PRIVATE);
    }

    public static void setPasswordAndHashedPassword(String password) {
        SharedPreferences.Editor editor = getOpenMRSSharedPreferences().edit();
        String salt = BCrypt.gensalt(ApplicationConstants.DEFAULT_BCRYPT_ROUND);
        String hashedPassword = BCrypt.hashpw(password, salt);
        editor.putString(ApplicationConstants.UserKeys.PASSWORD, password);
        editor.putString(ApplicationConstants.UserKeys.HASHED_PASSWORD, hashedPassword);
        editor.apply();
    }

    public static Boolean getFirstTime() {
        SharedPreferences prefs = getOpenMRSSharedPreferences();
        return prefs.getBoolean(ApplicationConstants.UserKeys.FIRST_TIME, ApplicationConstants.FIRST);
    }

    public static void setUserFirstTime(boolean firstLogin) {
        SharedPreferences.Editor editor = OpenmrsAndroid.getOpenMRSSharedPreferences().edit();
        editor.putBoolean(ApplicationConstants.UserKeys.FIRST_TIME, firstLogin);
        editor.apply();
    }

    public static String getPassword() {
        SharedPreferences prefs = getOpenMRSSharedPreferences();
        return prefs.getString(ApplicationConstants.UserKeys.PASSWORD, ApplicationConstants.EMPTY_STRING);
    }

    public static void setPassword(String password) {
        SharedPreferences.Editor editor = getOpenMRSSharedPreferences().edit();
        editor.putString(ApplicationConstants.UserKeys.PASSWORD, password);
        editor.apply();
    }

    public static String getHashedPassword() {
        SharedPreferences prefs = getOpenMRSSharedPreferences();
        return prefs.getString(ApplicationConstants.UserKeys.HASHED_PASSWORD, ApplicationConstants.EMPTY_STRING);
    }

    public static void setHashedPassword(String hashedPassword) {
        SharedPreferences.Editor editor = getOpenMRSSharedPreferences().edit();
        editor.putString(ApplicationConstants.UserKeys.HASHED_PASSWORD, hashedPassword);
        editor.apply();
    }

    public static String getUsername() {
        SharedPreferences prefs = getOpenMRSSharedPreferences();
        return prefs.getString(ApplicationConstants.UserKeys.USER_NAME, ApplicationConstants.EMPTY_STRING);
    }

    public static void setUsername(String username) {
        SharedPreferences.Editor editor = getOpenMRSSharedPreferences().edit();
        editor.putString(ApplicationConstants.UserKeys.USER_NAME, username);
        editor.apply();
    }

    public static boolean isUserLoggedOnline() {
        SharedPreferences prefs = getOpenMRSSharedPreferences();
        return prefs.getBoolean(ApplicationConstants.UserKeys.LOGIN, false);
    }

    public static void setUserLoggedOnline(boolean firstLogin) {
        SharedPreferences.Editor editor = getOpenMRSSharedPreferences().edit();
        editor.putBoolean(ApplicationConstants.UserKeys.LOGIN, firstLogin);
        editor.apply();
    }

    public static String getServerUrl() {
        SharedPreferences prefs = getOpenMRSSharedPreferences();
        return prefs.getString(ApplicationConstants.SERVER_URL, ApplicationConstants.DEFAULT_OPEN_MRS_URL);
    }

    public static void setServerUrl(String serverUrl) {
        SharedPreferences.Editor editor = getOpenMRSSharedPreferences().edit();
        editor.putString(ApplicationConstants.SERVER_URL, serverUrl);
        editor.apply();
    }
    public static String getLastLoginServerUrl() {
        SharedPreferences prefs = getOpenMRSSharedPreferences();
        return prefs.getString(ApplicationConstants.LAST_LOGIN_SERVER_URL, ApplicationConstants.EMPTY_STRING);
    }

    public static void setLastLoginServerUrl(String url) {
        SharedPreferences.Editor editor = getOpenMRSSharedPreferences().edit();
        editor.putString(ApplicationConstants.LAST_LOGIN_SERVER_URL, url);
        editor.apply();
    }

    public static String getSessionToken() {
        SharedPreferences prefs = getOpenMRSSharedPreferences();
        return prefs.getString(ApplicationConstants.SESSION_TOKEN, ApplicationConstants.EMPTY_STRING);
    }

    public static void setSessionToken(String serverUrl) {
        SharedPreferences.Editor editor = getOpenMRSSharedPreferences().edit();
        editor.putString(ApplicationConstants.SESSION_TOKEN, serverUrl);
        editor.apply();
    }

    public static String getLastSessionToken() {
        SharedPreferences prefs = getOpenMRSSharedPreferences();
        return prefs.getString(ApplicationConstants.LAST_SESSION_TOKEN, ApplicationConstants.EMPTY_STRING);
    }
    public static String getLocation() {
        SharedPreferences prefs = getOpenMRSSharedPreferences();
        return prefs.getString(ApplicationConstants.LOCATION, ApplicationConstants.EMPTY_STRING);
    }

    public static void setLocation(String location) {
        SharedPreferences.Editor editor = getOpenMRSSharedPreferences().edit();
        editor.putString(ApplicationConstants.LOCATION, location);
        editor.apply();
    }

    public static String getVisitTypeUUID() {
        SharedPreferences prefs = getOpenMRSSharedPreferences();
        return prefs.getString(ApplicationConstants.VISIT_TYPE_UUID, ApplicationConstants.EMPTY_STRING);
    }

    public static void setVisitTypeUUID(String visitTypeUUID) {
        SharedPreferences.Editor editor = getOpenMRSSharedPreferences().edit();
        editor.putString(ApplicationConstants.VISIT_TYPE_UUID, visitTypeUUID);
        editor.apply();
    }

    private void createSecretKey() {
        secretKey = BCrypt.hashpw(OpenmrsAndroid.getUsername() + ApplicationConstants.DB_PASSWORD_LITERAL_PEPPER + OpenmrsAndroid.getPassword(), ApplicationConstants.DB_PASSWORD_BCRYPT_PEPPER);
    }

    public String getSecretKey() {
        if (secretKey == null) {
            createSecretKey();
        }
        return secretKey;
    }

    public static boolean getSyncState() {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(instance);
        return prefs.getBoolean("sync", true);
    }


    public static void setSyncState(boolean enabled) {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(instance);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("sync", enabled);
        editor.apply();
    }

    public static void setCurrentUserInformation(Map<String, String> userInformation) {
        SharedPreferences.Editor editor = getOpenMRSSharedPreferences().edit();
        for (Map.Entry<String, String> entry : userInformation.entrySet()) {
            editor.putString(entry.getKey(), entry.getValue());
        }
        editor.apply();
    }

    public Map<String, String> getCurrentLoggedInUserInfo() {
        SharedPreferences prefs = getOpenMRSSharedPreferences();
        Map<String, String> infoMap = new HashMap<>();
        infoMap.put(ApplicationConstants.UserKeys.USER_PERSON_NAME, prefs.getString(ApplicationConstants.UserKeys.USER_PERSON_NAME, ApplicationConstants.EMPTY_STRING));
        infoMap.put(ApplicationConstants.UserKeys.USER_UUID, prefs.getString(ApplicationConstants.UserKeys.USER_UUID, ApplicationConstants.EMPTY_STRING));
        return infoMap;
    }

    public static void clearCurrentLoggedInUserInfo() {
        SharedPreferences prefs = OpenmrsAndroid.getOpenMRSSharedPreferences();
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(ApplicationConstants.UserKeys.USER_PERSON_NAME);
        editor.remove(ApplicationConstants.UserKeys.USER_UUID);
        editor.apply();
    }

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

    public void setDefaultFormLoadID(String xFormName, String xFormID) {
        SharedPreferences.Editor editor = getOpenMRSSharedPreferences().edit();
        editor.putString(xFormName, xFormID);
        editor.apply();
    }

    public String getDefaultFormLoadID(String xFormName) {
        SharedPreferences prefs = getOpenMRSSharedPreferences();
        return prefs.getString(xFormName, ApplicationConstants.EMPTY_STRING);
    }

    public String getAuthorizationToken() {
        SharedPreferences prefs = getOpenMRSSharedPreferences();
        return prefs.getString(ApplicationConstants.AUTHORIZATION_TOKEN, ApplicationConstants.EMPTY_STRING);
    }

    public void setAuthorizationToken(String authorization) {
        SharedPreferences.Editor editor = getOpenMRSSharedPreferences().edit();
        editor.putString(ApplicationConstants.AUTHORIZATION_TOKEN, authorization);
        editor.apply();
    }



}
