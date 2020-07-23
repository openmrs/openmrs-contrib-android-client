/*
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
import android.content.Intent;

import androidx.annotation.NonNull;

import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.dao.EncounterTypeRoomDAO;
import org.openmrs.mobile.dao.FormResourceDAO;
import org.openmrs.mobile.databases.AppDatabase;
import org.openmrs.mobile.databases.entities.FormResourceEntity;
import org.openmrs.mobile.models.Encounter;
import org.openmrs.mobile.models.EncounterType;
import org.openmrs.mobile.models.Results;
import org.openmrs.mobile.utilities.NetworkUtils;
import org.openmrs.mobile.utilities.ToastUtil;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FormListService extends IntentService {
    private final RestApi apiService = RestServiceBuilder.createService(RestApi.class);
    private List<FormResourceEntity> formresourcelist;

    public FormListService() {
        super("Sync Form List");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        FormResourceDAO formResourceDAO = AppDatabase.getDatabase(OpenMRS.getInstance().getApplicationContext()).formResourceDAO();
        EncounterTypeRoomDAO encounterTypeRoomDAO = AppDatabase.getDatabase(OpenMRS.getInstance().getApplicationContext()).encounterTypeRoomDAO();
        if (NetworkUtils.isOnline()) {

            Call<Results<FormResourceEntity>> call = apiService.getForms();
            call.enqueue(new Callback<Results<FormResourceEntity>>() {
                @Override
                public void onResponse(@NonNull Call<Results<FormResourceEntity>> call, @NonNull Response<Results<FormResourceEntity>> response) {
                    if (response.isSuccessful()) {
                        formResourceDAO.deleteAllForms();
                        formresourcelist = response.body().getResults();
                        int size = formresourcelist.size();
                        for (int i = 0; i < size; i++) {
                            formResourceDAO.addFormResource(formresourcelist.get(i));
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Results<FormResourceEntity>> call, @NonNull Throwable t) {
                    ToastUtil.error(t.getMessage());
                }
            });

            Call<Results<EncounterType>> call2 = apiService.getEncounterTypes();
            call2.enqueue(new Callback<Results<EncounterType>>() {
                @Override
                public void onResponse(@NonNull Call<Results<EncounterType>> call, @NonNull Response<Results<EncounterType>> response) {
                    if (response.isSuccessful()) {
                        encounterTypeRoomDAO.deleteAllEncounterTypes();
                        Results<EncounterType> encounterTypeList = response.body();
                        for (EncounterType encounterType : encounterTypeList.getResults())
                            encounterTypeRoomDAO.addEncounterType(encounterType);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Results<EncounterType>> call, @NonNull Throwable t) {
                    ToastUtil.error(t.getMessage());
                }
            });
        }
    }
}