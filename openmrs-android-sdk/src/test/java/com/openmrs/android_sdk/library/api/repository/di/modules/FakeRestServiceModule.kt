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

import com.openmrs.android_sdk.library.api.RestApi
import com.openmrs.android_sdk.library.api.RestServiceBuilder
import com.openmrs.android_sdk.library.di.modules.ApplicationContextModule
import com.openmrs.android_sdk.library.di.modules.RestServiceModule
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import io.mockk.mockk
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [RestServiceModule::class]
)
object FakeRestServiceModule {
    @Provides
    @Singleton
    fun provideRestService(): RestApi = mockk()
}