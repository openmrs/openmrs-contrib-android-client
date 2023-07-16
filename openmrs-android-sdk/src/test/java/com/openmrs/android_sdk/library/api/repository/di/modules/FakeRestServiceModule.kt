package com.openmrs.android_sdk.library.api.repository.di.modules

import com.openmrs.android_sdk.library.api.RestApi
import com.openmrs.android_sdk.library.api.RestServiceBuilder
import com.openmrs.android_sdk.library.di.modules.ApplicationContextModule
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
    replaces = [RestServiceModule::class]
)
object FakeRestServiceModule {
    @Provides
    @Singleton
    fun provideRestService(): RestApi = mockk()
}