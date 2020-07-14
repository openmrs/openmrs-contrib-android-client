package org.openmrs.mobile.listeners.retrofit;

public interface DefaultVisitsCallback {
    void onSuccess(String response);

    void onFailure(String errorMessage);
}
