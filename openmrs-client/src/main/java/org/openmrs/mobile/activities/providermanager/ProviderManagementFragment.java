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

package org.openmrs.mobile.activities.providermanager;

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

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.ACBaseFragment;
import org.openmrs.mobile.models.Provider;
import org.openmrs.mobile.utilities.FontsUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ProviderManagementFragment extends ACBaseFragment<ProviderManagerContract.Presenter>
        implements ProviderManagerContract.View {
    // Fragment components
    private TextView mEmptyList;
    private RecyclerView mProviderManagementRecyclerView;
    private ProviderManagementRecyclerViewAdapter providersAdapter;

    //Initialization Progress bar
    private ProgressBar mProgressBar;

    private List<Provider> providerList;

    /**
     * @return New instance of ProviderManagementFragment
     */
    public static ProviderManagementFragment newInstance() {
        return new ProviderManagementFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_provider_management, container, false);
        providerList = new ArrayList<>();

        mPresenter.getProviders(this);

        providersAdapter = new ProviderManagementRecyclerViewAdapter(getContext(), providerList);

        mProviderManagementRecyclerView = root.findViewById(R.id.providerManagementRecyclerView);
        mProviderManagementRecyclerView.setHasFixedSize(true);
        mProviderManagementRecyclerView.setAdapter(providersAdapter);
        mProviderManagementRecyclerView.setVisibility(View.GONE);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(root.getContext());
        mProviderManagementRecyclerView.setLayoutManager(linearLayoutManager);

        mEmptyList = root.findViewById(R.id.emptyProviderManagementList);
        mProgressBar = root.findViewById(R.id.providerManagementInitialProgressBar);

        // Font config
        FontsUtil.setFont(Objects.requireNonNull(this.getActivity()).findViewById(android.R.id.content));

        return root;
    }


    @Override
    public void updateAdapter(List<Provider> providerList) {
        this.providerList = providerList;
        providersAdapter.setItems(providerList);
        providersAdapter.notifyDataSetChanged();
    }

    @Override
    public void updateVisibility(boolean visibility, String text) {
        mProgressBar.setVisibility(View.GONE);
        if (visibility) {
            mProviderManagementRecyclerView.setVisibility(View.VISIBLE);
            mEmptyList.setVisibility(View.GONE);
        } else {
            mProviderManagementRecyclerView.setVisibility(View.GONE);
            mEmptyList.setVisibility(View.VISIBLE);
            mEmptyList.setText(text);
        }
    }

    public void filterProviders(String searchString) {
        if (searchString.isEmpty()) {
            providersAdapter.setItems(providerList);
            providersAdapter.notifyDataSetChanged();
            return;
        }

        List<Provider> filteredProviders = new ArrayList<>(providerList);
        for (Provider provider : providerList) {
            if (!provider.getDisplay().toLowerCase().contains(searchString.toLowerCase())) {
                filteredProviders.remove(provider);
            }
        }

        providersAdapter.setItems(filteredProviders);
        providersAdapter.notifyDataSetChanged();
    }
}