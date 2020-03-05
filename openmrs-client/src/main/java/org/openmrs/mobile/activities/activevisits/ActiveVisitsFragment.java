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

package org.openmrs.mobile.activities.activevisits;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.ACBaseFragment;
import org.openmrs.mobile.models.Visit;
import org.openmrs.mobile.utilities.FontsUtil;

import java.util.ArrayList;
import java.util.List;

public class ActiveVisitsFragment extends ACBaseFragment<ActiveVisitsContract.Presenter> implements ActiveVisitsContract.View {

    private RecyclerView visitsRecyclerView;
    private TextView emptyList;
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefreshLayout;

    public static ActiveVisitsFragment newInstance(){
        return new ActiveVisitsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_active_visits, container, false);

        progressBar = root.findViewById(R.id.progressBar);
        visitsRecyclerView = root.findViewById(R.id.visitsRecyclerView);
        swipeRefreshLayout = root.findViewById(R.id.swipeLayout);
        visitsRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getActivity());
        visitsRecyclerView.setLayoutManager(linearLayoutManager);
        visitsRecyclerView.setAdapter(new ActiveVisitsRecyclerViewAdapter(this.getActivity(),
                new ArrayList<>()));

        emptyList = root.findViewById(R.id.emptyVisitsListViewLabel);
        emptyList.setText(getString(R.string.search_visits_no_results));
        emptyList.setVisibility(View.INVISIBLE);

        swipeRefreshLayout.setOnRefreshListener(() -> {
            refreshUI();
            swipeRefreshLayout.setRefreshing(false);
        });

        FontsUtil.setFont(this.getActivity().findViewById(android.R.id.content));

        return root;
    }

    private void refreshUI() {
        progressBar.setVisibility(View.VISIBLE);
        mPresenter.updateVisitsInDatabaseList();
    }

    @Override
    public void updateListVisibility(List<Visit> visits) {
        progressBar.setVisibility(View.GONE);
        if (visits.isEmpty()) {
            visitsRecyclerView.setVisibility(View.GONE);
            emptyList.setVisibility(View.VISIBLE);
        } else {
            visitsRecyclerView.setAdapter(new ActiveVisitsRecyclerViewAdapter(this.getActivity(), visits));
            visitsRecyclerView.setVisibility(View.VISIBLE);
            emptyList.setVisibility(View.GONE);
        }
    }

    @Override
    public void setEmptyListText(int stringId) {
        emptyList.setText(getString(stringId));
    }

    @Override
    public void setEmptyListText(int stringId, String query) {
        emptyList.setText(getString(stringId, query));
    }
}
