package org.openmrs.client.utilities;

public abstract class ApplicationConstants {
    public static final String EMPTY_STRING = "";

    public abstract static class OpenMRSSharedPreferenceNames {
        public static final String SHARED_PREFERENCES_NAME = "shared_preferences";
    }

    public static final String USER_NAME = "username";
    public static final String SERVER_URL = "server_url";
    public static final String SESSION_TOKEN = "session_id";

    public abstract static class API {
        public static final String COMMON_PART = "/ws/rest/v1/";
        public static final String AUTHORISATION_END_POINT = "session";
    }

    public abstract static class DialogTAG {
        public static final String URL_DIALOG_TAG = "urlDialog";
    }

    public abstract static class BundleKeys {
        public static final String CUSTOM_DIALOG_BUNDLE = "customDialogBundle";
    }

}
