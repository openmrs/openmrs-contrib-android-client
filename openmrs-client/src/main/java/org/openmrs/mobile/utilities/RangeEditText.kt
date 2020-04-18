package org.openmrs.mobile.utilities

import android.content.Context
import androidx.appcompat.widget.AppCompatEditText


class RangeEditText(context: Context?) : AppCompatEditText(context!!) {
    var upperlimit: Double? = null
    var lowerlimit: Double? = null
    var name: String? = null

}
