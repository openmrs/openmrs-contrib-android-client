package org.openmrs.mobile.activities.contactus;

import androidx.annotation.NonNull;
import org.openmrs.mobile.activities.BasePresenter;

public class ContactUsPresenter extends BasePresenter implements ContactUsContract.Presenter  {
    @NonNull
    private final ContactUsContract.View mContactUsView;

    public ContactUsPresenter(@NonNull ContactUsContract.View view) {
        mContactUsView = view;
        view.setPresenter(this);
    }


    @Override
    public void subscribe() {
        // This method is intentionally empty
    }
}
