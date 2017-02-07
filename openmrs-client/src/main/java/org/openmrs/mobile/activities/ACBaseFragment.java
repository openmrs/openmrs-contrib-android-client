package org.openmrs.mobile.activities;

import android.support.v4.app.Fragment;

public abstract class ACBaseFragment<T extends BasePresenterContract> extends Fragment implements BaseView<T> {

    protected T mPresenter;

    @Override
    public void setPresenter(T presenter) {
        mPresenter = presenter;
    }

    public boolean isActive() {
        return isAdded();
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.subscribe();
    }

    @Override
    public void onPause() {
        super.onPause();
        mPresenter.unsubscribe();
    }
}
