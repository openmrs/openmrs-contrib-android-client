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

package org.openmrs.mobile.activities.patientdashboard.allergy;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.openmrs.android_sdk.library.models.Allergy;

import org.openmrs.mobile.R;
import com.openmrs.android_sdk.utilities.ApplicationConstants;

import java.util.List;

public class PatientAllergyRecyclerViewAdapter extends RecyclerView.Adapter<PatientAllergyRecyclerViewAdapter.ViewHolder> {
    private Context context;
    private List<Allergy> list;
    private OnLongPressListener longPressListener;

    public PatientAllergyRecyclerViewAdapter(Context context, List<Allergy> list, OnLongPressListener longPressListener) {
        this.context = context;
        this.list = list;
        this.longPressListener = longPressListener;
    }

    @NonNull
    @Override
    public PatientAllergyRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.list_patient_allergy, parent, false);
        return new ViewHolder(itemView, longPressListener);
    }

    @Override
    public void onBindViewHolder(@NonNull PatientAllergyRecyclerViewAdapter.ViewHolder holder, int position) {
        Allergy allergy = list.get(position);
        holder.allergen.setText(allergy.getAllergen().getCodedAllergen().getDisplay());
        if (null == allergy.getComment() || allergy.getComment().isEmpty()) {
            holder.comment.setText(ApplicationConstants.EMPTY_DASH_REPRESENTATION);
        } else {
            holder.comment.setText(allergy.getComment());
        }

        if (allergy.getReactions().size() == 0) {
            holder.reaction.setText(ApplicationConstants.EMPTY_DASH_REPRESENTATION);
        } else {
            StringBuilder reactions = new StringBuilder();
            for (int i = 0; i < allergy.getReactions().size() - 1; i++) {
                reactions.append(allergy.getReactions().get(i).getReaction().getDisplay()).append(ApplicationConstants.COMMA_WITH_SPACE);
            }
            reactions.append(allergy.getReactions().get(allergy.getReactions().size() - 1).getReaction().getDisplay());
            holder.reaction.setText(reactions);
        }

        if (allergy.getSeverity() == null) {
            holder.severity.setText(ApplicationConstants.EMPTY_DASH_REPRESENTATION);
        } else {
            holder.severity.setText(allergy.getSeverity().getDisplay());
        }

        holder.cardView.setOnLongClickListener(view -> {
            longPressListener.showDialogueBox(allergy);
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView allergen;
        private TextView reaction;
        private TextView severity;
        private TextView comment;
        private CardView cardView;

        public ViewHolder(@NonNull View itemView, OnLongPressListener longPressListener) {
            super(itemView);
            allergen = itemView.findViewById(R.id.allergy_allergen);
            reaction = itemView.findViewById(R.id.allergy_reaction);
            severity = itemView.findViewById(R.id.allergy_severity);
            comment = itemView.findViewById(R.id.allergy_comment);
            cardView = itemView.findViewById(R.id.allergy_cardView);
        }
    }

    interface OnLongPressListener {
        void showDialogueBox(Allergy allergy);
    }
}
