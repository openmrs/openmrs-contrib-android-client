package com.openmrs.android_sdk.library.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Form data
 *
 * <p> More about Forms https://rest.openmrs.org/#forms </p>
 * @constructor Create empty Form data
 */
class FormData {

    @SerializedName("name")
    @Expose
    var name: String? = null

    @SerializedName("valueReference")
    @Expose
    var valueReference: String? = null

    @SerializedName("dataType")
    @Expose
    var dataType: String? = null

}