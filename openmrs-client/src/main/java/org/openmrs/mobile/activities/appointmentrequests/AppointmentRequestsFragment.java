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
package org.openmrs.mobile.activities.appointmentrequests;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.ACBaseFragment;
import org.openmrs.mobile.activities.formappointmentrequest.FormAppointmentRequestActivity;
import org.openmrs.mobile.api.RestApi;
import org.openmrs.mobile.api.RestServiceBuilder;
import org.openmrs.mobile.models.appointmentrequestmodel.AppointmentRequest;
import org.openmrs.mobile.models.appointmentrequestmodel.Result;
import org.openmrs.mobile.utilities.FontsUtil;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class AppointmentRequestsFragment extends ACBaseFragment<AppointmentRequestsContract.Presenter> implements AppointmentRequestsContract.View {
    // Fragment components
    private TextView mEmptyList;
    private RecyclerView mAppointmentRequestRecyclerView;
    //Initialization Progress bar
    private ProgressBar mProgressBar;

    public static AppointmentRequestsFragment newInstance() {
        return new AppointmentRequestsFragment();
    }
    @Override
    public void getAppointmentRequests() {
        RestApi apiService = RestServiceBuilder.createService(RestApi.class);
        Call<AppointmentRequest> call = apiService.getAppointmentRequests();
        call.enqueue(new Callback<AppointmentRequest>() {

            @Override
            public void onResponse(Call<AppointmentRequest> call, Response<AppointmentRequest> response) {
                if (response.isSuccessful()) {

                    List<Result> appointmentRequests =response.body().getResults();
                    updateListVisibility(true);
                    updateAdapter(appointmentRequests);
                }

            }

            @Override
            public void onFailure(Call<AppointmentRequest> call, Throwable t) {
            //This method is left blank intentionally.
            }

        });

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_appointment_request, container, false);

        // Patient list config
        mAppointmentRequestRecyclerView = (RecyclerView) root.findViewById(R.id.appointmentRequestRecyclerView);
        mAppointmentRequestRecyclerView.setHasFixedSize(true);
        mAppointmentRequestRecyclerView.setAdapter(new AppointmentRequestsRecyclerViewAdapter(this,
                new ArrayList<Result>()));
        mAppointmentRequestRecyclerView.setVisibility(View.GONE);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(root.getContext());
        mAppointmentRequestRecyclerView.setLayoutManager(linearLayoutManager);
        mProgressBar = (ProgressBar) root.findViewById(R.id.appointmentRequestInitialProgressBar);
        mEmptyList = (TextView) root.findViewById(R.id.emptyAppointmentRequestList);
        mEmptyList.setText(getString(R.string.search_visits_no_results));
        mEmptyList.setVisibility(View.INVISIBLE);
        // Font config
        FontsUtil.setFont((ViewGroup) this.getActivity().findViewById(android.R.id.content));
        return root;
    }

    @Override
    public void updateAdapter(List<Result> appointmentRequests) {
        AppointmentRequestsRecyclerViewAdapter adapter = new AppointmentRequestsRecyclerViewAdapter(this, appointmentRequests);
        adapter.notifyDataSetChanged();
        mAppointmentRequestRecyclerView.setAdapter(adapter);
    }


    @Override
    public void updateListVisibility(boolean isVisible) {
        mProgressBar.setVisibility(View.GONE);
        if (isVisible) {
            mAppointmentRequestRecyclerView.setVisibility(View.VISIBLE);
            mEmptyList.setVisibility(View.GONE);
        } else {
            mAppointmentRequestRecyclerView.setVisibility(View.GONE);
            mEmptyList.setVisibility(View.VISIBLE);
            mEmptyList.setText(getString(R.string.search_patient_no_results));
        }
    }

    @Override
    public void setEmptyListText(int stringId) {
        mEmptyList.setText(getString(stringId));
    }

    @Override
    public void setEmptyListText(int stringId, String query) {
        mEmptyList.setText(getString(stringId, query));
    }
    @Override
    public void getForm(Result result) {
        super.onPause();

        Intent Intent = new Intent(this.getActivity(),FormAppointmentRequestActivity.class);
        Intent.putExtra("Uuid", result.getUuid());
        Intent.putExtra("Service", result.getAppointmentType().getDisplay());
        getActivity().startActivity(Intent);
    }
}
