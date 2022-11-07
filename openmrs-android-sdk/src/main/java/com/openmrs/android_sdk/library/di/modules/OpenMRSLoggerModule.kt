package com.openmrs.android_sdk.library.di.modules

import com.openmrs.android_sdk.library.OpenMRSLogger
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object OpenMRSLoggerModule {

    @Provides
    @Singleton
    fun provideLogger(): OpenMRSLogger = OpenMRSLogger()
}
