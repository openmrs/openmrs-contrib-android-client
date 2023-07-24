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