/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.mobile.services;

import javax.inject.Inject;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;
import retrofit2.Response;
import android.app.IntentService;
import android.content.Intent;

import com.openmrs.android_sdk.library.api.RestApi;
import com.openmrs.android_sdk.library.dao.EncounterTypeRoomDAO;
import com.openmrs.android_sdk.library.dao.FormResourceDAO;
import com.openmrs.android_sdk.library.databases.AppDatabase;
import com.openmrs.android_sdk.library.databases.entities.FormResourceEntity;
import com.openmrs.android_sdk.library.models.EncounterType;
import com.openmrs.android_sdk.library.models.Results;
import com.openmrs.android_sdk.utilities.NetworkUtils;
import com.openmrs.android_sdk.utilities.ToastUtil;

@AndroidEntryPoint
public class FormListService extends IntentService {
    @Inject
    RestApi apiService;
    @Inject
    AppDatabase appDatabase;

    public FormListService() {
        super("Sync Form List");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (!NetworkUtils.isOnline()) return;
        // Refresh forms
        FormResourceDAO formResourceDAO = appDatabase.formResourceDAO();
        Response<Results<FormResourceEntity>> response = null;
        try {
            response = apiService.getForms().execute();
            if (!response.isSuccessful()) ToastUtil.error(response.message());
            formResourceDAO.deleteAllForms();
            List<FormResourceEntity> formResourceList = response.body().getResults();
            for (FormResourceEntity formResourceEntity : formResourceList) {
                formResourceDAO.addFormResource(formResourceEntity);
            }
        } catch (Exception e) {
            ToastUtil.error(response.message());
        }
        // Refresh encounter types
        EncounterTypeRoomDAO encounterTypeRoomDAO = appDatabase.encounterTypeRoomDAO();
        Response<Results<EncounterType>> response2 = null;
        try {
            response2 = apiService.getEncounterTypes().execute();
            if (!response2.isSuccessful()) ToastUtil.error(response2.message());
            encounterTypeRoomDAO.deleteAllEncounterTypes();
            List<EncounterType> encounterTypeList = response2.body().getResults();
            for (EncounterType encounterType : encounterTypeList) {
                encounterTypeRoomDAO.addEncounterType(encounterType);
            }
        } catch (Exception e) {
            ToastUtil.error(response2.message());
        }
    }
}
