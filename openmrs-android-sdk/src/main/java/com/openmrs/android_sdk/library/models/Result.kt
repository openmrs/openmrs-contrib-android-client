package com.openmrs.android_sdk.library.models

sealed class Result<out T> {
    data class Success<out T>(val data: T, val operationType: OperationType = OperationType.GeneralOperation) : Result<T>()
    data class Error(val throwable: Throwable, val operationType: OperationType = OperationType.GeneralOperation) : Result<Nothing>()
    class Loading(val operationType: OperationType = OperationType.GeneralOperation) : Result<Nothing>()
}

enum class OperationType {
    GeneralOperation,
    PatientRegistering,
    PatientMerging,
    ActiveVisitsFetching,
    ActiveVisitsSearching,
    PatientFetching,
    PatientSynchronizing,
    PatientVisitsFetching,
    PatientVisitStarting,
    PatientAllergyFetching,
    PatientVitalsFetching,
    PatientDeleting
}

enum class ResultType {
    AllergyDeletionSuccess,
    AllergyDeletionLocalSuccess,
    AllergyDeletionError
}
