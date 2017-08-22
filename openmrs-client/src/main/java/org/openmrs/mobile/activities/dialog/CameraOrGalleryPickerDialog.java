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

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.openmrs.mobile.R;

public class CameraOrGalleryPickerDialog extends DialogFragment {

    private DialogInterface.OnClickListener listener;


    public static CameraOrGalleryPickerDialog getInstance(DialogInterface.OnClickListener listener) {
        CameraOrGalleryPickerDialog dialog = new CameraOrGalleryPickerDialog();
        dialog.listener = listener;
        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {


        String[] textResources = {"Take a photo", "Choose another"};
        int[] imageResources = {R.drawable.ic_photo_camera, R.drawable.ic_photo_library};


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setAdapter(new GalleryOrCameraPickerListAdapter(getActivity(),
                R.layout.list_gallery_or_camera_item, R.id.textView, R.id.imageView,
                textResources, imageResources), listener);

        return builder.create();
    }

    private class GalleryOrCameraPickerListAdapter extends ArrayAdapter<String> {

        @IdRes private int textViewResourceId;

        @IdRes private int imageViewResourceId;

        private String[] textResources;
        private int[] imageResources;


        public GalleryOrCameraPickerListAdapter(@NonNull Context context, @LayoutRes int resource,
                                                @IdRes int textViewResourceId, @IdRes int imageViewResourceId,
                                                String[] textResources, int[] imageResources) {
            super(context, resource, textViewResourceId, textResources);
            this.textResources = textResources;
            this.imageResources = imageResources;
            this.textViewResourceId = textViewResourceId;
            this.imageViewResourceId = imageViewResourceId;
        }

        @Override
        public int getCount() {
            return textResources.length;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            View v =  super.getView(position, convertView, parent);

            ((ImageView) v.findViewById(imageViewResourceId)).setImageResource(imageResources[position]);
            ((TextView) v.findViewById(textViewResourceId)).setText(textResources[position]);

            return v;
        }
    }
}
