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
package com.example.openmrs_android_sdk.utilities

import com.example.openmrs_android_sdk.library.models.EncounterType


object ApplicationConstants {
    const val UUID_LENGTH = 36
    const val EMPTY_STRING = ""
    const val SERVER_URL = "server_url"
    const val SESSION_TOKEN = "session_id"
    const val AUTHORIZATION_TOKEN = "authorisation"
    const val SECRET_KEY = "secretKey"
    const val LOCATION = "location"
    const val FIRST = true
    const val VISIT_TYPE_UUID = "visit_type_uuid"
    const val LAST_SESSION_TOKEN = "last_session_id"
    const val LAST_LOGIN_SERVER_URL = "last_login_server_url"
    const val DEFAULT_OPEN_MRS_URL = "https://demo.openmrs.org/openmrs"
    const val DB_NAME = "openmrs.db"
    const val DB_PASSWORD_BCRYPT_PEPPER = "$2a$08\$iUp3M1VapYpjcAXQBNX6uu"
    const val DB_PASSWORD_LITERAL_PEPPER = "Open Sesame"
    const val DEFAULT_VISIT_TYPE_UUID = "7b0f5697-27e3-40c4-8bae-f4049abfb4ed"
    const val DEFAULT_BCRYPT_ROUND = 8
    const val SPLASH_TIMER = 3500
    const val PACKAGE_NAME = "org.openmrs.mobile"
    const val USER_GUIDE = "https://openmrs.github.io/openmrs-android-client-user-guide/getting-started.html"
    const val MESSAGE_RFC_822 = "message/rfc822"
    const val FLAG = "flag"
    const val ERROR = "error"
    const val URI_FILE = "file://"
    const val URI_IMAGE = "image/*"
    const val IMAGE_JPEG = "image/jpeg"
    const val INTENT_KEY_PHOTO = "photo"
    const val INTENT_KEY_NAME = "name"
    const val READ_MODE = "r"
    const val MIME_TYPE_MAILTO = "mailto:"
    const val OPENMRS_PREF_FILE = "OpenMRSPrefFile"
    const val VITAL_NAME = "vitalName"
    const val BUNDLE = "bundle"
    const val URI_CONTENT = "content://"
    const val MIME_TYPE_VND = "vnd"
    const val ASPECT_RATIO_FOR_CROPPING = 5f
    const val CAUSE_OF_DEATH = "concept.causeOfDeath"
    const val MALE = "M";
    const val EMPTY_DASH_REPRESENTATION = "---"
    const val COMMA_WITH_SPACE = ", "
    const val PRIMARY_KEY_ID = "_id"
    const val MIN_NUMBER_OF_PATIENTS_TO_SHOW = 7;
    const val ABOUT_OPENMRS_URL = "https://openmrs.org/about/"

    object OpenMRSSharedPreferenceNames {
        const val SHARED_PREFERENCES_NAME = "shared_preferences"
    }

    object API {
        const val REST_ENDPOINT = "/ws/rest/v1/"
        const val FULL = "full"
        const val TAG_ADMISSION_LOCATION="Admission Location"
    }

    object UserKeys {
        const val USER_NAME = "username"
        const val PASSWORD = "password"
        const val HASHED_PASSWORD = "hashedPassword"
        const val USER_PERSON_NAME = "userDisplay"
        const val USER_UUID = "userUUID"
        const val LOGIN = "login"
        const val FIRST_TIME = "firstTime"
    }

    object DialogTAG {
        const val LOGOUT_DIALOG_TAG = "logoutDialog"
        const val END_VISIT_DIALOG_TAG = "endVisitDialogTag"
        const val START_VISIT_DIALOG_TAG = "startVisitDialog"
        const val START_VISIT_IMPOSSIBLE_DIALOG_TAG = "startVisitImpossibleDialog"
        const val WARNING_LOST_DATA_DIALOG_TAG = "warningLostDataDialog"
        const val SIMILAR_PATIENTS_TAG = "similarPatientsDialogTag"
        const val DELETE_PATIENT_DIALOG_TAG = "deletePatientDialogTag"
        const val DELETE_PROVIDER_DIALOG_TAG = "deleteProviderDialogTag"
        const val LOCATION_DIALOG_TAG = "locationDialogTag"
        const val CREDENTIAL_CHANGED_DIALOG_TAG = "locationDialogTag"
        const val MULTI_DELETE_PATIENT_DIALOG_TAG = "multiDeletePatientDialogTag"
    }

    object RegisterPatientRequirements {
        const val MAX_PATIENT_AGE = 120
    }

    object BundleKeys {
        const val CUSTOM_DIALOG_BUNDLE = "customDialogBundle"
        const val PATIENT_ID_BUNDLE = "patientID"
        const val VISIT_ID = "visitID"
        const val ENCOUNTERTYPE = "encounterType"
        const val VALUEREFERENCE = "valueReference"
        const val FORM_NAME = "formName"
        const val CALCULATED_LOCALLY = "CALCULATED_LOCALLY"
        const val PATIENTS_AND_MATCHES = "PATIENTS_AND_MATCHES"
        const val FORM_FIELDS_BUNDLE = "formFieldsBundle"
        const val FORM_FIELDS_LIST_BUNDLE = "formFieldsListBundle"
        const val PATIENT_QUERY_BUNDLE = "patientQuery"
        const val PATIENTS_START_INDEX = "patientsStartIndex"
        const val PROVIDER_ID_BUNDLE = "providerID"
        const val EXISTING_PROVIDERS_BUNDLE = "existingProviders"
        const val ALLERGY_UUID = "allergy_uuid"
        const val PATIENT_UUID = "patient_uuid"
    }

