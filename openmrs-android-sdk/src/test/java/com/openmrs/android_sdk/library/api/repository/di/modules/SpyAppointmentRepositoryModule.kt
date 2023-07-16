package com.openmrs.android_sdk.library.api.repository.di.modules

import com.openmrs.android_sdk.library.api.repository.AppointmentRepository
import com.openmrs.android_sdk.library.api.repository.BaseRepository
import com.openmrs.android_sdk.library.di.modules.ApplicationContextModule
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import io.mockk.mockk
import io.mockk.spyk
import org.bouncycastle.jcajce.provider.symmetric.ARC4.Base
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SpyAppointmentRepositoryModule {

    @Provides
    @Singleton
    fun provideAppointmentRepository(): BaseRepository = BaseRepository()
}