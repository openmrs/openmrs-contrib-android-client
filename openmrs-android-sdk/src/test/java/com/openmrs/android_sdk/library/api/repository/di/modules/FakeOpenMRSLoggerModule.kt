package com.openmrs.android_sdk.library.api.repository.di.modules

import com.openmrs.android_sdk.library.OpenMRSLogger
import com.openmrs.android_sdk.library.di.modules.OpenMRSLoggerModule
import com.openmrs.android_sdk.library.di.modules.RestServiceModule
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import io.mockk.mockk
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [OpenMRSLoggerModule::class]
)
object FakeOpenMRSLoggerModule {
    @Provides
    @Singleton
    fun provideLogger(): OpenMRSLogger = mockk()
}