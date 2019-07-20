package org.openmrs.mobile.activities.providerdashboard;

import android.content.Intent;

import androidx.annotation.NonNull;

import org.openmrs.mobile.activities.BasePresenter;
import org.openmrs.mobile.api.CustomApiCallback;
import org.openmrs.mobile.api.RestApi;
import org.openmrs.mobile.api.RestServiceBuilder;
import org.openmrs.mobile.api.retrofit.ProviderRepository;
import org.openmrs.mobile.models.Provider;
import org.openmrs.mobile.utilities.ApplicationConstants;

public class ProviderDashboardPresenter extends BasePresenter implements ProviderDashboardContract.Presenter {
    private RestApi restApi;

    @NonNull
    private ProviderDashboardContract.View providerDashboardView;

    public ProviderDashboardPresenter(@NonNull ProviderDashboardContract.View view){
        this.providerDashboardView = view;
        this.providerDashboardView.setPresenter(this);
        this.restApi = RestServiceBuilder.createService(RestApi.class);
    }

    public ProviderDashboardPresenter(@NonNull ProviderDashboardContract.View view, RestApi restApi){
        this.providerDashboardView = view;
        this.providerDashboardView.setPresenter(this);
        this.restApi = restApi;
    }

    @Override
    public Provider getProviderFromIntent(Intent intent) {
        Provider provider = (Provider) (intent.getSerializableExtra(ApplicationConstants.BundleKeys.PROVIDER_ID_BUNDLE));
        return provider;
    }

    @Override
    public void editProvider(Provider provider) {
        ProviderRepository providerRepository = new ProviderRepository();
        providerRepository.editProvider(restApi, provider, new CustomApiCallback() {
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
    public void subscribe() {

    }
}
