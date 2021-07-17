/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package com.example.openmrs_android_sdk.library.api;

import android.util.Base64;

import com.chuckerteam.chucker.api.ChuckerInterceptor;
import com.example.openmrs_android_sdk.library.OpenmrsAndroid;
import com.example.openmrs_android_sdk.library.models.Observation;
import com.example.openmrs_android_sdk.library.models.Resource;
import com.example.openmrs_android_sdk.utilities.ApplicationConstants;
import com.example.openmrs_android_sdk.utilities.ObservationDeserializer;
import com.example.openmrs_android_sdk.utilities.ResourceSerializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RestServiceBuilder {
    private static String API_BASE_URL = OpenmrsAndroid.getServerUrl() + ApplicationConstants.API.REST_ENDPOINT;
    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
    private static Retrofit.Builder builder;

    static {
        builder =
                new Retrofit.Builder()
                        .baseUrl(API_BASE_URL)
                        .addConverterFactory(buildGsonConverter())
                        .client((httpClient).build());
    }

    public static <S> S createService(Class<S> serviceClass, String username, String password) {
        if (username != null && password != null) {
            String credentials = username + ":" + password;
            final String basic = "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);

            // header interceptor
            httpClient.addNetworkInterceptor(chain -> {
                Request original = chain.request();

                Request.Builder requestBuilder = original.newBuilder()
                        .header("Authorization", basic)
                        .header("Accept", "application/json")
                        .method(original.method(), original.body());

                Request request = requestBuilder.build();
                return chain.proceed(request);
            });

            httpClient.addInterceptor(new ChuckerInterceptor(OpenmrsAndroid.getInstance()));
        }
        OkHttpClient client = httpClient.build();
        Retrofit retrofit = builder.client(client).build();
        return retrofit.create(serviceClass);
    }

    public static <S> S createService(Class<S> serviceClass) {
        String username = OpenmrsAndroid.getUsername();
        String password = OpenmrsAndroid.getPassword();
        return createService(serviceClass, username, password);
    }

    private static GsonConverterFactory buildGsonConverter() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson myGson = gsonBuilder
                .excludeFieldsWithoutExposeAnnotation()
                .registerTypeHierarchyAdapter(Resource.class, new ResourceSerializer())
                .registerTypeHierarchyAdapter(Observation.class, new ObservationDeserializer())
                .create();

        return GsonConverterFactory.create(myGson);
    }

    public static <S> S createServiceForPatientIdentifier(Class<S> clazz) {
        return new Retrofit.Builder()
                .baseUrl(OpenmrsAndroid.getServerUrl() + '/')
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(clazz);
    }

    public static void changeBaseUrl(String newServerUrl) {
        API_BASE_URL = newServerUrl + ApplicationConstants.API.REST_ENDPOINT;

        builder = new Retrofit.Builder()
                .baseUrl(API_BASE_URL)
                .addConverterFactory(buildGsonConverter());
    }
}