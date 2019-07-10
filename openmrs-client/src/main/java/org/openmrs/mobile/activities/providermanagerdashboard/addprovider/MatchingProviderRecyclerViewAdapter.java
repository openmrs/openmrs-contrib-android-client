package org.openmrs.mobile.activities.providermanagerdashboard.addprovider;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import org.openmrs.mobile.R;
import org.openmrs.mobile.models.Provider;
import org.openmrs.mobile.utilities.FontsUtil;

import java.util.List;

public class MatchingProviderRecyclerViewAdapter extends
        RecyclerView.Adapter<MatchingProviderRecyclerViewAdapter.SimilarProviderViewHolder> {
    private List<Provider> mItems;
    private Context context;

    public MatchingProviderRecyclerViewAdapter(Context context, List<Provider> items) {
        this.context = context;
        this.mItems = items;
    }

    @Override
    public SimilarProviderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_matching_provider, parent, false);
        FontsUtil.setFont((ViewGroup) itemView);
        return new SimilarProviderViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SimilarProviderViewHolder holder, int position) {
        final Provider provider = mItems.get(position);
        if (provider.getPerson().getDisplay() != null) {
            holder.mName.setText(provider.getPerson().getDisplay());
        }

        if (provider.getIdentifier() != null) {
            holder.mIdentifier.setText(provider.getIdentifier());
        }

        // TODO open provider dashboard for clicked provider
        holder.providerDetailsCL.setOnClickListener(view -> {
            // Action
        });

    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }


    class SimilarProviderViewHolder extends RecyclerView.ViewHolder {
        private TextView mIdentifier;
        private TextView mName;
        private ConstraintLayout providerDetailsCL;

        public SimilarProviderViewHolder(View itemView) {
            super(itemView);
            providerDetailsCL = itemView.findViewById(R.id.providerManagementCL);
            mIdentifier = itemView.findViewById(R.id.providerManagementIdentifier);
            mName = itemView.findViewById(R.id.providerManagementName);

        }
    }

    public void setItems(List<Provider> mItems) {
        this.mItems = mItems;
    }
}