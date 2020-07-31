package org.openmrs.mobile.activities.providerdashboard;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

import org.openmrs.mobile.activities.BasePresenter;
import org.openmrs.mobile.listeners.retrofit.CustomApiCallback;
import org.openmrs.mobile.api.RestApi;
import org.openmrs.mobile.api.RestServiceBuilder;
import org.openmrs.mobile.api.repository.ProviderRepository;
import org.openmrs.mobile.models.Provider;
import org.openmrs.mobile.utilities.ApplicationConstants;

public class ProviderDashboardPresenter extends BasePresenter implements ProviderDashboardContract.Presenter, CustomApiCallback {
    private RestApi restApi;
    private ProviderRepository providerRepository;
    private Provider provider;
    @NonNull
    private ProviderDashboardContract.View providerDashboardView;

    public ProviderDashboardPresenter(@NonNull ProviderDashboardContract.View view, Context context) {
        this.providerDashboardView = view;
        this.providerDashboardView.setPresenter(this);
        this.restApi = RestServiceBuilder.createService(RestApi.class);
        providerRepository = new ProviderRepository(context);
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
        providerRepository.updateProvider(restApi, provider, new CustomApiCallback() {
            @Override
            public void onSuccess() {
                providerDashboardView.setupBackdrop(provider);
            }

            @Override
            public void onFailure() {
                providerDashboardView.showSnackbarForFailedEditRequest();
            }
        });
    }

    @Override
    public void deleteProvider() {
        providerRepository.deleteProviders(restApi, provider.getUuid(), this);
    }

    @Override
    public void subscribe() {

    }

    @Override
    public void onFailure() {

    }

    /**
     * no need of refreshUI in this onSuccess() callback
     * since onUpdate() will rebuild this dashboard again
     * and onDelete this dashboard will be closed as activity finishes
     */
    @Override
    public void onSuccess() {

    }

    public Provider getProvider() {
        return provider;
    }

    public void setProvider(Provider provider) {
        this.provider = provider;
    }
}
