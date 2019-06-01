package org.openmrs.mobile.test.presenters;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.openmrs.mobile.activities.providermanager.ProviderManagementPresenter;
import org.openmrs.mobile.activities.providermanager.ProviderManagerContract;
import org.openmrs.mobile.api.RestApi;
import org.openmrs.mobile.dao.ProviderDAO;
import org.openmrs.mobile.models.Provider;
import org.openmrs.mobile.test.ACUnitTestBase;
import org.openmrs.mobile.utilities.NetworkUtils;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import rx.Observable;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@PrepareForTest({NetworkUtils.class})
public class ProviderManagementPresenterTest extends ACUnitTestBase {

    @Mock
    private RestApi restApi;
    @Mock
    private ProviderDAO providerDAO;
    @Mock
    private ProviderManagerContract.View providerManagerView;

    private ProviderManagementPresenter providerManagementPresenter;
    private Provider providerOne, providerTwo;

    @Before
    public void setUp(){
        providerManagementPresenter = new ProviderManagementPresenter(providerManagerView,restApi,providerDAO);
        providerOne = createProvider(1l,"doctor");
        providerTwo = createProvider(2l,"nurse");
        mockStaticMethods();
    }

    @Test
    public void shouldGetProviders_AllOK(){
        when(NetworkUtils.hasNetwork()).thenReturn(true);
        List<Provider> providerList = Arrays.asList(providerOne,providerTwo);
        when(restApi.getProviderList()).thenReturn(mockSuccessCall(providerList));
        when(providerDAO.saveProvider(providerOne)).thenReturn(Observable.just(1L));
        when(providerDAO.saveProvider(providerTwo)).thenReturn(Observable.just(2L));
        providerManagementPresenter.getProviders();
        verify(restApi).getProviderList();
        verify(providerManagerView).updateAdapter(providerList);
        verify(providerManagerView).updateVisibility(true,null);
    }

    @Test
    public void shouldGetProviders_Error(){
        when(NetworkUtils.hasNetwork()).thenReturn(true);
        when(restApi.getProviderList()).thenReturn(mockErrorCall(401));
        providerManagementPresenter.getProviders();
        verify(restApi).getProviderList();
    }

    @Test
    public void shouldGetProviders_EmptyList(){
        when(NetworkUtils.hasNetwork()).thenReturn(true);
        List<Provider> providerList = new ArrayList<>();
        when(restApi.getProviderList()).thenReturn(mockSuccessCall(providerList));
        providerManagementPresenter.getProviders();
        verify(restApi).getProviderList();
        verify(providerManagerView).updateVisibility(false,"No Data to display.");
    }

    private void mockStaticMethods() {
        PowerMockito.mockStatic(NetworkUtils.class);
    }
}
