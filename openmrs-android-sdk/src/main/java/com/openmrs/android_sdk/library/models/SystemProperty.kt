package com.openmrs.android_sdk.library.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * System property
 *
 * @constructor Create empty System property
 */
class SystemProperty {
    @SerializedName("value")
    @Expose
    var conceptUUID: String? = null

    @SerializedName("display")
    @Expose
    var display: String? = null
}
