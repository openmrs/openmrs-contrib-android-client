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

import org.openmrs.mobile.models.EncounterType;

public abstract class ApplicationConstants {

    public static final String EMPTY_STRING = "";
    public static final String SERVER_URL = "server_url";
    public static final String SESSION_TOKEN = "session_id";
    public static final String AUTHORIZATION_TOKEN = "authorisation";
    public static final String SECRET_KEY = "secretKey";
    public static final String LOCATION = "location";
    public static final String VISIT_TYPE_UUID = "visit_type_uuid";
    public static final String LAST_SESSION_TOKEN = "last_session_id";
    public static final String LAST_LOGIN_SERVER_URL = "last_login_server_url";
    public static final String DEFAULT_OPEN_MRS_URL = "http://devtest04.openmrs.org:8080/openmrs";

    public abstract static class OpenMRSSharedPreferenceNames {
        public static final String SHARED_PREFERENCES_NAME = "shared_preferences";
    }

    public abstract static class API {
        public static final String REST_ENDPOINT = "/ws/rest/v1/";
        public static final String FULL = "full";
    }

    public abstract static class UserKeys {
        public static final String USER_NAME = "username";
        public static final String PASSWORD = "password";
        public static final String USER_PERSON_NAME = "userDisplay";
        public static final String USER_UUID = "userUUID";
        public static final String LOGIN = "login";
    }

    public abstract static class DialogTAG {
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
        public static final String SIMILAR_PATIENTS_TAG = "similarPatientsDialogTag";
        public static final String DELET_PATIENT_DIALOG_TAG = "deletePatientDialogTag";
    }

    public abstract static class RegisterPatientRequirements {
        public static final int MAX_PATIENT_AGE = 120;
    }

    public abstract static class BundleKeys {
        public static final String CUSTOM_DIALOG_BUNDLE = "customDialogBundle";
        public static final String PATIENT_ID_BUNDLE = "patientID";
        public static final String VISIT_ID = "visitID";
        public static final String ENCOUNTERTYPE = "encounterType";
        public static final String VALUEREFERENCE = "valueReference";
        public static final String FORM_NAME = "formName";
        public static final String CALCULATED_LOCALLY = "CALCULATED_LOCALLY";
        public static final String PATIENTS_AND_MATCHES = "PATIENTS_AND_MATCHES";
        public static final String FORM_FIELDS_BUNDLE = "formFieldsBundle";
        public static final String FORM_FIELDS_LIST_BUNDLE = "formFieldsListBundle";
        public static final String PATIENT_QUERY_BUNDLE = "patientQuery";
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

    public abstract static class EncounterTypes {
        public static final String VITALS = "67a71486-1a54-468f-ac3e-7091a9a79584";
        public static String[] ENCOUNTER_TYPES_DISPLAYS = {EncounterType.VITALS, EncounterType.ADMISSION, EncounterType.DISCHARGE, EncounterType.VISIT_NOTE};
    }

}
