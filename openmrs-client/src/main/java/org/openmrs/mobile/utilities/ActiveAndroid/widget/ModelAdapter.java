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

package org.openmrs.mobile.utilities.ActiveAndroid.widget;

import android.content.Context;
import android.widget.ArrayAdapter;

import org.openmrs.mobile.utilities.ActiveAndroid.Model;

import java.util.Collection;
import java.util.List;


public class ModelAdapter<T extends Model> extends ArrayAdapter<T> {
    public ModelAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public ModelAdapter(Context context, int resource, int textViewResourceId) {
        super(context, resource, textViewResourceId);
    }

    public ModelAdapter(Context context, int textViewResourceId, List<T> objects) {
        super(context, textViewResourceId, objects);
    }

    public ModelAdapter(Context context, int resource, int textViewResourceId, List<T> objects) {
        super(context, resource, textViewResourceId, objects);
    }

    /**
     * Clears the adapter and, if data != null, fills if with new Items.
     *
     * @param collection A Collection&lt;? extends T&gt; which members get added to the adapter.
     */
    public void setData(Collection<? extends T> collection) {
        clear();

        if (collection != null) {
            for (T item : collection) {
                add(item);
            }
        }
    }

    /**
     * @return The Id of the record at position.
     */
    @Override
    public long getItemId(int position) {
        T item = getItem(position);

        if (item != null) {
            return item.getId();
        } else {
            return -1;
        }
    }
}
