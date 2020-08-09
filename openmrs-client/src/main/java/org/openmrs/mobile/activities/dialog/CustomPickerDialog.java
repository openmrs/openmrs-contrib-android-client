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

package org.openmrs.mobile.activities.dialog;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.openmrs.mobile.R;

import java.util.List;

public class CustomPickerDialog extends DialogFragment {
    private onInputSelected mOnInputSelected;
    private List<CustomDialogModel> list;

    public CustomPickerDialog(List<CustomDialogModel> list) {
        this.list = list;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_my_custom, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView_dialog);
        CustomDialogAdapter adapter = new CustomDialogAdapter(getActivity(), list);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(false);
        recyclerView.setAdapter(adapter);

        return view;
    }

    private void performActivity(int position) {
        getDialog().dismiss();
        mOnInputSelected.performFunction(position);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            mOnInputSelected = (onInputSelected) getTargetFragment();
        } catch (ClassCastException e) {
            Log.e("error", "onAttach: ClassCastException : " + e.getMessage());
        }
    }

    public interface onInputSelected {
        void performFunction(int position);
    }

    public class CustomDialogAdapter extends RecyclerView.Adapter<CustomDialogAdapter.ViewHolder> {

        Context context;
        List<CustomDialogModel> modelList;

        public CustomDialogAdapter(Context context, List<CustomDialogModel> list) {
            this.context = context;
            this.modelList = list;
        }

        @NonNull
        @Override
        public CustomDialogAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.list_gallery_or_camera_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull CustomDialogAdapter.ViewHolder holder, int position) {
            CustomDialogModel customDialogModel = modelList.get(position);
            holder.textView.setText(customDialogModel.getName());
            holder.imageView.setImageResource(customDialogModel.getId());
            holder.linearLayout.setOnClickListener(v -> CustomPickerDialog.this.performActivity(position));
        }

        @Override
        public int getItemCount() {
            return modelList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            ImageView imageView;
            TextView textView;
            LinearLayout linearLayout;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                imageView = itemView.findViewById(R.id.imageView);
                textView = itemView.findViewById(R.id.textView);
                linearLayout = itemView.findViewById(R.id.linearLayoutDialog);
            }
        }
    }

}
