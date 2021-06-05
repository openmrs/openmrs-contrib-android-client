package com.example.openmrs_android_sdk.library.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class EncounterProviderCreate(
        @field:Expose
        @field:SerializedName("provider")
        var providerUUID: String,

        @field:Expose
        @field:SerializedName("encounterRole")
        var encounterRoleUUID: String)