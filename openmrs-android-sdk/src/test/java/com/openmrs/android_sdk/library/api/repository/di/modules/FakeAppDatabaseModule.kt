/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */

package com.openmrs.android_sdk.library.api.repository.di.modules

import android.content.Context
import com.openmrs.android_sdk.library.api.repository.VisitRepository
import com.openmrs.android_sdk.library.dao.AllergyRoomDAO
import com.openmrs.android_sdk.library.dao.AppointmentRoomDAO
import com.openmrs.android_sdk.library.dao.ConceptRoomDAO
import com.openmrs.android_sdk.library.dao.EncounterCreateRoomDAO
import com.openmrs.android_sdk.library.dao.EncounterDAO
import com.openmrs.android_sdk.library.dao.ProviderRoomDAO
import com.openmrs.android_sdk.library.databases.AppDatabase
import com.openmrs.android_sdk.library.di.modules.AppDatabaseModule
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
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
    fun provideFakeAppDatabase(@ApplicationContext context: Context): AppDatabase {
        val encounterCreateRoomDAO: EncounterCreateRoomDAO = mockk()
        val appDatabase: AppDatabase = mockk()
        every { appDatabase.encounterCreateRoomDAO() } returns encounterCreateRoomDAO
        every { encounterCreateRoomDAO.updateExistingEncounter(any()) } just Runs
        every { encounterCreateRoomDAO.getCreatedEncountersByID(any()) } returns mockk()


        return appDatabase
    }

    @Provides
    @Singleton
    fun provideAppointmentRoomDAO(@ApplicationContext context: Context): AppointmentRoomDAO = mockk()
}