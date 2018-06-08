package org.openmrs.mobile.activities.providermanager;

import android.support.annotation.NonNull;

import org.openmrs.mobile.activities.BasePresenterContract;
import org.openmrs.mobile.activities.BaseView;
import org.openmrs.mobile.activities.syncedpatients.SyncedPatientsContract;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.models.Provider;

import java.util.List;

/**
 * Created by Chathuranga on 16/05/2018.
 */

public interface ProviderManagerContract {

    interface View extends BaseView<ProviderManagerContract.Presenter> {

        void updateAdapter(List<Provider> providerList);
        void updateVisibility(boolean visibility, String text);
    }

    interface Presenter extends BasePresenterContract {
        void getProviders();
    }
}
