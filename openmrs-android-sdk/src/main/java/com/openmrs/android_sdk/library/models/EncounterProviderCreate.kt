package com.openmrs.android_sdk.library.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Encounter provider create
 *
 * @property providerUUID
 * @property encounterRoleUUID
 * @constructor Create empty Encounter provider create
 */
class EncounterProviderCreate(
        @field:Expose
        @field:SerializedName("provider")
        var providerUUID: String,

        @field:Expose
        @field:SerializedName("encounterRole")
        var encounterRoleUUID: String)