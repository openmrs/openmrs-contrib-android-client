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
package org.openmrs.mobile.utilities

import com.openmrs.android_sdk.utilities.DateUtils.getDateFromString
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.DateFormat
import java.text.SimpleDateFormat
import kotlin.math.abs

class DayAxisValueFormatter(private val dates: ArrayList<String>) : ValueFormatter(), IAxisValueFormatter {
    override fun getFormattedValue(value: Float, axis: AxisBase): String {
        val intValue = value.toInt()
        val vitalDate = getDateFromString(dates[abs(intValue)])
        val dateFormat: DateFormat = SimpleDateFormat("MMM d, ''yy")
        return dateFormat.format(vitalDate)
        //        return vitalDate.getDate() + "/" + vitalDate.getMonth() + "/" + vitalDate.getYear();
    }

}