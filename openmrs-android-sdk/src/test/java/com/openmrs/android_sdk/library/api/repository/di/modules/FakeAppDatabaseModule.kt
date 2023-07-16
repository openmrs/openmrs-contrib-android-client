package com.openmrs.android_sdk.library.api.repository.di.modules

import android.content.Context
import com.openmrs.android_sdk.library.dao.AllergyRoomDAO
import com.openmrs.android_sdk.library.dao.AppointmentRoomDAO
import com.openmrs.android_sdk.library.dao.ConceptRoomDAO
import com.openmrs.android_sdk.library.dao.ProviderRoomDAO
import com.openmrs.android_sdk.library.databases.AppDatabase
import com.openmrs.android_sdk.library.di.modules.AppDatabaseModule
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
    replaces = [AppDatabaseModule::class]
)
object FakeAppDatabaseModule {

    @Provides
    @Singleton
    fun provideFakeAppDatabase(@ApplicationContext context: Context): AppDatabase = mockk()

    @Provides
    @Singleton
    fun provideAppointmentRoomDAO(@ApplicationContext context: Context): AppointmentRoomDAO = mockk()
}