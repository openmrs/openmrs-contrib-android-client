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
import org.openmrs.mobile.api.UserService;
import org.openmrs.mobile.api.retrofit.VisitApi;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.application.OpenMRSLogger;
import org.openmrs.mobile.dao.LocationDAO;
import org.openmrs.mobile.databases.OpenMRSSQLiteOpenHelper;
import org.openmrs.mobile.listeners.retrofit.GetVisitTypeCallbackListener;
import org.openmrs.mobile.models.Location;
import org.openmrs.mobile.models.Results;
import org.openmrs.mobile.models.Session;
import org.openmrs.mobile.models.VisitType;
import org.openmrs.mobile.net.AuthorizationManager;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.NetworkUtils;
import org.openmrs.mobile.utilities.StringUtils;
import org.openmrs.mobile.utilities.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginPresenter implements LoginContract.Presenter{

    private LoginContract.View loginView;
    private OpenMRS mOpenMRS;
    private OpenMRSLogger mLogger;
    private AuthorizationManager authorizationManager;
    private boolean mWipeRequired;

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
    public void login(String username, String password, String url, String oldUrl) {
        if (validateLoginFields(username, password, url)) {
            loginView.hideSoftKeys();
            if ((!mOpenMRS.getUsername().equals(ApplicationConstants.EMPTY_STRING) &&
                    !mOpenMRS.getUsername().equals(username)) ||
                    ((!mOpenMRS.getServerUrl().equals(ApplicationConstants.EMPTY_STRING) &&
                            !mOpenMRS.getServerUrl().equals(oldUrl))) ||
                    mWipeRequired) {
                loginView.showWarningDialog();
            } else {
                loginView.showLoadingAnimation();
                authenticateUser(username, password, url);
            }
        }
    }

    @Override
    public void authenticateUser(final String username, final String password, final String url) {
        authenticateUser(username, password, url, mWipeRequired);
    }

    @Override
    public void authenticateUser(final String username, final String password, final String url, final boolean wipeDatabase) {
        loginView.showLoadingAnimation();

        if (NetworkUtils.isOnline()) {
            mWipeRequired = wipeDatabase;
            RestApi restApi = RestServiceBuilder.createService(RestApi.class, username, password);
            Call<Session> call = restApi.getSession();
            call.enqueue(new Callback<Session>() {
                @Override
                public void onResponse(Call<Session> call, Response<Session> response) {
                    mLogger.d(response.body().toString());
                    if (response.isSuccessful()) {
                        Session session = response.body();
                        if (session.isAuthenticated()) {
                            if (wipeDatabase) {
                                mOpenMRS.deleteDatabase(OpenMRSSQLiteOpenHelper.DATABASE_NAME);
                                setData(session.getSessionId(), url, username, password);
                                mWipeRequired = false;
                            }
                            if (authorizationManager.isUserNameOrServerEmpty()) {
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
                                public void onResponse() {
                                }

                                @Override
                                public void onErrorResponse(String errorMessage) {
                                    loginView.showToast("Failed to fetch visit type", ToastUtil.ToastType.ERROR);
                                }
                            });
                            setLogin(true, url);
                            new UserService().updateUserInformation(username);

                            loginView.userAuthenticated();
                            loginView.finishLoginActivity();
                        } else {
                            loginView.sendIntentBroadcast(ApplicationConstants.CustomIntentActions.ACTION_AUTH_FAILED_BROADCAST);
                        }
                    } else {
                        loginView.hideLoadingAnimation();
                        loginView.showToast(response.message(), ToastUtil.ToastType.ERROR);
                    }
                }

                @Override
                public void onFailure(Call<Session> call, Throwable t) {
                    loginView.hideLoadingAnimation();
                    loginView.showToast(t.getMessage(), ToastUtil.ToastType.ERROR);
                }
            });
        } else {
            if (mOpenMRS.isUserLoggedOnline() && url.equals(mOpenMRS.getLastLoginServerUrl())) {
                if (mOpenMRS.getUsername().equals(username) && mOpenMRS.getPassword().equals(password)) {
                    mOpenMRS.setSessionToken(mOpenMRS.getLastSessionToken());
                    loginView.showToast("LoggedIn in offline mode.", ToastUtil.ToastType.NOTICE);
                    loginView.userAuthenticated();
                    loginView.finishLoginActivity();
                } else {
                    loginView.hideLoadingAnimation();
                    loginView.showToast(R.string.auth_failed_dialog_message, ToastUtil.ToastType.ERROR);
                }
            } else if (NetworkUtils.hasNetwork()) {
                loginView.showToast(R.string.offline_mode_unsupported_in_first_login, ToastUtil.ToastType.ERROR);
                loginView.hideLoadingAnimation();
            } else {
                loginView.showToast(R.string.no_internet_conn_dialog_message, ToastUtil.ToastType.ERROR);
                loginView.hideLoadingAnimation();
            }
        }
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

        if (NetworkUtils.hasNetwork()) {
            String locationEndPoint = url + ApplicationConstants.API.REST_ENDPOINT + "location";
            RestApi restApi = RestServiceBuilder.createService(RestApi.class);
            Call<Results<Location>> call = restApi.getLocations(locationEndPoint, "Login Location", "full");
            call.enqueue(new Callback<Results<Location>>() {
                @Override
                public void onResponse(Call<Results<Location>> call, Response<Results<Location>> response) {
                    if (response.isSuccessful()) {
                        RestServiceBuilder.changeBaseUrl(url.trim());
                        mOpenMRS.setServerUrl(url);
                        loginView.initLoginForm(response.body().getResults(), url);
                        loginView.startFormListService();
                        loginView.setLocationErrorOccurred(false);
                    } else {
                        loginView.showInvalidURLSnackbar("Failed to fetch server's locations");
                        loginView.setLocationErrorOccurred(true);
                        loginView.initLoginForm(new ArrayList<Location>(), url);
                    }
                    loginView.hideUrlLoadingAnimation();
                }

                @Override
                public void onFailure(Call<Results<Location>> call, Throwable t) {
                    loginView.hideUrlLoadingAnimation();
                    loginView.showInvalidURLSnackbar(t.getMessage());
                    loginView.initLoginForm(new ArrayList<Location>(), url);
                    loginView.setLocationErrorOccurred(true);
                }
            });
        } else {
            List<Location> locations = LocationDAO.getLocations();
            if (locations.size() > 0) {
                loginView.initLoginForm(locations, url);
                loginView.setLocationErrorOccurred(false);
            } else {
                loginView.showToast("Network not available.", ToastUtil.ToastType.ERROR);
                loginView.setLocationErrorOccurred(true);
            }
            loginView.hideLoadingAnimation();
        }

    }

    private boolean validateLoginFields(String username, String password, String url) {
        return StringUtils.notEmpty(username) || StringUtils.notEmpty(password) || StringUtils.notEmpty(url);
    }

    private void setData(String sessionToken,String url, String username, String password) {
        mOpenMRS.setSessionToken(sessionToken);
        mOpenMRS.setServerUrl(url);
        mOpenMRS.setUsername(username);
        mOpenMRS.setPassword(password);
    }

    private void setLogin(boolean isLogin, String serverUrl) {
        mOpenMRS.setUserLoggedOnline(isLogin);
        mOpenMRS.setLastLoginServerUrl(serverUrl);
    }
}
