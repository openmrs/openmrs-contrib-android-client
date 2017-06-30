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

package org.openmrs.mobile.utilities;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class DayAxisValueFormatter implements IAxisValueFormatter {

    private ArrayList<String> dates;

    public DayAxisValueFormatter(ArrayList<String> dateList) {
        this.dates = dateList;
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {

        int intValue = (int) value;

        Date vitalDate = DateUtils.getDateFromString(dates.get(Math.abs(intValue)));
        DateFormat dateFormat = new SimpleDateFormat("MMM d, ''yy");
        return dateFormat.format(vitalDate);
//        return vitalDate.getDate() + "/" + vitalDate.getMonth() + "/" + vitalDate.getYear();
    }
  }