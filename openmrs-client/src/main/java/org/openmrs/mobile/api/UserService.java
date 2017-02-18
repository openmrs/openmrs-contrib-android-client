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

package org.openmrs.mobile.api;

import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.models.Results;
import org.openmrs.mobile.models.User;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.ToastUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserService {

    public void updateUserInformation(final String username) {
        RestApi restApi = RestServiceBuilder.createService(RestApi.class);
        Call<Results<User>> call = restApi.getUserInfo(username);
        call.enqueue(new Callback<Results<User>>() {
            @Override
            public void onResponse(Call<Results<User>> call, Response<Results<User>> response) {
                if (response.isSuccessful()) {
                    List<User> resultList = response.body().getResults();
                    boolean matchFound = false;
                    if (resultList.size() > 0) {
                        for (User user : resultList) {
                            if (user.getDisplay().toUpperCase().equals(username.toUpperCase())) {
                                matchFound = true;
                                fetchFullUserInformation(user.getUuid());
                            }
                        }
                        if (!matchFound) {
                            ToastUtil.error("Couldn't fetch user data");
                        }
                    }
                }
                else {
                    ToastUtil.error(response.message());
                }
            }

            @Override
            public void onFailure(Call<Results<User>> call, Throwable t) {
                ToastUtil.error(t.getMessage());
            }
        });
    }

    private void fetchFullUserInformation(String uuid) {
        RestApi restApi = RestServiceBuilder.createService(RestApi.class);
        Call<User> call = restApi.getFullUserInfo(uuid);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    Map<String, String> userInfo = new HashMap<>();
                    userInfo.put(ApplicationConstants.UserKeys.USER_PERSON_NAME, response.body().getPerson().getDisplay());
                    userInfo.put(ApplicationConstants.UserKeys.USER_UUID, response.body().getPerson().getUuid());
                    OpenMRS.getInstance().setCurrentUserInformation(userInfo);
                }
                else {
                    ToastUtil.error(response.message());
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                ToastUtil.error(t.getMessage());
            }
        });
    }
}
