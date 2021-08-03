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

import android.content.Context;
import android.content.res.Resources;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.openmrs.android_sdk.library.OpenMRSLogger;
import com.openmrs.android_sdk.library.OpenmrsAndroid;
import com.openmrs.android_sdk.library.dao.ProviderRoomDAO;
import com.openmrs.android_sdk.library.databases.entities.LocationEntity;
import com.openmrs.android_sdk.library.models.Provider;
import com.openmrs.android_sdk.library.models.Resource;
import com.openmrs.android_sdk.utilities.NetworkUtils;
import com.openmrs.android_sdk.utilities.ToastUtil;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.openmrs.mobile.activities.formadmission.FormAdmissionContract;
import org.openmrs.mobile.activities.formadmission.FormAdmissionFragment;
import org.openmrs.mobile.activities.formadmission.FormAdmissionPresenter;
import com.openmrs.android_sdk.library.api.RestApi;
import com.openmrs.android_sdk.library.api.repository.ProviderRepository;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.test.ACUnitTestBase;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.Single;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.RETURNS_MOCKS;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({NetworkUtils.class, ToastUtil.class, OpenMRS.class, OpenMRSLogger.class,OpenmrsAndroid.class})
public class FormAdmissionPresenterTest extends ACUnitTestBase {
    @Rule
    public InstantTaskExecutorRule taskExecutorRule = new InstantTaskExecutorRule();
    MutableLiveData<List<Provider>> providerLiveData = Mockito.mock(MutableLiveData.class);
    List<Provider> providerList;
    List<Resource> resourceList;
    List<LocationEntity> locationList;
    Provider providerOne = createProvider(1l, "doctor");
    Provider providerTwo = createProvider(2l, "nurse");
    LocationEntity locationEntityOne = new LocationEntity("entity 1");
    LocationEntity locationEntityTwo = new LocationEntity("entity 2");
    Resource resourceOne = new Resource("uuid", "display", new ArrayList<>(), 1L);
    Resource resourceTwo = new Resource("uuid 2", "display 2", new ArrayList<>(), 2L);
    @Mock
    private RestApi restApi;
    @Mock
    private FormAdmissionContract.View formAdmissionView;
    @Mock
    private Observer<List<Provider>> observer;
    @Mock
    private Observer<List<LocationEntity>> locationObserver;
    @Mock
    private Observer<List<Resource>> resourceObserver;
    @Mock
    private OpenMRSLogger openMRSLogger;
    @Mock
    private OpenMRS openMRS;
    @Mock
    private Context context;
    @Mock
    private Resources resources;
    private FormAdmissionPresenter formAdmissionPresenter;
    private ProviderRepository providerRepository;
    private FormAdmissionFragment fragment = new FormAdmissionFragment();

    @Before
    public void setUp() {
        mockStaticMethods();
        providerList = Arrays.asList(providerOne, providerTwo);
        locationList = Arrays.asList(locationEntityOne, locationEntityTwo);
        resourceList = Arrays.asList(resourceOne, resourceTwo);
        providerLiveData.postValue(providerList);

        this.providerRepository = new ProviderRepository(restApi, openMRSLogger);
        ProviderRoomDAO providerRoomDao = Mockito.mock(ProviderRoomDAO.class, RETURNS_MOCKS);
        ProviderRoomDAO spyProviderRoomDao = spy(providerRoomDao);

        Single listSingle = Mockito.mock(Single.class);
        doNothing().when(spyProviderRoomDao).updateProviderByUuid(Mockito.anyString(), Mockito.anyLong(), Mockito.any(), Mockito.anyString(), Mockito.anyString());
        when(spyProviderRoomDao.getProviderList()).thenReturn(listSingle);
        when(listSingle.blockingGet()).thenReturn(providerList);

        this.providerRepository.setProviderRoomDao(spyProviderRoomDao);

        formAdmissionPresenter = new FormAdmissionPresenter(formAdmissionView, restApi, context, openMRSLogger);
    }

