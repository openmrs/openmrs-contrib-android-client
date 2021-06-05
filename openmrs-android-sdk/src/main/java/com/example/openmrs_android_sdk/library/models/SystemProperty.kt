package com.example.openmrs_android_sdk.library.models;

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
