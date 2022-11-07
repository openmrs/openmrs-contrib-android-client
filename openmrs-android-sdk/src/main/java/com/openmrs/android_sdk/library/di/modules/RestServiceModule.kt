package com.openmrs.android_sdk.library.di.modules

import com.openmrs.android_sdk.library.api.RestApi
import com.openmrs.android_sdk.library.api.RestServiceBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RestServiceModule {

    @Provides
    @Singleton
    fun provideRestService(): RestApi = RestServiceBuilder.createService(RestApi::class.java)
}
