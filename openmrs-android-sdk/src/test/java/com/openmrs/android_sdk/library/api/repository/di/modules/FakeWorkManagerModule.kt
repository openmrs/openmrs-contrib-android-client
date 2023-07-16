package com.openmrs.android_sdk.library.api.repository.di.modules

import android.content.Context
import androidx.work.WorkManager
import com.openmrs.android_sdk.library.di.modules.WorkManagerModule
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
    replaces = [WorkManagerModule::class]
)
object FakeWorkManagerModule {
    @Provides
    @Singleton
    fun provideWorkManager(@ApplicationContext context: Context): WorkManager = mockk()
}