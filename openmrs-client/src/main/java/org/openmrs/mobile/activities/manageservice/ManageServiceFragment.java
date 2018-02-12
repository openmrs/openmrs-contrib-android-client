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
package org.openmrs.mobile.activities.manageservice;
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
import org.openmrs.mobile.activities.formaddeditservice.FormAddEditServiceActivity;
import org.openmrs.mobile.api.RestApi;
import org.openmrs.mobile.api.RestServiceBuilder;
import org.openmrs.mobile.models.Results;
import org.openmrs.mobile.models.servicestypemodel.Services;
import org.openmrs.mobile.utilities.FontsUtil;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
public class ManageServiceFragment extends ACBaseFragment<ManageServiceContract.Presenter> implements ManageServiceContract.View {
    // Fragment components
    private TextView mEmptyList;
    private RecyclerView mManageServiceRecyclerView;
    //Initialization Progress bar
    private ProgressBar mProgressBar;


    public static ManageServiceFragment newInstance() {
        return new ManageServiceFragment();
    }

    @Override
    public void getServiceTypes() {
        RestApi apiService = RestServiceBuilder.createService(RestApi.class);
        Call<Results<Services>> call = apiService.getServiceTypes();
        call.enqueue(new Callback<Results<Services>>() {

            @Override
            public void onResponse(Call<Results<Services>> call, Response<Results<Services>> response) {
                if (response.isSuccessful()) {
                  List<Services> services =response.body().getResults();
                    updateListVisibility(true);
                    updateAdapter(services);
                }

            }

            @Override
            public void onFailure(Call<Results<Services>> call, Throwable t) {
                //This method is left blank intentionally.
            }

        });

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_manage_service, container, false);

        // Patient list config
        mManageServiceRecyclerView = (RecyclerView) root.findViewById(R.id.manageServiceRecyclerView);
        mManageServiceRecyclerView.setHasFixedSize(true);
        mManageServiceRecyclerView.setAdapter(new ManageServiceRecyclerViewAdapter(this,
                new ArrayList<Services>()));
        mManageServiceRecyclerView.setVisibility(View.GONE);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(root.getContext());
        mManageServiceRecyclerView.setLayoutManager(linearLayoutManager);
        mProgressBar = (ProgressBar) root.findViewById(R.id.manageServiceInitialProgressBar);
        mEmptyList = (TextView) root.findViewById(R.id.emptyManageServiceList);
        mEmptyList.setText(getString(R.string.search_visits_no_results));
        mEmptyList.setVisibility(View.INVISIBLE);
        // Font config
        FontsUtil.setFont((ViewGroup) this.getActivity().findViewById(android.R.id.content));
        return root;
    }

    @Override
    public void updateAdapter(List<Services> services) {
        ManageServiceRecyclerViewAdapter adapter = new ManageServiceRecyclerViewAdapter(this, services);
        adapter.notifyDataSetChanged();
        mManageServiceRecyclerView.setAdapter(adapter);
    }


    @Override
    public void updateListVisibility(boolean isVisible) {
        mProgressBar.setVisibility(View.GONE);
        if (isVisible) {
            mManageServiceRecyclerView.setVisibility(View.VISIBLE);
            mEmptyList.setVisibility(View.GONE);
        } else {
            mManageServiceRecyclerView.setVisibility(View.GONE);
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

    public void showDialog(Services services) {
        super.onPause();
        Intent Intent = new Intent(this.getActivity(),FormAddEditServiceActivity.class);
        Intent.putExtra("service_name", services.getName());
        Intent.putExtra("service_duration", String.valueOf(services.getDuration()));
        Intent.putExtra("service_description",String.valueOf(services.getDescription()));
        Intent.putExtra("service_uuid",String.valueOf(services.getUuid()));
        Intent.putExtra("edit_or_new",1);
        getActivity().startActivity(Intent);


    }

    public void newDialog() {
        super.onPause();
        Intent Intent = new Intent(this.getActivity(),FormAddEditServiceActivity.class);
        Intent.putExtra("edit_or_new",0);
        getActivity().startActivity(Intent);

    }

}