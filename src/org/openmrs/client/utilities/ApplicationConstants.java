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

package org.openmrs.client.utilities;

public abstract class ApplicationConstants {
    public static final String EMPTY_STRING = "";

    public abstract static class OpenMRSSharedPreferenceNames {
        public static final String SHARED_PREFERENCES_NAME = "shared_preferences";
    }

    public static final String USER_NAME = "username";
    public static final String SERVER_URL = "server_url";
    public static final String SESSION_TOKEN = "session_id";
    public static final String AUTHORIZATION_TOKEN = "authorisation";
    public static final String SECRET_KEY = "secretKey";
    public static final String LOCATION = "location";

    public static final String AUTHORIZATION_PARAM = "Authorization";
    public static final String JSESSIONID_PARAM = "JSESSIONID";
    public static final String COOKIE_PARAM = "Cookie";

    public abstract static class API {
        public static final String COMMON_PART = "/ws/rest/v1/";
        public static final String AUTHORISATION_END_POINT = "session";
        public static final String PATIENT_DETAILS = "patient/";
        public static final String VISIT_DETAILS = "visit/";

        public static final String FULL_VERSION = "?v=full";
    }

    public abstract static class DialogTAG {
        public static final String URL_DIALOG_TAG = "urlDialog";
        public static final String AUTH_FAILED_DIALOG_TAG = "authFailedDialog";
        public static final String CONN_TIMEOUT_DIALOG_TAG = "connectionTimeoutDialog";
        public static final String NO_INTERNET_CONN_DIALOG_TAG = "noInternetConnectionDialog";
        public static final String SERVER_UNAVAILABLE_DIALOG_TAG = "serverUnavailableDialog";
        public static final String INVALID_URL_DIALOG_TAG = "invalidURLDialog";
        public static final String LOGOUT_DIALOG_TAG = "logoutDialog";
        public static final String END_VISIT_DIALOG_TAG = "endVisitDialogTag";
        public static final String UNAUTHORIZED_DIALOG_TAG = "unauthorizedDialog";
        public static final String SERVER_ERROR_DIALOG_TAG = "serverErrorDialog";
        public static final String SOCKET_EXCEPTION_DIALOG_TAG = "socketExceptionDialog";
    }

    public abstract static class BundleKeys {
        public static final String CUSTOM_DIALOG_BUNDLE = "customDialogBundle";
        public static final String PATIENT_ID_BUNDLE = "patientID";
        public static final String PATIENT_BUNDLE = "patientBundle";
        public static final String VISIT_ID = "visitID";
        public static final String PATIENT_NAME = "patientName";
    }

    public abstract static class CustomIntentActions {
        public static final String ACTION_AUTH_FAILED_BROADCAST = "org.openmrs.client.intent.action.AUTH_FAILED_BROADCAST";
        public static final String ACTION_UNAUTHORIZED_BROADCAST = "org.openmrs.client.intent.action.UNAUTHORIZED_BROADCAST";
        public static final String ACTION_CONN_TIMEOUT_BROADCAST = "org.openmrs.client.intent.action.CONN_TIMEOUT_BROADCAST";
        public static final String ACTION_NO_INTERNET_CONNECTION_BROADCAST = "org.openmrs.client.intent.action.NO_INTERNET_CONNECTION_BROADCAST";
        public static final String ACTION_SERVER_UNAVAILABLE_BROADCAST = "org.openmrs.client.intent.action.SERVER_UNAVAILABLE_BROADCAST";
        public static final String ACTION_SERVER_ERROR_BROADCAST = "org.openmrs.client.intent.action.SERVER_ERROR_BROADCAST";
        public static final String ACTION_SOCKET_EXCEPTION_BROADCAST = "org.openmrs.client.intent.action.SOCKET_EXCEPTION_BROADCAST";
    }

    public abstract static class VolleyErrors {
        public static final String CONNECTION_TIMEOUT = "com.android.volley.TimeoutError";
        public static final String NO_CONNECTION = "com.android.volley.NoConnectionError";
        public static final String CONNECT_EXCEPTION = "java.net.ConnectException";
        public static final String UNKNOWN_HOST = "java.net.UnknownHostException";
        public static final String AUTHORISATION_FAILURE = "com.android.volley.AuthFailureError";
        public static final String SERVER_ERROR = "com.android.volley.ServerError";
        public static final String SOCKET_EXCEPTION = "java.net.SocketException";
    }

    public static final String DEFAULT_OPEN_MRS_URL = EMPTY_STRING;
}