    object ServiceActions {
        const val START_CONCEPT_DOWNLOAD_ACTION = "com.openmrs.mobile.services.conceptdownloadservice.action.startforeground"
        const val STOP_CONCEPT_DOWNLOAD_ACTION = "com.openmrs.mobile.services.conceptdownloadservice.action.stopforeground"
    }

    object BroadcastActions {
        const val CONCEPT_DOWNLOAD_BROADCAST_INTENT_ID = "com.openmrs.mobile.services.conceptdownloadservice.action.broadcastintent"
        const val CONCEPT_DOWNLOAD_BROADCAST_INTENT_KEY_COUNT = "com.openmrs.mobile.services.conceptdownloadservice.broadcastintent.key.count"
        const val AUTHENTICATION_CHECK_BROADCAST_ACTION = "org.openmrs.mobile.services.AuthenticateCheckService"
    }

    object ServiceNotificationId {
        const val CONCEPT_DOWNLOADFOREGROUND_SERVICE = 101
    }

    object SystemSettingKeys {
        const val WS_REST_MAX_RESULTS_ABSOLUTE = "webservices.rest.maxResultsAbsolute"
    }

    object EncounterTypes {
        const val VITALS = "67a71486-1a54-468f-ac3e-7091a9a79584"

        @JvmField
        var ENCOUNTER_TYPES_DISPLAYS = arrayOf(EncounterType.VITALS, EncounterType.ADMISSION, EncounterType.DISCHARGE, EncounterType.VISIT_NOTE)
    }

    object RequestCodes {
        const val ADD_PROVIDER_REQ_CODE = 100
        const val EDIT_PROVIDER_REQ_CODE = 101
        const val START_SETTINGS_REQ_CODE = 102
        const val IMAGE_REQUEST = 1
        const val GALLERY_IMAGE_REQUEST = 2
    }

    object OpenMRSThemes {
        const val KEY_DARK_MODE = "key_dark_mode"
    }

    object OpenMRSlanguage {
        const val KEY_LANGUAGE_MODE = "key_language_mode"
        val LANGUAGE_LIST = arrayOf("English", "हिन्दी")
        val LANGUAGE_CODE = arrayOf("en", "hi")
    }

    object ShowCaseViewConstants {
        const val SHOW_FIND_PATIENT = 1
        const val SHOW_ACTIVE_VISITS = 2
        const val SHOW_REGISTER_PATIENT = 3
        const val SHOW_FORM_ENTRY = 4
        const val SHOW_MANAGE_PROVIDERS = 5
    }

    object TypeFacePathConstants {
        const val MONTSERRAT = "fonts/Roboto/Montserrat.ttf"
        const val ROBOTO_LIGHT = "fonts/Roboto/Roboto-Light.ttf"
        const val ROBOTO_LIGHT_ITALIC = "fonts/Roboto/Roboto-LightItalic.ttf"
        const val ROBOTO_MEDIUM = "fonts/Roboto/Roboto-Medium.ttf"
        const val ROBOTO_MEDIUM_ITALIC = "fonts/Roboto/Roboto-MediumItalic.ttf"
        const val ROBOTO_REGULAR = "fonts/Roboto/Roboto-Regular.ttf"
    }

    object PatientDashboardTabs {
        const val DETAILS_TAB_POS = 0
        const val ALLERGY_TAB_POS = 1
        const val DIAGNOSIS_TAB_POS = 2
        const val VISITS_TAB_POS = 3
        const val VITALS_TAB_POS = 4
        const val CHARTS_TAB_POS = 5
        const val TAB_COUNT = 6
    }

    object ConceptDownloadService {
        const val CHANNEL_ID = "conceptCount"
        const val CHANNEL_DESC = "This channel receives new concept count notifications"
        const val CHANNEL_NAME = "Concepts Channel"
    }

    object AllergyModule {
        const val CONCEPT_ALLERGEN_DRUG = "allergy.concept.allergen.drug"
        const val CONCEPT_ALLERGEN_ENVIRONMENT = "allergy.concept.allergen.environment"
        const val CONCEPT_ALLERGEN_FOOD = "allergy.concept.allergen.food"
        const val CONCEPT_REACTION = "allergy.concept.reactions"
        const val CONCEPT_SEVERITY_MILD = "allergy.concept.severity.mild"
        const val CONCEPT_SEVERITY_MODERATE = "allergy.concept.severity.moderate"
        const val CONCEPT_SEVERITY_SEVERE = "allergy.concept.severity.severe"
        const val PROPERTY_REACTION = "REACTION"
        const val PROPERTY_FOOD = "FOOD"
        const val PROPERTY_DRUG = "DRUG"
        const val PROPERTY_OTHER = "OTHER"
        const val PROPERTY_MILD = "Mild"
        const val PROPERTY_SEVERE = "Severe"
        const val SELECT_ALLERGEN = "Select Allergen"
        const val SELECT_REACTION = "Reactions (you can select multiple)"
    }
}
