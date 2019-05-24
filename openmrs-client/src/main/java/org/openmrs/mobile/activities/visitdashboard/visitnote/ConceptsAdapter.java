package org.openmrs.mobile.activities.visitdashboard.visitnote;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.openmrs.mobile.models.Concept;
import org.openmrs.mobile.usecase.ConceptUseCase;

import java.util.ArrayList;
import java.util.List;

public class ConceptsAdapter
        extends ArrayAdapter<Concept>
        implements Filterable {

    private ConceptUseCase mConceptUseCase;

    private ArrayList<Concept> data = new ArrayList<>();

    public ConceptsAdapter(@NonNull Context context, ConceptUseCase conceptUseCase) {
        super(context, android.R.layout.simple_dropdown_item_1line, new ArrayList<>());
        mConceptUseCase = conceptUseCase;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Concept item = getItem(position);
        ViewHolder viewHolder = null;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            convertView = inflater.inflate(android.R.layout.simple_dropdown_item_1line, parent, false);

            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.text.setText(item.getDisplay());

        return convertView;
    }

    @NonNull
    @Override
    public int getCount() {
        return data.size();
    }

    @Nullable
    @Override
    public Concept getItem(int position) {
        return data.get(position);
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<Concept> values = mConceptUseCase.getConcepts(String.valueOf(constraint)).getResults();

                FilterResults filterResults = new FilterResults();
                filterResults.values = values;
                filterResults.count = values.size();
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                List<Concept> dataset = (List<Concept>) results.values;
                data.clear();
                if (dataset != null) {
                    if (dataset.isEmpty() && !TextUtils.isEmpty(constraint)) {
                        Concept concept = new Concept();
                        concept.setDisplay("Non coded value " + constraint);
                        dataset.add(concept);
                    }
                    data.addAll(dataset);
                }
                notifyDataSetChanged();
            }
        };
    }

    public void setData(ArrayList<Concept> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    // PRIVATE METHODS AND CLASSES

    private class ViewHolder {
        private final TextView text;

        public ViewHolder(View view) {
            text = view.findViewById(android.R.id.text1);
        }
    }
}
