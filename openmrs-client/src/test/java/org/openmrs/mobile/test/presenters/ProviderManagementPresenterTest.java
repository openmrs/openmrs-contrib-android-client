package org.openmrs.mobile.test.presenters;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import org.openmrs.mobile.activities.providermanager.ProviderManagementPresenter;
import org.openmrs.mobile.activities.providermanager.ProviderManagerContract;
import org.openmrs.mobile.api.RestApi;
import org.openmrs.mobile.api.retrofit.ProviderRepository;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.application.OpenMRSLogger;
import org.openmrs.mobile.models.Provider;
import org.openmrs.mobile.test.ACUnitTestBase;
import org.openmrs.mobile.utilities.NetworkUtils;
import org.openmrs.mobile.utilities.ToastUtil;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.verify;

@PrepareForTest({NetworkUtils.class, ToastUtil.class, OpenMRS.class, OpenMRSLogger.class})
public class ProviderManagementPresenterTest extends ACUnitTestBase {

    @Rule
    public InstantTaskExecutorRule taskExecutorRule = new InstantTaskExecutorRule();

    @Mock
    private RestApi restApi;
    @Mock
    private ProviderRepository providerRepository;
    @Mock
    private ProviderManagerContract.View providerManagerView;
    @Mock
    private Observer<List<Provider>> observer;
    @Mock
    private OpenMRSLogger openMRSLogger;
    @Mock
    private OpenMRS openMRS;

    MutableLiveData<List<Provider>> providerLiveData = Mockito.mock(MutableLiveData.class);

    private ProviderManagementPresenter providerManagementPresenter;
    private Fragment fragment = new Fragment();
    List<Provider> providerList;

    @Before
    public void setUp(){
        providerManagementPresenter = new ProviderManagementPresenter(providerManagerView,restApi);
        mockStaticMethods();
    }

    @Test
    public void shouldGetProviders_AllOK(){
        Provider providerOne = createProvider(1l,"doctor");
        Provider providerTwo = createProvider(2l,"nurse");
        providerList  = Arrays.asList(providerOne,providerTwo);
        providerLiveData.postValue(providerList);

        Mockito.lenient().when(NetworkUtils.isOnline()).thenReturn(true);
        Mockito.lenient().when(restApi.getProviderList()).thenReturn(mockSuccessCall(providerList));
        Mockito.lenient().when(providerRepository.getProviders(restApi)).thenReturn(providerLiveData);

        providerRepository.getProviders(restApi).observeForever(observer);
        providerManagementPresenter.getProviders(fragment);
        providerManagementPresenter.updateViews(providerList);

        verify(restApi).getProviderList();
        verify(providerManagerView).updateAdapter(providerList);
        verify(providerManagerView).updateVisibility(true,null);
    }

    @Test
    public void shouldGetProviders_Error(){
        Mockito.lenient().when(NetworkUtils.isOnline()).thenReturn(true);
        Mockito.lenient().when(restApi.getProviderList()).thenReturn(mockErrorCall(401));

        providerManagementPresenter.getProviders(fragment);
        verify(restApi).getProviderList();
    }

    @Test
    public void shouldGetProviders_EmptyList(){
        List<Provider> providerList = new ArrayList<>();
        providerLiveData.postValue(providerList);

        Mockito.lenient().when(NetworkUtils.isOnline()).thenReturn(true);
        Mockito.lenient().when(restApi.getProviderList()).thenReturn(mockSuccessCall(providerList));
        Mockito.lenient().when(providerRepository.getProviders(restApi)).thenReturn(providerLiveData);

        providerRepository.getProviders(restApi).observeForever(observer);
        providerManagementPresenter.getProviders(fragment);
        providerManagementPresenter.updateViews(providerList);
        verify(restApi).getProviderList();
        verify(providerManagerView).updateVisibility(false,"No Data to display.");
    }

    private void mockStaticMethods() {
        PowerMockito.mockStatic(NetworkUtils.class);
        PowerMockito.mockStatic(OpenMRS.class);
        PowerMockito.mockStatic(OpenMRSLogger.class);
        Mockito.lenient().when(OpenMRS.getInstance()).thenReturn(openMRS);
        PowerMockito.when(openMRS.getOpenMRSLogger()).thenReturn(openMRSLogger);
        PowerMockito.mockStatic(ToastUtil.class);
    }
}
