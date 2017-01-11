/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */

package org.openmrs.mobile.activities.lastviewedpatients;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.openmrs.mobile.R;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.utilities.ToastUtil;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class LastViewedPatientsFragment extends Fragment implements LastViewedPatientsContract.View {

    private TextView mEmptyList;
    private ProgressBar mSpinner;
    private RecyclerView mPatientsRecyclerView;
    private LastViewedPatientRecyclerViewAdapter mAdapter;
    public SwipeRefreshLayout mSwipeRefreshLayout;

    LastViewedPatientsContract.Presenter mPresenter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_last_viewed_patients, container, false);
        mPatientsRecyclerView = ((RecyclerView) root.findViewById(R.id.lastViewedPatientRecyclerView));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getActivity());
        mPatientsRecyclerView.setLayoutManager(linearLayoutManager);
        mSpinner = (ProgressBar) root.findViewById(R.id.patientRecyclerViewLoading);
        mEmptyList = (TextView) root.findViewById(R.id.emptyLastViewedPatientList);
        mSwipeRefreshLayout = ((SwipeRefreshLayout) root.findViewById(R.id.swiperefreshLastPatients));
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.refresh();
                mAdapter.finishActionMode();
            }
        });
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
    }

    @Override
    public boolean isActive() {
        return isAdded();
    }

    @Override
    public void setPresenter(@NonNull LastViewedPatientsContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }

    @Override
    public void enableSwipeRefresh(boolean enabled) {
        mSwipeRefreshLayout.setRefreshing(!enabled);
        mSwipeRefreshLayout.setEnabled(enabled);
    }

    @Override
    public void setSpinnerVisibility(boolean visibility) {
        mSpinner.setVisibility(visibility ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setEmptyListVisibility(boolean visibility) {
        mEmptyList.setVisibility(visibility ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setListVisibility(boolean visibility) {
        mPatientsRecyclerView.setVisibility(visibility ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setEmptyListText(String text) {
        mEmptyList.setText(text);
    }

    public static LastViewedPatientsFragment newInstance() {
        return new LastViewedPatientsFragment();
    }

    public void updateList(List<Patient> patientList) {
        mAdapter = new LastViewedPatientRecyclerViewAdapter(this.getActivity(), patientList);
        mPatientsRecyclerView.setAdapter(mAdapter);
    }

    public boolean isRefreshing() {
        return mSwipeRefreshLayout.isRefreshing();
    }

    public void stopRefreshing() {
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void showErrorToast(String message) {
        ToastUtil.error(message);
    }
}
