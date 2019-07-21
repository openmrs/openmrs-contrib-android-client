package org.openmrs.mobile.activities.providerdashboard;

import android.content.Intent;

import org.openmrs.mobile.activities.BasePresenterContract;
import org.openmrs.mobile.activities.BaseView;
import org.openmrs.mobile.models.Provider;

public class ProviderDashboardContract {
    public interface View extends BaseView<Presenter> {

        void setupBackdrop(Provider provider);

        void showSnackbarForFailedEditRequest();
    }

    public interface Presenter extends BasePresenterContract {

        void editProvider(Provider provider);

        Provider getProviderFromIntent(Intent intent);
    }
}
