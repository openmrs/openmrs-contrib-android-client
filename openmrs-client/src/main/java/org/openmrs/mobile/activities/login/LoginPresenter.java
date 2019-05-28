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

import org.mindrot.jbcrypt.BCrypt;
import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.BasePresenter;
import org.openmrs.mobile.api.RestApi;
import org.openmrs.mobile.api.RestServiceBuilder;
import org.openmrs.mobile.api.UserService;
import org.openmrs.mobile.api.repository.VisitRepository;
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

import androidx.annotation.NonNull;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class LoginPresenter extends BasePresenter implements LoginContract.Presenter {

    private RestApi restApi;
    private VisitRepository visitRepository;
    private UserService userService;
    private LoginContract.View loginView;
    private OpenMRS mOpenMRS;
    private OpenMRSLogger mLogger;
    private AuthorizationManager authorizationManager;
    private LocationDAO locationDAO;
    private boolean mWipeRequired;

    public LoginPresenter(LoginContract.View loginView, OpenMRS openMRS) {
        this.loginView = loginView;
        this.mOpenMRS = openMRS;
        this.mLogger = openMRS.getOpenMRSLogger();
        this.loginView.setPresenter(this);
        this.authorizationManager = new AuthorizationManager();
        this.locationDAO = new LocationDAO();
        this.restApi = RestServiceBuilder.createService(RestApi.class);
        this.visitRepository = new VisitRepository();
        this.userService = new UserService();
    }

    public LoginPresenter(RestApi restApi, VisitRepository visitRepository, LocationDAO locationDAO,
                          UserService userService, LoginContract.View loginView, OpenMRS mOpenMRS,
                          OpenMRSLogger mLogger, AuthorizationManager authorizationManager) {
        this.restApi = restApi;
        this.visitRepository = visitRepository;
        this.locationDAO = locationDAO;
        this.userService = userService;
        this.loginView = loginView;
        this.mOpenMRS = mOpenMRS;
        this.mLogger = mLogger;
        this.authorizationManager = authorizationManager;
        this.loginView.setPresenter(this);
    }

    @Override
    public void subscribe() {
        // This method is intentionally empty
    }

    @Override
    public void login(String username, String password, String url, String oldUrl) {
        if (validateLoginFields(username, password, url)) {
            loginView.hideSoftKeys();
            if ((!mOpenMRS.getUsername().equals(ApplicationConstants.EMPTY_STRING) &&
                 !mOpenMRS.getUsername().equals(username)) ||
               ((!mOpenMRS.getServerUrl().equals(ApplicationConstants.EMPTY_STRING) &&
                 !mOpenMRS.getServerUrl().equals(oldUrl))) ||
               (!mOpenMRS.getHashedPassword().equals(ApplicationConstants.EMPTY_STRING) &&
                 !BCrypt.checkpw(password, mOpenMRS.getHashedPassword())) ||
               mWipeRequired) {
                loginView.showWarningDialog();
            } else {
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
                public void onResponse(@NonNull Call<Session> call, @NonNull Response<Session> response) {
                    if (response.isSuccessful()) {
                        mLogger.d(response.body().toString());
                        Session session = response.body();
                        if (session.isAuthenticated()) {
                            mOpenMRS.deleteSecretKey();
                            if (wipeDatabase) {
                                mOpenMRS.deleteDatabase(OpenMRSSQLiteOpenHelper.DATABASE_NAME);
                                setData(session.getSessionId(), url, username, password);
                                mWipeRequired = false;
                            }
                            if (authorizationManager.isUserNameOrServerEmpty()) {
                                setData(session.getSessionId(), url, username, password);
                            } else {
                                mOpenMRS.setSessionToken(session.getSessionId());
                                mOpenMRS.setPasswordAndHashedPassword(password);
                            }

                            visitRepository.getVisitType(new GetVisitTypeCallbackListener() {
                                @Override
                                public void onGetVisitTypeResponse(VisitType visitType) {
                                    OpenMRS.getInstance().setVisitTypeUUID(visitType.getUuid());
                                }

                                @Override
                                public void onResponse() {
                                    // This method is intentionally empty
                                }

                                @Override
                                public void onErrorResponse(String errorMessage) {

                                    OpenMRS.getInstance().setVisitTypeUUID(ApplicationConstants.DEFAULT_VISIT_TYPE_UUID);
                                    loginView.showToast("Failed to fetch visit type",
                                            ToastUtil.ToastType.ERROR);
                                }
                            });
                            setLogin(true, url);
                            userService.updateUserInformation(username);

                            loginView.userAuthenticated();
                            loginView.finishLoginActivity();
                        } else {
                            loginView.hideLoadingAnimation();
                            loginView.showInvalidLoginOrPasswordSnackbar();
                        }
                    } else {
                        loginView.hideLoadingAnimation();
                        loginView.showToast(response.message(), ToastUtil.ToastType.ERROR);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Session> call, @NonNull Throwable t) {
                    loginView.hideLoadingAnimation();
                    loginView.showToast(t.getMessage(), ToastUtil.ToastType.ERROR);
                }
            });
        } else {
            if (mOpenMRS.isUserLoggedOnline() && url.equals(mOpenMRS.getLastLoginServerUrl())) {
                if (mOpenMRS.getUsername().equals(username) && BCrypt.checkpw(password, mOpenMRS.getHashedPassword())) {
                    mOpenMRS.deleteSecretKey();
                    mOpenMRS.setPasswordAndHashedPassword(password);
                    mOpenMRS.setSessionToken(mOpenMRS.getLastSessionToken());
                    loginView.showToast(R.string.login_offline_toast_message,
                            ToastUtil.ToastType.NOTICE);
                    loginView.userAuthenticated();
                    loginView.finishLoginActivity();
                } else {
                    loginView.hideLoadingAnimation();
                    loginView.showToast(R.string.auth_failed_dialog_message,
                            ToastUtil.ToastType.ERROR);
                }
            } else if (NetworkUtils.hasNetwork()) {
                loginView.showToast(R.string.offline_mode_unsupported_in_first_login,
                        ToastUtil.ToastType.ERROR);
                loginView.hideLoadingAnimation();
            } else {
                loginView.showToast(R.string.no_internet_conn_dialog_message,
                        ToastUtil.ToastType.ERROR);
                loginView.hideLoadingAnimation();
            }
        }
    }


    @Override
    public void saveLocationsToDatabase(List<Location> locationList, String selectedLocation) {
        mOpenMRS.setLocation(selectedLocation);
        locationDAO.deleteAllLocations();
        for (int i = 0; i < locationList.size(); i++) {
            locationDAO.saveLocation(locationList.get(i))
                    .observeOn(Schedulers.io())
                    .subscribe();
        }
    }

    @Override
    public void loadLocations(final String url) {
        loginView.showLocationLoadingAnimation();

        if (NetworkUtils.hasNetwork()) {
            String locationEndPoint = url + ApplicationConstants.API.REST_ENDPOINT + "location";
            Call<Results<Location>> call =
                    restApi.getLocations(locationEndPoint, "Login Location", "full");
            call.enqueue(new Callback<Results<Location>>() {
                @Override
                public void onResponse(@NonNull Call<Results<Location>> call, @NonNull Response<Results<Location>> response) {
                    if (response.isSuccessful()) {
                        RestServiceBuilder.changeBaseUrl(url.trim());
                        mOpenMRS.setServerUrl(url);
                        loginView.initLoginForm(response.body().getResults(), url);
                        loginView.startFormListService();
                        loginView.setLocationErrorOccurred(false);
                    } else {
                        loginView.showInvalidURLSnackbar("Failed to fetch server's locations");
                        loginView.setLocationErrorOccurred(true);
                        loginView.initLoginForm(new ArrayList<>(), url);
                    }
                    loginView.hideUrlLoadingAnimation();
                }

                @Override
                public void onFailure(@NonNull Call<Results<Location>> call, @NonNull Throwable t) {
                    loginView.hideUrlLoadingAnimation();
                    loginView.showInvalidURLSnackbar(t.getMessage());
                    loginView.initLoginForm(new ArrayList<>(), url);
                    loginView.setLocationErrorOccurred(true);
                }
            });
        } else {
            addSubscription(locationDAO.getLocations()
                     .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(locations -> {
                        if (locations.size() > 0) {
                            loginView.initLoginForm(locations, url);
                            loginView.setLocationErrorOccurred(false);
                        } else {
                            loginView.showToast("Network not available.", ToastUtil.ToastType.ERROR);
                            loginView.setLocationErrorOccurred(true);
                        }
                        loginView.hideLoadingAnimation();
                    }));
        }

    }

    private boolean validateLoginFields(String username, String password, String url) {
        return StringUtils.notEmpty(username) || StringUtils.notEmpty(password) || StringUtils.notEmpty(url);
    }

    private void setData(String sessionToken,String url, String username, String password) {
        mOpenMRS.setSessionToken(sessionToken);
        mOpenMRS.setServerUrl(url);
        mOpenMRS.setUsername(username);
        mOpenMRS.setPasswordAndHashedPassword(password);
    }

    private void setLogin(boolean isLogin, String serverUrl) {
        mOpenMRS.setUserLoggedOnline(isLogin);
        mOpenMRS.setLastLoginServerUrl(serverUrl);
    }
}
