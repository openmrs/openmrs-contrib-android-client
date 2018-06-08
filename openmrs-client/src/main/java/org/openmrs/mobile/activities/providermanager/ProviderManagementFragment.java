package org.openmrs.mobile.activities.providermanager;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.ACBaseFragment;
import org.openmrs.mobile.models.Provider;
import org.openmrs.mobile.utilities.FontsUtil;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Chathuranga on 16/05/2018.
 */

public class ProviderManagementFragment extends ACBaseFragment<ProviderManagerContract.Presenter>
        implements ProviderManagerContract.View {
    // Fragment components
    private TextView mEmptyList;
    private RecyclerView mProviderManagementRecyclerView;

    //Initialization Progress bar
    private ProgressBar mProgressBar;

    private MenuItem mAddProvidersMenuItem;

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

        mPresenter.getProviders();


        mProviderManagementRecyclerView = (RecyclerView) root.findViewById(R.id.providerManagementRecyclerView);
        mProviderManagementRecyclerView.setHasFixedSize(true);
        mProviderManagementRecyclerView.setAdapter(new ProviderManagementRecyclerViewAdapter(this,
                providerList));
        mProviderManagementRecyclerView.setVisibility(View.GONE);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(root.getContext());
        mProviderManagementRecyclerView.setLayoutManager(linearLayoutManager);

        mEmptyList = root.findViewById(R.id.emptyProviderManagementList);
        mProgressBar = root.findViewById(R.id.providerManagementInitialProgressBar);

        // Font config
        FontsUtil.setFont(this.getActivity().findViewById(android.R.id.content));

        return root;
    }



    @Override
    public void updateAdapter(List<Provider> providerList) {
        this.providerList =  providerList;
        mProviderManagementRecyclerView.getAdapter().notifyDataSetChanged();
        ProviderManagementRecyclerViewAdapter adapter = new ProviderManagementRecyclerViewAdapter(this,providerList);
        mProviderManagementRecyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

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
}
