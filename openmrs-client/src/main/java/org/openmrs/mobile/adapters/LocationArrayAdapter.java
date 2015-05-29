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

package org.openmrs.mobile.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

public class LocationArrayAdapter extends ArrayAdapter<String> {

    private boolean emptyRemoved;

    public LocationArrayAdapter(Context context, List<String> objects) {
        super(context, android.R.layout.simple_spinner_item, objects);
        setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    }

    @Override
    public int getCount() {
        if (emptyRemoved) {
            return super.getCount();
        }
        return super.getCount() - 1;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        if (!emptyRemoved) {
            emptyRemoved = true;
            setNotifyOnChange(false);
            remove(getItem(0));
            setNotifyOnChange(true);
        }
        return super.getDropDownView(position, convertView, parent);
    }

    @Override
    public long getItemId(int position) {
        if (emptyRemoved) {
            return position + 1;
        }
        return position;
    }
}
