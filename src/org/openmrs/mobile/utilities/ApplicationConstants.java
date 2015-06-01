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

package org.openmrs.mobile.utilities;

public abstract class ApplicationConstants {
    public static final String EMPTY_STRING = "";

    public abstract static class OpenMRSSharedPreferenceNames {
        public static final String SHARED_PREFERENCES_NAME = "shared_preferences";
    }

    public static final String SERVER_URL = "server_url";
    public static final String SESSION_TOKEN = "session_id";
    public static final String AUTHORIZATION_TOKEN = "authorisation";
    public static final String SECRET_KEY = "secretKey";
    public static final String LOCATION = "location";
    public static final String VISIT_TYPE_UUID = "visit_type_uuid";
    public static final String VISIT_TYPE_DISPLAY = "visit_type_display";
    public static final String ONLINE_MODE = "online_mode";
    public static final String REQUEST_QUEUE = "request_queue";

    public static final String AUTHORIZATION_PARAM = "Authorization";
    public static final String JSESSIONID_PARAM = "JSESSIONID";
    public static final String COOKIE_PARAM = "Cookie";
    public static final String PATIENT_UUID_PARAM = "ACCEPT_PATIENT_UUID";

    public abstract static class API {
        public static final String REST_ENDPOINT = "/ws/rest/v1/";
        public static final String XFORM_ENDPOINT = "/moduleServlet/xforms/";
        public static final String XFORM_UPLOAD = "xformDataUpload";

        public static final String AUTHORISATION_END_POINT = "session";
        public static final String PATIENT_DETAILS = "patient";
        public static final String VISIT_DETAILS = "visit";
        public static final String OBS_DETAILS = "obs";
        public static final String VISIT_TYPE = "visittype";
        public static final String ENCOUNTER_DETAILS = "encounter";
        public static final String USER_DETAILS = "user";

        public static final String PATIENT_QUERY = "patient?q=";
        public static final String USER_QUERY = "user?q=";

        public static final String FORM_LIST = "xformDownload?target=xformslist&format=withurl";

        public static final String FULL_VERSION = "?v=full";
        public static final String FULL_VERSION_NEXT_PARAM = "&v=full";
        public static final int REQUEST_TIMEOUT = 20000;
    }

    public abstract static class UserKeys {
        public static final String USER_NAME = "username";
        public static final String USER_PERSON_NAME = "userDisplay";
        public static final String USER_UUID = "userUUID";
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
        public static final String SERVER_NOT_SUPPORTED_DIALOG_TAG = "serverNotSupportedDialog";
        public static final String START_VISIT_DIALOG_TAG = "startVisitDialog";
        public static final String START_VISIT_IMPOSSIBLE_DIALOG_TAG = "startVisitImpossibleDialog";
        public static final String WARNING_LOST_DATA_DIALOG_TAG = "warningLostDataDialog";
        public static final String NO_VISIT_DIALOG_TAG = "noVisitDialogTag";
    }

    public abstract static class BundleKeys {
        public static final String CUSTOM_DIALOG_BUNDLE = "customDialogBundle";
        public static final String PATIENT_ID_BUNDLE = "patientID";
        public static final String PATIENT_UUID_BUNDLE = "patientUUID";
        public static final String PATIENT_BUNDLE = "patientBundle";
        public static final String VISIT_ID = "visitID";
        public static final String PATIENT_NAME = "patientName";
        public static final String PATIENT_LIST = "patientList";
        public static final String PROGRESS_BAR = "progressBar";
        public static final String FORM_NAME = "formName";
    }

    public abstract static class CustomIntentActions {
        public static final String ACTION_AUTH_FAILED_BROADCAST = "org.openmrs.mobile.intent.action.AUTH_FAILED_BROADCAST";
        public static final String ACTION_UNAUTHORIZED_BROADCAST = "org.openmrs.mobile.intent.action.UNAUTHORIZED_BROADCAST";
        public static final String ACTION_CONN_TIMEOUT_BROADCAST = "org.openmrs.mobile.intent.action.CONN_TIMEOUT_BROADCAST";
        public static final String ACTION_NO_INTERNET_CONNECTION_BROADCAST = "org.openmrs.mobile.intent.action.NO_INTERNET_CONNECTION_BROADCAST";
        public static final String ACTION_SERVER_UNAVAILABLE_BROADCAST = "org.openmrs.mobile.intent.action.SERVER_UNAVAILABLE_BROADCAST";
        public static final String ACTION_SERVER_ERROR_BROADCAST = "org.openmrs.mobile.intent.action.SERVER_ERROR_BROADCAST";
        public static final String ACTION_SOCKET_EXCEPTION_BROADCAST = "org.openmrs.mobile.intent.action.SOCKET_EXCEPTION_BROADCAST";
        public static final String ACTION_SERVER_NOT_SUPPORTED_BROADCAST = "org.openmrs.mobile.intent.action.SERVER_NOT_SUPPORTED_BROADCAST";
    }

    public abstract static class VolleyErrors {
        public static final String CONNECTION_TIMEOUT = "com.android.volley.TimeoutError";
        public static final String NO_CONNECTION = "com.android.volley.NoConnectionError";
        public static final String CONNECT_EXCEPTION = "java.net.ConnectException";
        public static final String UNKNOWN_HOST = "java.net.UnknownHostException";
        public static final String AUTHORISATION_FAILURE = "com.android.volley.AuthFailureError";
        public static final String SERVER_ERROR = "com.android.volley.ServerError";
        public static final String SOCKET_EXCEPTION = "java.net.SocketException";
        public static final String EOF_EXCEPTION = "java.io.EOFException";
    }

    public static final String DEFAULT_OPEN_MRS_URL = EMPTY_STRING;

    public abstract static class EncounterTypes {
        public static final String VITALS = "67a71486-1a54-468f-ac3e-7091a9a79584";
    }

    public abstract static class FormNames {
        public static final String VITALS_XFORM = "Vitals XForm";
    }
    public static final boolean DEFAULT_ONLINE_MODE = true;
    public static final int DISABLED_ICON_ALPHA = 70;
    public static final int ENABLED_ICON_ALPHA = 255;

    public abstract static class OfflineRequests {
        public static final String INACTIVATE_VISIT = "inactivateVisit";
        public static final String START_VISIT = "startVisit";
    }
}
