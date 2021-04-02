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
package org.openmrs.mobile.activities.login

import android.R
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class LocationArrayAdapter(context: Context?, objects: List<String?>?) : ArrayAdapter<String?>(context!!, R.layout.simple_spinner_item, objects!!) {
    override fun getCount(): Int {
        return super.getCount()
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        if (position == 0) {
            val LocationTextView = TextView(context)
            LocationTextView.height = 0
            LocationTextView.visibility = View.GONE
            view = LocationTextView
        } else {
            view = super.getDropDownView(position, null, parent)
        }
        parent?.isVerticalScrollBarEnabled = false
        return view
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    init {
        setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
    }
}