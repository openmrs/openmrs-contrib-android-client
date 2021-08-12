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

package org.openmrs.mobile.activities.providermanagerdashboard;

import android.content.Intent;
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

import com.openmrs.android_sdk.library.models.Provider;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.openmrs.mobile.activities.ACBaseFragment;
import org.openmrs.mobile.activities.providermanagerdashboard.addprovider.AddProviderActivity;
import org.openmrs.mobile.databinding.FragmentProviderManagementBinding;
import com.openmrs.android_sdk.utilities.ApplicationConstants;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static com.openmrs.android_sdk.utilities.ApplicationConstants.RequestCodes.ADD_PROVIDER_REQ_CODE;
import static com.openmrs.android_sdk.utilities.ApplicationConstants.RequestCodes.EDIT_PROVIDER_REQ_CODE;

public class ProviderManagerDashboardFragment extends ACBaseFragment<ProviderManagerDashboardContract.Presenter>
    implements ProviderManagerDashboardContract.View {

    private FragmentProviderManagementBinding binding;
    private TextView emptyList;
    private RecyclerView providerManagementRecyclerView;
    private ProviderManagerDashboardRecyclerViewAdapter providersAdapter;
    private SwipeRefreshLayout refreshList;
    private ProgressBar progressBar;
    private List<Provider> providerList;
    public FloatingActionButton addProviderFab;

    /**
     * @return New instance of ProviderManagerDashboardFragment
     */
    public static ProviderManagerDashboardFragment newInstance() {
        return new ProviderManagerDashboardFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProviderManagementBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        providerList = new ArrayList<>();

        providersAdapter = new ProviderManagerDashboardRecyclerViewAdapter(this, mPresenter, providerList);

        providerManagementRecyclerView = binding.providerManagementRecyclerView;
        providerManagementRecyclerView.setHasFixedSize(true);
        providerManagementRecyclerView.setAdapter(providersAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(root.getContext());
        providerManagementRecyclerView.setLayoutManager(linearLayoutManager);

        emptyList = binding.emptyProviderManagementList;
        progressBar = binding.providerManagementInitialProgressBar;
        addProviderFab = binding.providerManagementFragAddFAB;
        refreshList = binding.swipeLayout;

        refreshUI();

        addProviderFab.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), AddProviderActivity.class);
            ArrayList<Provider> providerArrayList = new ArrayList<>(providerList);
            intent.putExtra(ApplicationConstants.BundleKeys.EXISTING_PROVIDERS_BUNDLE, providerArrayList);
            startActivityForResult(intent, ADD_PROVIDER_REQ_CODE);
        });

        refreshList.setOnRefreshListener(() -> {
            refreshUI();
            refreshList.setRefreshing(false);
        });
        return root;
    }

    @Override
    public void refreshUI() {
        providerManagementRecyclerView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        mPresenter.getProviders(this);
    }

    @Override
    public void updateAdapter(List<Provider> providerList) {
        this.providerList = providerList;
        providersAdapter.setItems(providerList);
        providersAdapter.notifyDataSetChanged();
    }

    @Override
    public void updateVisibility(boolean visibility, String text) {
        progressBar.setVisibility(View.GONE);
        if (visibility) {
            providerManagementRecyclerView.setVisibility(View.VISIBLE);
            emptyList.setVisibility(View.GONE);
        } else {
            providerManagementRecyclerView.setVisibility(View.GONE);
            emptyList.setVisibility(View.VISIBLE);
            emptyList.setText(text);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ADD_PROVIDER_REQ_CODE) {
            if (resultCode == RESULT_OK) {
                Provider provider = (Provider) data.getSerializableExtra(ApplicationConstants.BundleKeys.PROVIDER_ID_BUNDLE);
                mPresenter.addProvider(provider);
            }
        } else if (requestCode == EDIT_PROVIDER_REQ_CODE) {
            if (resultCode == RESULT_OK) {
                Provider provider = (Provider) data.getSerializableExtra(ApplicationConstants.BundleKeys.PROVIDER_ID_BUNDLE);
                mPresenter.updateProvider(provider);
            }
        }
        providersAdapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshUI();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}