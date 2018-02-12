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

package org.openmrs.mobile.test.presenters;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.openmrs.mobile.activities.login.LoginContract;
import org.openmrs.mobile.activities.login.LoginPresenter;
import org.openmrs.mobile.api.RestApi;
import org.openmrs.mobile.api.RestServiceBuilder;
import org.openmrs.mobile.api.UserService;
import org.openmrs.mobile.api.retrofit.VisitApi;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.application.OpenMRSLogger;
import org.openmrs.mobile.dao.EncounterDAO;
import org.openmrs.mobile.dao.LocationDAO;
import org.openmrs.mobile.dao.VisitDAO;
import org.openmrs.mobile.models.Location;
import org.openmrs.mobile.models.Session;
import org.openmrs.mobile.models.User;
import org.openmrs.mobile.models.VisitType;
import org.openmrs.mobile.net.AuthorizationManager;
import org.openmrs.mobile.test.ACUnitTestBaseRx;
import org.openmrs.mobile.utilities.NetworkUtils;
import org.openmrs.mobile.utilities.StringUtils;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;

import java.util.ArrayList;
import java.util.Collections;

import rx.Observable;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@PrepareForTest({OpenMRS.class, NetworkUtils.class, LocationDAO.class, RestServiceBuilder.class,
        StringUtils.class})
@PowerMockIgnore("javax.net.ssl.*")
public class LoginPresenterTest extends ACUnitTestBaseRx {

    @Mock
    private OpenMRS openMRS;
    @Mock
    private OpenMRSLogger openMRSLogger;
    @Mock
    private AuthorizationManager authorizationManager;
    @Mock
    private RestApi restApi;
    @Mock
    private LoginContract.View view;
    @Mock
    private LocationDAO locationDAO;
    @Mock
    private VisitDAO visitDAO;
    @Mock
    private UserService userService;


    private LoginPresenter presenter;

    @Before
    public void setUp(){
        super.setUp();
        VisitApi visitApi = new VisitApi(restApi, visitDAO, locationDAO, new EncounterDAO());
        presenter = new LoginPresenter(restApi, visitApi, locationDAO, userService, view, openMRS,
                openMRSLogger, authorizationManager);
        mockStaticMethods();
    }

    @Test
    public void shouldNotLoginUser_emptyCredentials(){
        mockNonEmptyCredentials(false);
        presenter.login("", "", "some_url", "some_old_url");
        verify(view, never()).showWarningDialog();
        verify(view, never()).showLoadingAnimation();
    }

    @Test
    public void shouldShowWipingDBWarningDialog_newUsernameAndUrl(){
        mockNonEmptyCredentials(true);
        mockLastUser("newUser", "pass", "newUrl");
        presenter.login("oldUsername", "pass", "some_url", "some_old_url");
        verify(view).hideSoftKeys();
        verify(view).showWarningDialog();
    }

    @Test
    public void shouldLoginUserInOnlineMode_noWipe_userAuthenticated(){
        mockNonEmptyCredentials(true);
        mockOnlineMode(true);
        when(restApi.getSession())
                .thenReturn(mockSuccessCall(new Session("someId", true, new User())));
        when(restApi.getVisitType())
                .thenReturn(mockSuccessCall(Collections.singletonList(new VisitType("visitType"))));
        when(authorizationManager.isUserNameOrServerEmpty()).thenReturn(false);
        String user = "user";
        String url = "url";
        String password = "pass";
        mockLastUser(user, password, url);
        presenter.login(user, password, url, url);

        verify(view).showLoadingAnimation();
        verify(view).userAuthenticated();
        verify(view).finishLoginActivity();
    }

    @Test
    public void shouldLoginUserInOnlineMode_noWipe_userNotAuthenticated(){
        mockNonEmptyCredentials(true);
        mockOnlineMode(true);
        when(restApi.getSession())
                .thenReturn(mockSuccessCall(new Session("someId", false, new User())));
        when(restApi.getVisitType())
                .thenReturn(mockSuccessCall(Collections.singletonList(new VisitType("visitType"))));
        when(authorizationManager.isUserNameOrServerEmpty()).thenReturn(false);
        String user = "user";
        String url = "url";
        String password = "pass";
        mockLastUser(user, password, url);
        presenter.login(user, password, url, url);

        verify(view).showLoadingAnimation();
        verify(view).showInvalidLoginOrPasswordSnackbar();
    }

    @Test
    public void shouldLoginUserInOnlineMode_errorResponse(){
        mockNonEmptyCredentials(true);
        mockOnlineMode(true);
        when(restApi.getSession()).thenReturn(mockErrorCall(401));
        String user = "user";
        String url = "url";
        String password = "pass";
        mockLastUser(user, password, url);
        presenter.login(user, password, url, url);

        verify(view).hideLoadingAnimation();
        verify(view).showToast(anyString(), any());
    }

    @Test
    public void shouldLoginUserInOnlineMode_failure(){
        mockNonEmptyCredentials(true);
        mockOnlineMode(true);
        when(restApi.getSession()).thenReturn(mockFailureCall());
        String user = "user";
        String url = "url";
        String password = "pass";
        mockLastUser(user, password, url);
        presenter.login(user, password, url, url);

        verify(view).hideLoadingAnimation();
        verify(view).showToast(anyString(), any());
    }

