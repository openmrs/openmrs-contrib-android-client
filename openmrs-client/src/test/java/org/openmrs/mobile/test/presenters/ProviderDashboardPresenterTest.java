package org.openmrs.mobile.test.presenters;


import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.openmrs.mobile.activities.providerdashboard.ProviderDashboardContract;
import org.openmrs.mobile.activities.providerdashboard.ProviderDashboardPresenter;
import org.openmrs.mobile.api.RestApi;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.application.OpenMRSLogger;
import org.openmrs.mobile.models.Provider;
import org.openmrs.mobile.test.ACUnitTestBase;
import org.openmrs.mobile.utilities.NetworkUtils;
import org.openmrs.mobile.utilities.ToastUtil;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;

@PrepareForTest({NetworkUtils.class, ToastUtil.class, OpenMRS.class, OpenMRSLogger.class})
public class ProviderDashboardPresenterTest extends ACUnitTestBase {
    @Rule
    public InstantTaskExecutorRule taskExecutorRule = new InstantTaskExecutorRule();

    @Mock
    private RestApi restApi;
    @Mock
    private ProviderDashboardContract.View providerManagerView;
    @Mock
    private OpenMRSLogger openMRSLogger;
    @Mock
    private OpenMRS openMRS;

    private ProviderDashboardPresenter providerDashboardPresenter;

    @Before
    public void setup() {
        this.providerDashboardPresenter = new ProviderDashboardPresenter(providerManagerView, restApi);
        mockStaticMethods();
    }

    @Test
    public void shouldReturnSuccessOnEditProvider() {
        Provider provider = createProvider(1l, "doctor");
        Mockito.lenient().when(NetworkUtils.isOnline()).thenReturn(true);
        Mockito.lenient().when(restApi.editProvider(provider.getUuid(), provider)).thenReturn(mockSuccessCall(provider));

        providerDashboardPresenter.editProvider(provider);
        Mockito.verify(providerManagerView).setupBackdrop(provider);
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
