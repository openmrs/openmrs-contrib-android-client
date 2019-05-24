package org.openmrs.mobile.activities.visitdashboard.visitnote;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.openmrs.mobile.R;
import org.openmrs.mobile.models.Concept;

import java.util.ArrayList;
import java.util.List;

public class DiagnosisAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Concept> datasource = new ArrayList<>();

    public void updateDataSet(List<Concept> data) {
        datasource.clear();
        datasource.addAll(data);
        notifyDataSetChanged();
    }

    public void addItem(Concept item) {
        datasource.add(item);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_item_diagnosis, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        String item = datasource.get(position).getDisplay();
        ((ViewHolder) holder).text.setText(item);
        ((ViewHolder) holder).remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datasource.remove(position);
                notifyItemRemoved(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return datasource.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView text;
        private final View remove;

        public ViewHolder(@NonNull View view) {
            super(view);
            text = view.findViewById(R.id.text);
            remove = view.findViewById(R.id.removeDiagnosis);
        }
    }
}

