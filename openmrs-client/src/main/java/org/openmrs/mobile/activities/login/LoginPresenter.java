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

package org.openmrs.mobile.activities.login;

import org.openmrs.mobile.R;
import org.openmrs.mobile.api.RestApi;
import org.openmrs.mobile.api.RestServiceBuilder;
import org.openmrs.mobile.api.retrofit.VisitApi;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.application.OpenMRSLogger;
import org.openmrs.mobile.dao.LocationDAO;
import org.openmrs.mobile.databases.OpenMRSSQLiteOpenHelper;
import org.openmrs.mobile.listeners.retrofit.GetVisitTypeCallbackListener;
import org.openmrs.mobile.models.Location;
import org.openmrs.mobile.models.retrofit.Results;
import org.openmrs.mobile.models.retrofit.Session;
import org.openmrs.mobile.models.retrofit.VisitType;
import org.openmrs.mobile.net.AuthorizationManager;
import org.openmrs.mobile.net.UserManager;
import org.openmrs.mobile.net.helpers.UserHelper;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.ToastUtil;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginPresenter implements LoginContract.Presenter{

    private LoginContract.View loginView;
    private OpenMRS mOpenMRS;
    private OpenMRSLogger mLogger;
    private AuthorizationManager authorizationManager;

    public LoginPresenter(LoginContract.View loginView, OpenMRS openMRS) {
        this.loginView = loginView;
        this.mOpenMRS = openMRS;
        this.mLogger = openMRS.getOpenMRSLogger();
        this.loginView.setPresenter(this);
        this.authorizationManager = new AuthorizationManager();
    }

    @Override
    public void start() {}

    @Override
    public void login(String username, String password, String url) {
        if (validateLoginFields(username, password)) {
            if ((!mOpenMRS.getUsername().equals(ApplicationConstants.EMPTY_STRING) &&
                    !mOpenMRS.getUsername().equals(username)) ||
                    ((!mOpenMRS.getServerUrl().equals(ApplicationConstants.EMPTY_STRING) &&
                            !mOpenMRS.getServerUrl().equals(url)))) {
                loginView.showWarningDialog();
            } else {
                loginView.showLoadingAnimation();
                authenticateUser(username, password, url);
            }
        } else {
            loginView.showToast(ToastUtil.ToastType.ERROR, R.string.login_dialog_login_or_password_empty);
        }
    }

    @Override
    public void authenticateUser(final String username, final String password, final String url) {
        loginView.showLoadingAnimation();
        RestApi restApi = RestServiceBuilder.createService(RestApi.class, username, password);
        Call<Session> call = restApi.getSession();
        call.enqueue(new Callback<Session>() {
            @Override
            public void onResponse(Call<Session> call, Response<Session> response) {
                mLogger.d(response.body().toString());
                if (response.isSuccessful()) {
                    Session session = response.body();
                    if (session.isAuthenticated()) {
                        if (authorizationManager.isDBCleaningRequired(username, url)) {
                            mOpenMRS.deleteDatabase(OpenMRSSQLiteOpenHelper.DATABASE_NAME);
                            setData(session.getSessionId(), url, username, password);
                        } else if (authorizationManager.isUserNameOrServerEmpty()) {
                            setData(session.getSessionId(), url, username, password);
                        } else {
                            mOpenMRS.setSessionToken(session.getSessionId());
                        }

                        new VisitApi().getVisitType(new GetVisitTypeCallbackListener() {
                            @Override
                            public void onGetVisitTypeResponse(VisitType visitType) {
                                OpenMRS.getInstance().setVisitTypeUUID(visitType.getUuid());
                            }
                            @Override
                            public void onResponse() {}
                            @Override
                            public void onErrorResponse() {
                                ToastUtil.error("Failed to fetch visit type");
                            }
                        });

                        UserManager userManager = new UserManager();
                        userManager.getUserInformation(
                                UserHelper.createUserInformationListener(username, userManager));
                        loginView.userAuthenticated();
                        loginView.finishLoginActivity();
                    } else {
                        loginView.sendIntentBroadcast(ApplicationConstants.CustomIntentActions.ACTION_AUTH_FAILED_BROADCAST);
                    }
                } else {
                    loginView.hideLoadingAnimation();
                    ToastUtil.error(response.message());
                }
            }

            @Override
            public void onFailure(Call<Session> call, Throwable t) {
                loginView.hideLoadingAnimation();
                ToastUtil.error(t.getMessage());
            }
        });
    }

    @Override
    public void saveLocationsToDatabase(List<Location> locationList, String selectedLocation) {
        mOpenMRS.setLocation(selectedLocation);
        new LocationDAO().deleteAllLocations();
        for (int i = 0; i < locationList.size(); i++) {
            new LocationDAO().saveLocation(locationList.get(i));
        }
    }

    @Override
    public void loadLocations(final String url) {
        loginView.showLocationLoadingAnimation();
        loginView.enableLocationSpinner(false);
        String locationEndPoint = url + ApplicationConstants.API.REST_ENDPOINT + "location";
        RestApi restApi = RestServiceBuilder.createService(RestApi.class);
        Call<Results<Location>> call = restApi.getLocations(locationEndPoint, "Login Location", "full");
        call.enqueue(new Callback<Results<Location>>() {
            @Override
            public void onResponse(Call<Results<Location>> call, Response<Results<Location>> response) {
                if(response.isSuccessful()){
                    RestServiceBuilder.changeBaseUrl(url.trim());
                    mOpenMRS.setServerUrl(url);
                    loginView.initLoginForm(response.body().getResults(), url);
                    loginView.startFormListService();
                    loginView.enableLocationSpinner(true);
                    loginView.setErrorOccurred(false);
                } else {
                    loginView.enableLocationSpinner(false);
                    loginView.showInvalidURLSnackbar("Failed to fetch server's locations");
                    loginView.setErrorOccurred(true);
                    loginView.sendIntentBroadcast(ApplicationConstants.CustomIntentActions.ACTION_SERVER_NOT_SUPPORTED_BROADCAST);
                }
                loginView.hideUrlLoadingAnimation();
            }

            @Override
            public void onFailure(Call<Results<Location>> call, Throwable t) {
                loginView.hideUrlLoadingAnimation();
                loginView.showInvalidURLSnackbar(t.getMessage());
                loginView.setErrorOccurred(true);
                loginView.enableLocationSpinner(false);
            }
        });
    }

    private boolean validateLoginFields(String username, String password) {
        return !(ApplicationConstants.EMPTY_STRING.equals(username)
                || ApplicationConstants.EMPTY_STRING.equals(password));
    }

    private void setData(String sessionToken,String url, String username, String password) {
        mOpenMRS.setSessionToken(sessionToken);
        mOpenMRS.setServerUrl(url);
        mOpenMRS.setUsername(username);
        mOpenMRS.setPassword(password);
    }
}
