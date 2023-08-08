package com.openmrs.android_sdk.library.models

data class DrugCreate(
    var combination: Boolean,
    var concept: String,
    var dosageForm: String,
    var maximumDailyDose: Int,
    var minimumDailyDose: Int,
    var name: String
)