    @Test
    public void shouldLoginUserInOfflineMode_userLoggedBefore_sameUrl(){
        String user = "user";
        String url = "url";
        String password = "pass";
        mockLastUser(user, password, url);
        mockNonEmptyCredentials(true);
        mockOnlineMode(false);
        when(openMRS.isUserLoggedOnline()).thenReturn(true);
        when(openMRS.getLastLoginServerUrl()).thenReturn(url);
        presenter.login(user, password, url, url);

        verify(view).showToast(anyInt(), any());
        verify(view).userAuthenticated();
        verify(view).finishLoginActivity();
    }

    @Test
    public void shouldLoginUserInOfflineMode_userLoggedBefore_wrongCredentials(){
        String user = "user";
        String url = "url";
        String password = "pass";
        mockLastUser(user, "newPass", url);
        mockNonEmptyCredentials(true);
        mockOnlineMode(false);
        when(openMRS.isUserLoggedOnline()).thenReturn(true);
        when(openMRS.getLastLoginServerUrl()).thenReturn(url);
        presenter.login(user, password, url, url);

        verify(view).showToast(anyInt(), any());
        verify(view).hideLoadingAnimation();
    }

    @Test
    public void shouldLoadLocationsInOnlineMode_allOK(){
        mockNetworkConnection(true);
        when(restApi.getLocations(any(), anyString(), anyString()))
                .thenReturn(mockSuccessCall(Collections.singletonList(new Location())));
        presenter.loadLocations("someUrl");
        verify(view).initLoginForm(any(), any());
        verify(view).startFormListService();
        verify(view).setLocationErrorOccurred(false);
        verify(view).hideUrlLoadingAnimation();
    }

    @Test
    public void shouldLoadLocationsInOnlineMode_errorResponse(){
        mockNetworkConnection(true);
        when(restApi.getLocations(any(), anyString(), anyString()))
                .thenReturn(mockErrorCall(401));
        presenter.loadLocations("someUrl");
        verify(view).initLoginForm(any(), any());
        verify(view).setLocationErrorOccurred(true);
        verify(view).showInvalidURLSnackbar(anyString());
        verify(view).hideUrlLoadingAnimation();
    }

    @Test
    public void shouldLoadLocationsInOnlineMode_failure(){
        mockNetworkConnection(true);
        when(restApi.getLocations(any(), anyString(), anyString()))
                .thenReturn(mockFailureCall());
        presenter.loadLocations("someUrl");
        verify(view).initLoginForm(any(), any());
        verify(view).setLocationErrorOccurred(true);
        verify(view).showInvalidURLSnackbar(anyString());
        verify(view).hideUrlLoadingAnimation();
    }

    @Test
    public void shouldLoadLocationsInOfflineMode_emptyList(){
        mockNetworkConnection(false);
        when(locationDAO.getLocations()).thenReturn(Observable.just(new ArrayList<>()));
        presenter.loadLocations("someUrl");
        verify(view).showToast(anyString(), any());
        verify(view).setLocationErrorOccurred(true);
        verify(view).hideLoadingAnimation();
    }

    @Test
    public void shouldLoadLocationsInOfflineMode_nonEmptyList(){
        mockNetworkConnection(false);
        when(locationDAO.getLocations()).thenReturn(Observable.just(Collections.singletonList(new Location())));
        presenter.loadLocations("someUrl");
        verify(view).initLoginForm(any(), any());
        verify(view).setLocationErrorOccurred(false);
        verify(view).hideLoadingAnimation();
    }

    private void mockNetworkConnection(boolean isNetwork) {
        PowerMockito.when(NetworkUtils.hasNetwork()).thenReturn(isNetwork);
    }


    private void mockNonEmptyCredentials(boolean isNonEmpty) {
        PowerMockito.when(StringUtils.notEmpty(anyString())).thenReturn(isNonEmpty);
    }


    private void mockOnlineMode(boolean isOnline) {
        PowerMockito.when(NetworkUtils.isOnline()).thenReturn(isOnline);
    }

    private void mockLastUser(String user, String password, String url) {
        when(openMRS.getUsername()).thenReturn(user);
        when(openMRS.getServerUrl()).thenReturn(url);
        when(openMRS.getPassword()).thenReturn(password);
    }

    private void mockStaticMethods() {
        PowerMockito.mockStatic(OpenMRS.class);
        PowerMockito.mockStatic(LocationDAO.class);
        PowerMockito.mockStatic(StringUtils.class);
        PowerMockito.mockStatic(NetworkUtils.class);
        when(openMRS.getServerUrl()).thenReturn("http://www.some_server_url.com");
        PowerMockito.when(OpenMRS.getInstance()).thenReturn(openMRS);
        PowerMockito.mockStatic(RestServiceBuilder.class);
        PowerMockito.when(RestServiceBuilder.createService(any(), any(), any())).thenReturn(restApi);
    }

}
