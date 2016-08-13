/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.mobile.api;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Delete;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.FormDisplayActivity;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.dao.EncounterDAO;
import org.openmrs.mobile.dao.VisitDAO;
import org.openmrs.mobile.models.Visit;
import org.openmrs.mobile.models.retrofit.Encounter;
import org.openmrs.mobile.models.retrofit.EncounterType;
import org.openmrs.mobile.models.retrofit.Encountercreate;
import org.openmrs.mobile.models.retrofit.FormResource;
import org.openmrs.mobile.models.retrofit.Results;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.NetworkUtils;
import org.openmrs.mobile.utilities.ToastUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FormListService extends IntentService {
    final RestApi apiService =
            RestServiceBuilder.createService(RestApi.class);
    private List<FormResource> formresourcelist;

    public FormListService() {
        super("Sync Form List");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if(NetworkUtils.isNetworkAvailable()) {

            Call<Results<FormResource>> call = apiService.getForms();
            call.enqueue(new Callback<Results<FormResource>>() {

                @Override
                public void onResponse(Call<Results<FormResource>> call, Response<Results<FormResource>> response) {
                    if (response.isSuccessful()) {
                        new Delete().from(FormResource.class).execute();
                        formresourcelist=response.body().getResults();
                        int size=formresourcelist.size();
                        ActiveAndroid.beginTransaction();
                        try {
                            for (int i = 0; i < size; i++)
                            {
                                formresourcelist.get(i).setResourcelist();
                                formresourcelist.get(i).save();
                            }
                            ActiveAndroid.setTransactionSuccessful();
                        }
                        finally {
                            ActiveAndroid.endTransaction();
                        }

                    }

                }

                @Override
                public void onFailure(Call<Results<FormResource>> call, Throwable t) {
                    ToastUtil.error(t.getMessage());
                }
            });



            Call<Results<EncounterType>> call2 = apiService.getEncounterTypes();
            call2.enqueue(new Callback<Results<EncounterType>>() {
                @Override
                public void onResponse(Call<Results<EncounterType>> call, Response<Results<EncounterType>> response) {
                    if (response.isSuccessful()) {
                        new Delete().from(EncounterType.class).execute();
                        Results<EncounterType> encountertypelist = response.body();
                            for (EncounterType enctype : encountertypelist.getResults())
                                enctype.save();
                    }

                }

                @Override
                public void onFailure(Call<Results<EncounterType>> call, Throwable t) {
                    ToastUtil.error(t.getMessage());

                }
            });



        } else {
            //ToastUtil.notify("No internet connection. Showing previously downloaded forms");
        }
    }

}