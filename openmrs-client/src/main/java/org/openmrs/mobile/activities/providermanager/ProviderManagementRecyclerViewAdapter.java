package org.openmrs.mobile.activities.providermanager;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.openmrs.mobile.R;
import org.openmrs.mobile.models.Provider;
import org.openmrs.mobile.providerdashboard.ProviderDashboardActivity;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.FontsUtil;

import java.util.List;

/**
 * Created by Chathuranga on 19/05/2018.
 */

public class ProviderManagementRecyclerViewAdapter extends
        RecyclerView.Adapter<ProviderManagementRecyclerViewAdapter.ProviderViewHolder> {
    private ProviderManagementFragment mContext;
    private List<Provider> mItems;

    public ProviderManagementRecyclerViewAdapter(ProviderManagementFragment context, List<Provider> items) {
        this.mContext = context;
        this.mItems = items;
    }

    @Override
    public ProviderManagementRecyclerViewAdapter.ProviderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.provider_management_row, parent, false);
        FontsUtil.setFont((ViewGroup) itemView);
        return new ProviderViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ProviderManagementRecyclerViewAdapter.ProviderViewHolder holder, int position) {
        final Provider provider = mItems.get(position);
        if (provider.getPerson().getDisplay() != null)
            holder.mName.setText(provider.getPerson().getDisplay());

        if (provider.getIdentifier() != null)
            holder.mIdentifier.setText(provider.getIdentifier());

        holder.mRowLayout.setOnClickListener(v -> {
            Intent intent = new Intent(mContext.getActivity(), ProviderDashboardActivity.class);
            intent.putExtra(ApplicationConstants.BundleKeys.PROVIDER_ID_BUNDLE, provider.getUuid());
            mContext.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    class ProviderViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout mRowLayout;
        private TextView mIdentifier;
        private TextView mName;


        public ProviderViewHolder(View itemView) {
            super(itemView);
            mRowLayout = (LinearLayout) itemView;
            mIdentifier = (TextView) itemView.findViewById(R.id.providerManagementIdentifier);
            mName = (TextView) itemView.findViewById(R.id.providerManagementName);

        }
    }

}
