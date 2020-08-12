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

package org.openmrs.mobile.api.workers.provider;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import org.jetbrains.annotations.NotNull;
import org.openmrs.mobile.R;
import org.openmrs.mobile.listeners.retrofitcallbacks.CustomResponseCallback;
import org.openmrs.mobile.api.RestApi;
import org.openmrs.mobile.api.RestServiceBuilder;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.dao.ProviderRoomDAO;
import org.openmrs.mobile.databases.AppDatabase;
import org.openmrs.mobile.models.Person;
import org.openmrs.mobile.models.PersonName;
import org.openmrs.mobile.models.Provider;
import org.openmrs.mobile.utilities.NetworkUtils;
import org.openmrs.mobile.utilities.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddProviderWorker extends Worker {
    ProviderRoomDAO providerRoomDao;
    RestApi restApi;

    public AddProviderWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        restApi = RestServiceBuilder.createService(RestApi.class);
        providerRoomDao = AppDatabase.getDatabase(getApplicationContext()).providerRoomDAO();
    }

    @NonNull
    @Override
    public Result doWork() {
        final boolean[] result = new boolean[1];
        String firstName = getInputData().getString("first_name");
        String lastName = getInputData().getString("last_name");
        String identifier = getInputData().getString("identifier");
        Person person = createPerson(firstName, lastName);
        Provider providerTobeCreated = createNewProvider(person, identifier, getInputData().getLong("id", 0l));

        addProvider(restApi, providerTobeCreated, new CustomResponseCallback() {
            @Override
            public void onResponse() {
                result[0] = true;
                ToastUtil.success(OpenMRS.getInstance().getString(R.string.add_provider_success_msg));
                OpenMRS.getInstance().getOpenMRSLogger().e("Adding Provider Successful ");
            }

            @Override
            public void onErrorResponse() {
                result[0] = false;
            }
        });

        if (result[0]) {
            return Result.success();
        } else {
            return Result.retry();
        }
    }

    private Person createPerson(String firstName, String lastName) {
        Person person = new Person();

        PersonName personName = new PersonName();
        personName.setGivenName(firstName);
        personName.setFamilyName(lastName);
        person.setUuid(null);

        person.setDisplay(firstName + " " + lastName);
        List<PersonName> names = new ArrayList<>();
        names.add(personName);
        person.setNames(names);

        return person;
    }

    private Provider createNewProvider(Person person, String identifier, Long providerId) {
        Provider provider = new Provider();
        provider.setPerson(person);
        provider.setUuid(null);
        provider.setIdentifier(identifier);
        provider.setRetired(false);
        provider.setId(providerId);

        return provider;
    }

    private void addProvider(RestApi restApi, Provider provider, CustomResponseCallback callback) {
        if (NetworkUtils.isOnline()) {
            restApi.addProvider(provider).enqueue(new Callback<Provider>() {
                @Override
                public void onResponse(@NotNull Call<Provider> call, @NotNull Response<Provider> response) {
                    if (response.isSuccessful()) {

                        //offline modifying the uuid of provider
                        providerRoomDao.updateProviderByUuid(response.body().getDisplay(), provider.getId(), response.body().getPerson(), response.body().getUuid(),
                            response.body().getIdentifier());

                        callback.onResponse();
                    }
                }

                @Override
                public void onFailure(@NotNull Call<Provider> call, @NotNull Throwable t) {
                    callback.onErrorResponse();
                }
            });
        }
    }
}
