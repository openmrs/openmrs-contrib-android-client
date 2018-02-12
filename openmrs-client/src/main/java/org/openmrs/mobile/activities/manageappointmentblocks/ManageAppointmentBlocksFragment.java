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
package org.openmrs.mobile.activities.manageappointmentblocks;

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
import org.openmrs.mobile.activities.formmanageappointmentblocks.FormManageAppointmentBlocksActivity;
import org.openmrs.mobile.api.RestApi;
import org.openmrs.mobile.api.RestServiceBuilder;
import org.openmrs.mobile.models.appointmentblocksmodel.AppointmentBlocks;
import org.openmrs.mobile.models.appointmentblocksmodel.Result;
import org.openmrs.mobile.utilities.DateUtils;
import org.openmrs.mobile.utilities.FontsUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ManageAppointmentBlocksFragment extends ACBaseFragment<ManageAppointmentBlocksContract.Presenter> implements ManageAppointmentBlocksContract.View {
    private TextView mEmptyList;

    //Initialization Progress bar
    private ProgressBar mProgressBar;


    private RecyclerView mManageAppointmentBlocksRecyclerView;

    public static ManageAppointmentBlocksFragment newInstance() {
        return new ManageAppointmentBlocksFragment();
    }

    @Override
    public void getAppointmentBlocks() {
        RestApi apiService = RestServiceBuilder.createService(RestApi.class);
        Call<AppointmentBlocks> call = apiService.getAppointmentBlocks();
        call.enqueue(new Callback<AppointmentBlocks>() {

            @Override
            public void onResponse(Call<AppointmentBlocks> call, Response<AppointmentBlocks> response) {
                if (response.isSuccessful()) {
                    List<Result> blocks = response.body().getResults();
                    updateListVisibility(true);
                    updateAdapter(blocks);
                }

            }

            @Override
            public void onFailure(Call<AppointmentBlocks> call, Throwable t) {
                //This method is left blank intentionally.
            }

        });

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_manage_appointment_blocks, container, false);

        // Patient list config
        mManageAppointmentBlocksRecyclerView = (RecyclerView) root.findViewById(R.id.manageAppointmentBlocksRecyclerView);
        mManageAppointmentBlocksRecyclerView.setHasFixedSize(true);
        mManageAppointmentBlocksRecyclerView.setAdapter(new ManageAppointmentBlocksRecyclerViewAdapter(this,
                new ArrayList<Result>()));
        mManageAppointmentBlocksRecyclerView.setVisibility(View.GONE);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(root.getContext());
        mManageAppointmentBlocksRecyclerView.setLayoutManager(linearLayoutManager);
        mProgressBar =
                (ProgressBar) root.findViewById(R.id.manageAppointmentBlocksInitialProgressBar);
        mEmptyList = (TextView) root.findViewById(R.id.emptyManageAppointmentBlocksList);
        mEmptyList.setText(getString(R.string.search_visits_no_results));
        mEmptyList.setVisibility(View.INVISIBLE);
        // Font config
        FontsUtil.setFont((ViewGroup) this.getActivity().findViewById(android.R.id.content));
        return root;
    }

    @Override
    public void updateAdapter(List<Result> blocks)  {
        List<Result> latest = new ArrayList<>();
        Calendar c = Calendar.getInstance();
        for (int i = 0; i < blocks.size(); i++) {
            Long Date = DateUtils.convertTime(blocks.get(i).getStartDate());
            if (c.getTimeInMillis()<Date) {
             latest.add(blocks.get(i));
            }
        }

        ManageAppointmentBlocksRecyclerViewAdapter adapter = new ManageAppointmentBlocksRecyclerViewAdapter(this, latest);
        adapter.notifyDataSetChanged();
        mManageAppointmentBlocksRecyclerView.setAdapter(adapter);
    }


    @Override
    public void updateListVisibility(boolean isVisible) {
        mProgressBar.setVisibility(View.GONE);
        if (isVisible) {
            mManageAppointmentBlocksRecyclerView.setVisibility(View.VISIBLE);
            mEmptyList.setVisibility(View.GONE);
        } else {
            mManageAppointmentBlocksRecyclerView.setVisibility(View.GONE);
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
    public void openForm(Result result) {
        List<String> test =new ArrayList<>();
        List<String> test2 =new ArrayList<>();
        test.add(result.getLocation().getDisplay());
        test.add(result.getProvider().getPerson().getDisplay());
        test.add(result.getStartDate());
        test.add(result.getEndDate());
        for (int i=0;i<result.getTypes().size();i++) {
            test2.add(result.getTypes().get(i).getDisplay());
        }
        super.onPause();
        Intent Intent = new Intent(this.getActivity(), FormManageAppointmentBlocksActivity.class);
        Intent.putExtra("blocks_uuid", result.getUuid());
        Intent.putExtra("edit_or_new", "edit_form");
        Intent.putExtra("test", (ArrayList<String>) test);
        Intent.putExtra("test2", (ArrayList<String>) test2);
        getActivity().startActivity(Intent);

    }


    @Override
    public void newForm() {
        super.onPause();
        Intent Intent = new Intent(this.getActivity(), FormManageAppointmentBlocksActivity.class);
        Intent.putExtra("edit_or_new", "new_form" );
        getActivity().startActivity(Intent);
    }
}
