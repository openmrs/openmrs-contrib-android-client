package org.openmrs.mobile.activities.providermanager;

import org.openmrs.mobile.activities.BasePresenterContract;
import org.openmrs.mobile.activities.BaseView;
import org.openmrs.mobile.models.Provider;

import java.util.List;

public interface ProviderManagerContract {

    interface View extends BaseView<ProviderManagerContract.Presenter> {

        void updateAdapter(List<Provider> providerList);

        void updateVisibility(boolean visibility, String text);
    }

    interface Presenter extends BasePresenterContract {
        void getProviders();
    }
}
