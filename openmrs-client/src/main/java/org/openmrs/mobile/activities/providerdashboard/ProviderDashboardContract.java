package org.openmrs.mobile.activities.providerdashboard;

import android.content.Intent;

import com.example.openmrs_android_sdk.library.models.Provider;

import org.openmrs.mobile.activities.BasePresenterContract;
import org.openmrs.mobile.activities.BaseView;

public interface ProviderDashboardContract {
    public interface View extends BaseView<Presenter> {
        void setupBackdrop(Provider provider);

        void showSnackbarForFailedEditRequest();
    }

    public interface Presenter extends BasePresenterContract {
        void updateProvider(Provider provider);

        Provider getProviderFromIntent(Intent intent);

        void deleteProvider();
    }
}
