package org.openmrs.mobile.utilities

import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import org.openmrs.mobile.utilities.DateUtils.getDateFromString
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


class DayAxisValueFormatter(private val dates: ArrayList<String>) : IAxisValueFormatter {

    override fun getFormattedValue(value: Float, axis: AxisBase): String {
        val intValue = value.toInt()
        val vitalDate = getDateFromString(dates[Math.abs(intValue)])
        val dateFormat: DateFormat = SimpleDateFormat("MMM d, ''yy")
        return dateFormat.format(vitalDate)
        //        return vitalDate.getDate() + "/" + vitalDate.getMonth() + "/" + vitalDate.getYear();
    }

}