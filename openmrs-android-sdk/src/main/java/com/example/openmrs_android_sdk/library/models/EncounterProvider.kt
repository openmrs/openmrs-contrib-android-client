package com.example.openmrs_android_sdk.library.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class EncounterProvider : Resource() {
    @SerializedName("provider")
    @Expose
    var provider: Resource? = null
    @SerializedName("encounterRole")
    @Expose
    var encounterRole: Resource? = null

}