package org.openmrs.mobile.di

import com.openmrs.android_sdk.library.OpenMRSLogger
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class LoggerModule {

    @Provides
    fun provideLogger() = OpenMRSLogger()
}
