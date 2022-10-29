package com.openmrs.android_sdk.library.models

import com.openmrs.android_sdk.library.models.OperationType.GeneralOperation

sealed class Result<out T> {
    data class Success<out T>(val data: T, val operationType: OperationType = GeneralOperation) : Result<T>()
    data class Error(val throwable: Throwable, val operationType: OperationType = GeneralOperation) : Result<Nothing>()
    class Loading(val operationType: OperationType = GeneralOperation) : Result<Nothing>()
}

enum class OperationType {
    GeneralOperation,
    PatientRegistering,
    PatientMerging,
    ActiveVisitsFetching,
    ActiveVisitsSearching,
    PatientFetching,
    PatientSearching,
    PatientSynchronizing,
    PatientVisitsFetching,
    PatientVisitStarting,
    PatientAllergyFetching,
    PatientVitalsFetching,
    PatientDeleting,
    LastViewedPatientsFetching,
    ProviderRegistering,
    ProviderUpdating,
    Login,
    LocationsFetching
}

enum class ResultType {
    AllergyDeletionSuccess,
    AllergyDeletionLocalSuccess,
    AllergyDeletionError,
    PatientUpdateSuccess,
    PatientUpdateLocalSuccess,
    PatientUpdateError,
    EncounterSubmissionSuccess,
    EncounterSubmissionLocalSuccess,
    EncounterSubmissionError,
    AddProviderSuccess,
    AddProviderLocalSuccess,
    UpdateProviderSuccess,
    UpdateProviderLocalSuccess,
    ProviderDeletionSuccess,
    ProviderDeletionLocalSuccess,
    ProviderDeletionError,
    LoginOfflineSuccess,
    LoginSuccess,
    LoginInvalidCredentials,
    LoginOfflineUnsupported,
    LoginNoInternetConnection,
    LocationsFetchingLocalSuccess,
    LocationsFetchingSuccess,
    LocationsFetchingNoInternetConnection
}
