package com.example.openmrs_android_sdk.library.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

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