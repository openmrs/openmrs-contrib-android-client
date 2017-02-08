package org.openmrs.mobile.activities;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

public abstract class BasePresenter implements BasePresenterContract {

    private CompositeSubscription mSubscription;

    public BasePresenter() {
        mSubscription = new CompositeSubscription();
    }

    public void addSubscription(Subscription subscription) {
        if(mSubscription != null) {
            mSubscription.add(subscription);
        }
    }

    @Override
    public void unsubscribe() {
        if(mSubscription != null) {
            mSubscription.clear();
        }
    }
}
