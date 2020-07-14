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

package org.openmrs.mobile.api.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.jetbrains.annotations.NotNull;
import org.openmrs.mobile.R;
import org.openmrs.mobile.api.RestApi;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.models.Allergy;
import org.openmrs.mobile.models.Results;
import org.openmrs.mobile.utilities.ToastUtil;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AllergyRepository {

    public AllergyRepository() {
    }

    public LiveData<List<Allergy>> getAllergies(RestApi restApi, String uuid) {
        MutableLiveData<List<Allergy>> allergyLiveData = new MutableLiveData<>();

        restApi.getAllergies(uuid).enqueue(new Callback<Results<Allergy>>() {
            @Override
            public void onResponse(@NotNull Call<Results<Allergy>> call, @NotNull Response<Results<Allergy>> response) {
                if (response.isSuccessful()) {
                    allergyLiveData.setValue(response.body().getResults());
                } else {
                    ToastUtil.error(OpenMRS.getInstance().getString(R.string.unable_to_fetch_allergies));
                    allergyLiveData.setValue(null);
                }
            }

            @Override
            public void onFailure(@NotNull Call<Results<Allergy>> call, @NotNull Throwable t) {
                ToastUtil.error(OpenMRS.getInstance().getString(R.string.unable_to_fetch_allergies));
                allergyLiveData.setValue(null);
            }
        });
        return allergyLiveData;
    }
}
