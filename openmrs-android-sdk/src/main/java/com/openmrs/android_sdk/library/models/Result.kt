package com.openmrs.android_sdk.library.models

sealed class Result<out T> {
    data class Success<out T>(val data: T, val operationType: OperationType) : Result<T>()
    data class Error<out T>(val throwable: Throwable, val operationType: OperationType) : Result<T>()
    class Loading<out T>(val operationType: OperationType) : Result<T>()
}

enum class OperationType {
    GeneralOperation, PatientRegistering, PatientMerging, ActiveVisitsFetching, ActiveVisitsSearching
}
