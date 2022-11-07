package com.openmrs.android_sdk.library.di.entrypoints

import com.openmrs.android_sdk.library.api.repository.FormRepository
import com.openmrs.android_sdk.library.api.repository.PatientRepository
import com.openmrs.android_sdk.library.api.repository.VisitRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface RepositoryEntryPoint {
    fun provideFormRepository(): FormRepository
    fun providePatientRepository(): PatientRepository
    fun provideVisitRepository(): VisitRepository
}
