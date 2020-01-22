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
    public static final Boolean FIRST = true;
    public static final String VISIT_TYPE_UUID = "visit_type_uuid";
    public static final String LAST_SESSION_TOKEN = "last_session_id";
    public static final String LAST_LOGIN_SERVER_URL = "last_login_server_url";
    public static final String DEFAULT_OPEN_MRS_URL = "https://demo.openmrs.org/openmrs";
    public static final String DB_NAME ="openmrs.db";
    public static final String DB_PASSWORD_BCRYPT_PEPPER = "$2a$08$iUp3M1VapYpjcAXQBNX6uu";
    public static final String DB_PASSWORD_LITERAL_PEPPER = "Open Sesame";
    public static final String DEFAULT_VISIT_TYPE_UUID = "7b0f5697-27e3-40c4-8bae-f4049abfb4ed";
    public static final int DEFAULT_BCRYPT_ROUND = 8;
    public static final int SPLASH_TIMER = 3500;
    public static final String PACKAGE_NAME = "org.openmrs.mobile";
    public static final String USER_GUIDE = "https://openmrs.github.io/openmrs-android-client-user-guide/getting-started.html";

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
        public static final String HASHED_PASSWORD = "hashedPassword";
        public static final String USER_PERSON_NAME = "userDisplay";
        public static final String USER_UUID = "userUUID";
        public static final String LOGIN = "login";
        public static final String FIRST_TIME = "firstTime";
    }

    public abstract static class DialogTAG {
        public static final String LOGOUT_DIALOG_TAG = "logoutDialog";
        public static final String END_VISIT_DIALOG_TAG = "endVisitDialogTag";
        public static final String START_VISIT_DIALOG_TAG = "startVisitDialog";
        public static final String START_VISIT_IMPOSSIBLE_DIALOG_TAG = "startVisitImpossibleDialog";
        public static final String WARNING_LOST_DATA_DIALOG_TAG = "warningLostDataDialog";
        public static final String SIMILAR_PATIENTS_TAG = "similarPatientsDialogTag";
        public static final String DELET_PATIENT_DIALOG_TAG = "deletePatientDialogTag";
        public static final String LOCATION_DIALOG_TAG = "locationDialogTag";
        public static final String CREDENTIAL_CHANGED_DIALOG_TAG = "locationDialogTag";
        public static final String MULTI_DELETE_PATIENT_DIALOG_TAG = "multiDeletePatientDialogTag";
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
        public static final String PATIENTS_START_INDEX = "patientsStartIndex";
        public static final String PROVIDER_ID_BUNDLE = "providerID";
        public static final String EXISTING_PROVIDERS_BUNDLE = "existingProviders";
    }

    public abstract static class ServiceActions {
        public static final String START_CONCEPT_DOWNLOAD_ACTION = "com.openmrs.mobile.services.conceptdownloadservice.action.startforeground";
        public static final String STOP_CONCEPT_DOWNLOAD_ACTION = "com.openmrs.mobile.services.conceptdownloadservice.action.stopforeground";
    }

    public abstract static class BroadcastActions {
        public static final String CONCEPT_DOWNLOAD_BROADCAST_INTENT_ID = "com.openmrs.mobile.services.conceptdownloadservice.action.broadcastintent";
        public static final String CONCEPT_DOWNLOAD_BROADCAST_INTENT_KEY_COUNT = "com.openmrs.mobile.services.conceptdownloadservice.broadcastintent.key.count";
        public static final String AUTHENTICATION_CHECK_BROADCAST_ACTION="org.openmrs.mobile.services.AuthenticateCheckService";
    }

    public abstract static class ServiceNotificationId {
        public static final int CONCEPT_DOWNLOADFOREGROUND_SERVICE = 101;
    }

    public abstract static class SystemSettingKeys {
        public static final String WS_REST_MAX_RESULTS_ABSOLUTE = "webservices.rest.maxResultsAbsolute";
    }

    public abstract static class EncounterTypes {
        public static final String VITALS = "67a71486-1a54-468f-ac3e-7091a9a79584";
        public static String[] ENCOUNTER_TYPES_DISPLAYS = {EncounterType.VITALS, EncounterType.ADMISSION, EncounterType.DISCHARGE, EncounterType.VISIT_NOTE};
    }

    public abstract static class RequestCodes{
        public static final int ADD_PROVIDER_REQ_CODE = 100;
        public static final int EDIT_PROVIDER_REQ_CODE = 101;
    }

    public abstract static class OpenMRSThemes{
        public static final String KEY_DARK_MODE = "key_dark_mode";
    }

    public abstract static class OpenMRSlanguage{
        public static final String KEY_LANGUAGE_MODE = "key_language_mode";
        public static final String[] LANGUAGE_LIST = {"en", "hi"};
    }

}
