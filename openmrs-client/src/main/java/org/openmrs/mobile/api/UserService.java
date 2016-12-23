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

    public void fetchFullUserInformation(String uuid) {
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
