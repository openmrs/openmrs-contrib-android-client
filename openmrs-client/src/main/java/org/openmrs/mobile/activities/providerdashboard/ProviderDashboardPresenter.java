package org.openmrs.mobile.activities.providerdashboard;

import android.content.Intent;

import androidx.annotation.NonNull;

import com.openmrs.android_sdk.library.models.Provider;
import com.openmrs.android_sdk.utilities.ApplicationConstants;
import com.openmrs.android_sdk.utilities.ToastUtil;

import org.openmrs.mobile.activities.BasePresenter;
import com.openmrs.android_sdk.library.api.RestApi;
import com.openmrs.android_sdk.library.api.RestServiceBuilder;
import com.openmrs.android_sdk.library.api.repository.ProviderRepository;
import com.openmrs.android_sdk.library.listeners.retrofitcallbacks.DefaultResponseCallback;

public class ProviderDashboardPresenter extends BasePresenter implements ProviderDashboardContract.Presenter, DefaultResponseCallback {
    private RestApi restApi;
    private ProviderRepository providerRepository;
    private Provider provider;
    @NonNull
    private ProviderDashboardContract.View providerDashboardView;

    public ProviderDashboardPresenter(@NonNull ProviderDashboardContract.View view) {
        this.providerDashboardView = view;
        this.providerDashboardView.setPresenter(this);
        this.restApi = RestServiceBuilder.createService(RestApi.class);
        providerRepository = new ProviderRepository();
    }

    public ProviderDashboardPresenter(@NonNull ProviderDashboardContract.View view, RestApi restApi, ProviderRepository providerRepository) {
        this.providerDashboardView = view;
        this.providerDashboardView.setPresenter(this);
        this.restApi = restApi;
        this.providerRepository = providerRepository;
    }

    @Override
    public Provider getProviderFromIntent(Intent intent) {
        return (Provider) (intent.getSerializableExtra(ApplicationConstants.BundleKeys.PROVIDER_ID_BUNDLE));
    }

    @Override
    public void updateProvider(Provider provider) {
        providerRepository.updateProvider(provider, new DefaultResponseCallback() {
            @Override
            public void onResponse() {
                providerDashboardView.setupBackdrop(provider);
            }

            @Override
            public void onErrorResponse(String errorMessage) {
                ToastUtil.error(errorMessage);
                providerDashboardView.showSnackbarForFailedEditRequest();
            }
        });
    }

    @Override
    public void deleteProvider() {
        providerRepository.deleteProviders(provider.getUuid(), this);
    }

    @Override
    public void subscribe() {

    }

    /**
     * no need of refreshUI in this onSuccess() callback
     * since onUpdate() will rebuild this dashboard again
     * and onDelete this dashboard will be closed as activity finishes
     */
    @Override
    public void onResponse() {

    }

    @Override
    public void onErrorResponse(String errorMessage) {
        ToastUtil.error(errorMessage);
    }

    public Provider getProvider() {
        return provider;
    }

    public void setProvider(Provider provider) {
        this.provider = provider;
    }
}
