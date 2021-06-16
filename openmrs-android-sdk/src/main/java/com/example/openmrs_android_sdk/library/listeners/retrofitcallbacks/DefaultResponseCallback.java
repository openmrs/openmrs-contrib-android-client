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

package com.example.openmrs_android_sdk.library.listeners.retrofitcallbacks;

/**
 * this method {@link #onResponse()} () onResponse}
 * will be overridden in custom callback methods if the response matches with the use case.
 * otherwise  method will be overloaded in the custom callbacks
 * this method {@link #onErrorResponse(String)}() onErrorResponse}
 * will be overridden in custom callback methods since it has uniform usage throughout the
 * app in various repositories
 */
public interface DefaultResponseCallback {
    default void onResponse() {
    }

    default void onErrorResponse(String errorMessage) {
    }
}
