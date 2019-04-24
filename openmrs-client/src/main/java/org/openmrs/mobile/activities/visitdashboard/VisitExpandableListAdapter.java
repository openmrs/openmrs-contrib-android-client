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

package org.openmrs.mobile.activities.visitdashboard;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.openmrs.mobile.R;
import org.openmrs.mobile.application.OpenMRSInflater;
import org.openmrs.mobile.models.Encounter;
import org.openmrs.mobile.models.EncounterType;
import org.openmrs.mobile.models.Observation;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.ImageUtils;

import java.util.ArrayList;
import java.util.List;

public class VisitExpandableListAdapter extends BaseExpandableListAdapter {

    private static final int LEFT = 0;
    private static final int RIGHT = 1;

    private Context mContext;
    private List<Encounter> mEncounters;
    private List<ViewGroup> mChildLayouts;
    private SparseArray<Bitmap> mBitmapCache;

    public VisitExpandableListAdapter(Context context, List<Encounter> encounters) {
        this.mContext = context;
        this.mEncounters = encounters;
        this.mBitmapCache = new SparseArray<Bitmap>();
        this.mChildLayouts = generateChildLayouts();
    }

    private List<ViewGroup> generateChildLayouts() {
        List<ViewGroup> layouts = new ArrayList<ViewGroup>();
        LayoutInflater inflater = LayoutInflater.from(mContext);
        OpenMRSInflater openMRSInflater = new OpenMRSInflater(inflater);

        for (Encounter encounter : this.mEncounters) {
            ViewGroup convertView = (ViewGroup) inflater.inflate(R.layout.list_visit_item, null);
            LinearLayout contentLayout = (LinearLayout) convertView.findViewById(R.id.listVisitItemLayoutContent);
            switch (encounter.getEncounterType().getDisplay()) {
                case EncounterType.VITALS:
                    for (Observation obs : encounter.getObservations()) {
                        convertView = openMRSInflater.addKeyValueStringView(contentLayout, obs.getDisplay(), obs.getDisplayValue());
                    }
                    layouts.add(convertView);
                    break;
                case EncounterType.VISIT_NOTE:
                    for (Observation obs : encounter.getObservations()) {
                        if (obs.getDiagnosisNote() != null && !obs.getDiagnosisNote().equals(ApplicationConstants.EMPTY_STRING)) {
                            convertView = openMRSInflater.addKeyValueStringView(contentLayout, mContext.getString(R.string.diagnosis_note_label), obs.getDiagnosisNote());
                        } else {
                            if (obs.getDiagnosisOrder() != null && obs.getShortDiagnosisCertainty() != null && obs.getDiagnosisList() != null) {
                                convertView = openMRSInflater.addKeyValueStringView(contentLayout, obs.getDiagnosisOrder(),
                                        "(" + obs.getShortDiagnosisCertainty() + ") " + obs.getDiagnosisList());
                            }
                        }
                    }
                    layouts.add(convertView);
                    break;
                case EncounterType.DISCHARGE:
                    convertView = openMRSInflater.addSingleStringView(contentLayout, mContext.getString(R.string.list_item_encounter_no_notes));
                    layouts.add(convertView);
                    break;
                case EncounterType.ADMISSION:
                    convertView = openMRSInflater.addSingleStringView(contentLayout, mContext.getString(R.string.list_item_encounter_no_notes));
                    layouts.add(convertView);
                    break;
                default:
                    break;
            }
        }

        return layouts;
    }

    @Override
    public int getGroupCount() {
        return mEncounters.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return  1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mEncounters.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mChildLayouts.get(groupPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getGroupView(int groupPosition, final boolean isExpanded, View convertView, ViewGroup parent) {
        View rowView = convertView;
        if (null == convertView) {
            LayoutInflater inflater = (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.list_visit_group, null);
        }

        final TextView encounterName = (TextView) rowView.findViewById(R.id.listVisitGroupEncounterName);
        final TextView detailsSelector = (TextView) rowView.findViewById(R.id.listVisitGroupDetailsSelector);
        final Encounter encounter = mEncounters.get(groupPosition);
        encounterName.setText(encounter.getEncounterType().getDisplay());
        if (isExpanded) {
            detailsSelector.setText(mContext.getString(R.string.list_visit_selector_hide));
            bindDrawableResources(R.drawable.exp_list_hide_details, detailsSelector, RIGHT);
        } else {
            detailsSelector.setText(mContext.getString(R.string.list_visit_selector_show));
            bindDrawableResources(R.drawable.exp_list_show_details, detailsSelector, RIGHT);
        }
        switch (encounter.getEncounterType().getDisplay()) {
            case EncounterType.VITALS:
                bindDrawableResources(R.drawable.ico_vitals_small, encounterName, LEFT);
                break;
            case EncounterType.VISIT_NOTE:
                bindDrawableResources(R.drawable.visit_note, encounterName, LEFT);
                break;
            case EncounterType.DISCHARGE:
                bindDrawableResources(R.drawable.discharge, encounterName, LEFT);
                break;
            case EncounterType.ADMISSION:
                bindDrawableResources(R.drawable.admission, encounterName, LEFT);
                break;
            default:
                break;
        }
        return rowView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        return (ViewGroup) getChild(groupPosition, childPosition);
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public void notifyDataSetChanged() {
        this.mChildLayouts = generateChildLayouts();
        super.notifyDataSetChanged();
    }

    private void bindDrawableResources(int drawableID, TextView textView, int direction) {
        final float scale = mContext.getResources().getDisplayMetrics().density;
        Drawable image = mContext.getResources().getDrawable(drawableID);
        if(direction == LEFT) {
            image.setBounds(0, 0, (int)(40 * scale + 0.5f), (int)(40 * scale + 0.5f));
            textView.setCompoundDrawablePadding((int)(13 * scale + 0.5f));
            textView.setCompoundDrawables(image, null, null, null);
        }else {
            image.setBounds(0, 0, image.getIntrinsicWidth(), image.getIntrinsicHeight());
            textView.setCompoundDrawablePadding((int)(10 * scale + 0.5f));
            textView.setCompoundDrawables(null, null, image, null);
        }
    }

    private void createImageBitmap(Integer key, ViewGroup.LayoutParams layoutParams) {
        if (mBitmapCache.get(key) == null) {
            mBitmapCache.put(key, ImageUtils.decodeBitmapFromResource(mContext.getResources(), key,
                    layoutParams.width, layoutParams.height));
        }
    }
}
