package com.openmrs.android_sdk.library.di.modules

import android.content.Context
import com.openmrs.android_sdk.library.dao.AllergyRoomDAO
import com.openmrs.android_sdk.library.dao.AppointmentRoomDAO
import com.openmrs.android_sdk.library.dao.ConceptRoomDAO
import com.openmrs.android_sdk.library.dao.ProviderRoomDAO
import com.openmrs.android_sdk.library.databases.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppDatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase = AppDatabase.getDatabase(context)

    @Provides
    @Singleton
    fun provideProviderRoomDAO(@ApplicationContext context: Context): ProviderRoomDAO =
            AppDatabase.getDatabase(context).providerRoomDAO()

    @Provides
    @Singleton
    fun provideConceptRoomDAO(@ApplicationContext context: Context): ConceptRoomDAO =
            AppDatabase.getDatabase(context).conceptRoomDAO()

    @Provides
    @Singleton
    fun provideAllergyRoomDAO(@ApplicationContext context: Context): AllergyRoomDAO =
            AppDatabase.getDatabase(context).allergyRoomDAO()

    @Provides
    @Singleton
    fun provideAppointmentRoomDAO(@ApplicationContext context: Context): AppointmentRoomDAO =
        AppDatabase.getDatabase(context).appointmentRoomDAO()
}



