package com.openmrs.android_sdk.library.models

data class Drug(
    var uuid: String?,
    var display: String?,
    var description: String?,
    var combination: Boolean?,
    var maximumDailyDose: Int?,
    var minimumDailyDose: Int?,
    var concept: String?,
    var dosageForm: DosageForm?,
    var drugReferenceMaps: List<Any>?,
    var ingredients: List<Any>?,
    var name: String?,
    var retired: Boolean?,
    var strength: String?,
    var resourceVersion: String?
)