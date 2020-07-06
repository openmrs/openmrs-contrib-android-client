package org.openmrs.mobile.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

class SystemProperty {
    @SerializedName("value")
    @Expose
    var conceptUUID: String? = null

    @SerializedName("display")
    @Expose
    var display: String? = null
}
