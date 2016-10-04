package org.openmrs.mobile.activities.login;

import android.content.Intent;
import android.support.annotation.NonNull;

import org.openmrs.mobile.activities.BasePresenter;
import org.openmrs.mobile.activities.BaseView;
import org.openmrs.mobile.models.Location;
import org.openmrs.mobile.utilities.ToastUtil;

import java.util.List;

public interface LoginContract {

    interface View extends BaseView<Presenter>{

        boolean isActive();

        void setPresenter(@NonNull Presenter presenter);

        void showWarningDialog();

        void showToast(ToastUtil.ToastType toastType, int message);

        void showLoadingAnimation();

        void finishLoginActivity();

        void setErrorOccurred(boolean errorOccurred);

        void sendBroadcast(Intent intent);

        void initLoginForm(List<Location> locationList, String url);

        void userAuthenticated();
    }

    interface  Presenter extends BasePresenter{

        void authenticateUser(final String username, final String password, final String url);

        void login(String username, String password, String url);

        void saveLocationsToDatabase(List<Location> locationList, String selectedLocation);

        void loadLocations(String url);
    }
}