    @Test
    public void shouldGetProviders_AllOK() {
        Mockito.lenient().when(NetworkUtils.isOnline()).thenReturn(true);
        Mockito.lenient().when(restApi.getProviderList()).thenReturn(mockSuccessCall(providerList));

        formAdmissionPresenter.updateViews(providerList);
        providerRepository.getProviders().observeForever(observer);

        verify(restApi).getProviderList();
        verify(formAdmissionView).updateProviderAdapter(providerList);
    }

    @Test
    public void shouldGetProviders_Error() {
        Mockito.lenient().when(NetworkUtils.isOnline()).thenReturn(true);
        Mockito.lenient().when(restApi.getProviderList()).thenReturn(mockErrorCall(401));

        providerRepository.getProviders().observeForever(observer);
        verify(restApi).getProviderList();
    }

    @Test
    public void shouldGetAdmissionLocation_AllOK() {
        Mockito.lenient().when(NetworkUtils.hasNetwork()).thenReturn(true);
        Mockito.lenient().when(restApi.getLocations(any(), anyString(), anyString()))
            .thenReturn(mockSuccessCall(locationList));
        formAdmissionPresenter.updateLocationList(locationList);
        providerRepository.getLocation("some url").observeForever(locationObserver);
        verify(formAdmissionView).updateLocationAdapter(locationList);
    }

    @Test
    public void shouldLoadLocations_errorResponse() {
        Mockito.lenient().when(NetworkUtils.hasNetwork()).thenReturn(true);
        Mockito.lenient().when(restApi.getLocations(any(), anyString(), anyString()))
            .thenReturn(mockErrorCall(401));

        Resources res = Mockito.mock(context.getResources().getClass());
        PowerMockito.when(context.getResources()).thenReturn(res);
        Mockito.when(res.getString(Mockito.anyInt())).thenReturn("error_message");

        formAdmissionPresenter.updateLocationList(null);
        providerRepository.getLocation("some url").observeForever(locationObserver);
        verify(formAdmissionView).showToast(anyString());
        verify(formAdmissionView).enableSubmitButton(false);
    }

    @Test
    public void shouldGetEncounterRoles_AllOK() {
        Mockito.lenient().when(NetworkUtils.hasNetwork()).thenReturn(true);
        Mockito.lenient().when(restApi.getEncounterRoles())
            .thenReturn(mockSuccessCall(resourceList));
        formAdmissionPresenter.updateEncounterRoles(resourceList);
        providerRepository.getEncounterRoles().observeForever(resourceObserver);
        verify(formAdmissionView).updateEncounterRoleList(resourceList);
    }

    @Test
    public void shouldLoadEncounterRoles_errorResponse() {
        Mockito.lenient().when(NetworkUtils.hasNetwork()).thenReturn(true);
        Mockito.lenient().when(restApi.getEncounterRoles())
            .thenReturn(mockErrorCall(401));

        Resources res = Mockito.mock(context.getResources().getClass());
        PowerMockito.when(context.getResources()).thenReturn(res);
        Mockito.when(res.getString(Mockito.anyInt())).thenReturn("error_message");

        formAdmissionPresenter.updateEncounterRoles(null);
        providerRepository.getEncounterRoles().observeForever(resourceObserver);
        verify(formAdmissionView).showToast(anyString());
        verify(formAdmissionView).enableSubmitButton(false);
    }

    private void mockStaticMethods() {
        PowerMockito.mockStatic(NetworkUtils.class);
        PowerMockito.mockStatic(OpenMRS.class);
        PowerMockito.mockStatic(OpenMRSLogger.class);
        PowerMockito.mockStatic(OpenmrsAndroid.class);
        Mockito.lenient().when(OpenMRS.getInstance()).thenReturn(openMRS);
        PowerMockito.mockStatic(ToastUtil.class);
        PowerMockito.when(OpenmrsAndroid.getOpenMRSLogger()).thenReturn(openMRSLogger);
        PowerMockito.when(context.getApplicationContext()).thenReturn(context);
        PowerMockito.when(context.getResources()).thenReturn(resources);
    }
}
