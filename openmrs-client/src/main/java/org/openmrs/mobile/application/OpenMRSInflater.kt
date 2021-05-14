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
package org.openmrs.mobile.application

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import org.openmrs.mobile.R

class OpenMRSInflater(private val mInflater: LayoutInflater) {
    fun addKeyValueStringView(parentLayout: ViewGroup, label: String, data: String?): ViewGroup {
        val view = mInflater.inflate(R.layout.row_key_value_data, null, false)
        val labelText = view.findViewById<TextView>(R.id.keyValueDataRowTextLabel)
        if (label.contains(":")) {
            labelText.text = label.substring(0, label.indexOf(':'))
        } else {
            labelText.text = label
        }
        val dataText = view.findViewById<TextView>(R.id.keyValueDataRowTextData)
        dataText.text = data
        parentLayout.addView(view)
        return parentLayout
    }

    fun addSingleStringView(parentLayout: ViewGroup, label: String?): ViewGroup {
        val view = mInflater.inflate(R.layout.row_single_text_data, null, false)
        val labelText = view.findViewById<TextView>(R.id.singleTextRowLabelText)
        labelText.text = label
        parentLayout.addView(view)
        return parentLayout
    }
}