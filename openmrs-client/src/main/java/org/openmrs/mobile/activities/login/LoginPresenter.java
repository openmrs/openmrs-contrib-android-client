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

import androidx.annotation.NonNull;

import com.openmrs.android_sdk.library.OpenMRSLogger;
import com.openmrs.android_sdk.library.OpenmrsAndroid;
import com.openmrs.android_sdk.library.dao.LocationDAO;
import com.openmrs.android_sdk.library.databases.entities.LocationEntity;
import com.openmrs.android_sdk.library.models.Results;
import com.openmrs.android_sdk.library.models.Session;
import com.openmrs.android_sdk.library.models.VisitType;
import com.openmrs.android_sdk.utilities.ApplicationConstants;
import com.openmrs.android_sdk.utilities.NetworkUtils;
import com.openmrs.android_sdk.utilities.StringUtils;
import com.openmrs.android_sdk.utilities.ToastUtil;

import org.mindrot.jbcrypt.BCrypt;
import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.BasePresenter;
import com.openmrs.android_sdk.library.api.RestApi;
import com.openmrs.android_sdk.library.api.RestServiceBuilder;
import org.openmrs.mobile.services.UserService;
import com.openmrs.android_sdk.library.api.repository.VisitRepository;
import org.openmrs.mobile.application.OpenMRS;
import com.openmrs.android_sdk.library.listeners.retrofitcallbacks.GetVisitTypeCallback;
import org.openmrs.mobile.net.AuthorizationManager;

import java.util.ArrayList;
import java.util.List;

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
        this.mLogger = OpenmrsAndroid.getOpenMRSLogger();
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
            if ((!OpenmrsAndroid.getUsername().equals(ApplicationConstants.EMPTY_STRING) &&
                    !OpenmrsAndroid.getUsername().equals(username)) ||
                    ((!OpenmrsAndroid.getServerUrl().equals(ApplicationConstants.EMPTY_STRING) &&
                            !OpenmrsAndroid.getServerUrl().equals(oldUrl))) ||
                    (!OpenmrsAndroid.getHashedPassword().equals(ApplicationConstants.EMPTY_STRING) &&
                            !BCrypt.checkpw(password, OpenmrsAndroid.getHashedPassword())) ||
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
                            OpenmrsAndroid.deleteSecretKey();
                            if (wipeDatabase) {
                                mOpenMRS.deleteDatabase(ApplicationConstants.DB_NAME);
                                setData(session.getSessionId(), url, username, password);
                                mWipeRequired = false;
                            }
                            if (authorizationManager.isUserNameOrServerEmpty()) {
                                setData(session.getSessionId(), url, username, password);
                            } else {
                                OpenmrsAndroid.setSessionToken(session.getSessionId());
                                OpenmrsAndroid.setPasswordAndHashedPassword(password);
                            }

                            visitRepository.getVisitType(new GetVisitTypeCallback() {
                                @Override
                                public void onGetVisitTypeResponse(VisitType visitType) {
                                    OpenmrsAndroid.setVisitTypeUUID(visitType.getUuid());
                                }

                                @Override
                                public void onResponse() {
                                    // This method is intentionally empty
                                }

                                @Override
                                public void onErrorResponse(String errorMessage) {

                                    OpenmrsAndroid.setVisitTypeUUID(ApplicationConstants.DEFAULT_VISIT_TYPE_UUID);
                                    loginView.showToast(R.string.failed_fetching_visit_type_error_message, ToastUtil.ToastType.ERROR);
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
            if (OpenmrsAndroid.isUserLoggedOnline() && url.equals(OpenmrsAndroid.getLastLoginServerUrl())) {
                if (OpenmrsAndroid.getUsername().equals(username) && BCrypt.checkpw(password, OpenmrsAndroid.getHashedPassword())) {
                    OpenmrsAndroid.deleteSecretKey();
                    OpenmrsAndroid.setPasswordAndHashedPassword(password);
                    OpenmrsAndroid.setSessionToken(OpenmrsAndroid.getLastSessionToken());
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
    public void saveLocationsToDatabase(List<LocationEntity> locationList, String selectedLocation) {
        OpenmrsAndroid.setLocation(selectedLocation);
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
            Call<Results<LocationEntity>> call =
                    restApi.getLocations(locationEndPoint, "Login Location", "full");
            call.enqueue(new Callback<Results<LocationEntity>>() {
                @Override
                public void onResponse(@NonNull Call<Results<LocationEntity>> call, @NonNull Response<Results<LocationEntity>> response) {
                    if (response.isSuccessful()) {
                        RestServiceBuilder.changeBaseUrl(url.trim());
                        OpenmrsAndroid.setServerUrl(url);
                        loginView.initLoginForm(response.body().getResults(), url);
                        loginView.startFormListService();
                        loginView.setLocationErrorOccurred(false);
                    } else {
                        loginView.showInvalidURLSnackbar(R.string.snackbar_server_error);
                        loginView.setLocationErrorOccurred(true);
                        loginView.initLoginForm(new ArrayList<>(), url);
                    }
                    loginView.hideUrlLoadingAnimation();
                }

                @Override
                public void onFailure(@NonNull Call<Results<LocationEntity>> call, @NonNull Throwable t) {
                    loginView.hideUrlLoadingAnimation();
                    loginView.showInvalidURLSnackbar(t.getMessage());
                    loginView.initLoginForm(new ArrayList<>(), url);
                    loginView.setLocationErrorOccurred(true);
                }
            });
        } else {
            addSubscription(locationDAO.getLocations()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(locationEntities -> {
                        if (locationEntities.size() > 0) {
                            loginView.initLoginForm(locationEntities, url);
                            loginView.setLocationErrorOccurred(false);
                        } else {
                            loginView.showToast(R.string.no_internet_connection_message, ToastUtil.ToastType.ERROR);
                            loginView.setLocationErrorOccurred(true);
                        }
                        loginView.hideLoadingAnimation();
                    }));
        }
    }

    private boolean validateLoginFields(String username, String password, String url) {
        return StringUtils.notEmpty(username) || StringUtils.notEmpty(password) || StringUtils.notEmpty(url);
    }

    // use this method to populate the Openmrs username password and everything else.

    private void setData(String sessionToken, String url, String username, String password) {
        OpenmrsAndroid.setSessionToken(sessionToken);
        OpenmrsAndroid.setServerUrl(url);
        OpenmrsAndroid.setUsername(username);
        OpenmrsAndroid.setPasswordAndHashedPassword(password);
    }

    private void setLogin(boolean isLogin, String serverUrl) {
        OpenmrsAndroid.setUserLoggedOnline(isLogin);
        OpenmrsAndroid.setLastLoginServerUrl(serverUrl);
    }
}
