package com.openmrs.android_sdk.library.api.repository.di.modules

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.openmrs.android_sdk.library.dao.AppointmentRoomDAO
import com.openmrs.android_sdk.library.databases.AppDatabase
import com.openmrs.android_sdk.library.di.modules.ApplicationContextModule
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import io.mockk.mockk
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [ApplicationContextModule::class]
)
object FakeApplicationContextModule {
    @Provides
    @Singleton
    fun provideApplicationContext(): Context = ApplicationProvider.getApplicationContext<Context>()
}