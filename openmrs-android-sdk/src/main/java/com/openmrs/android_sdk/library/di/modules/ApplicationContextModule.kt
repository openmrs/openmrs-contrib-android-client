package com.openmrs.android_sdk.library.di.modules

import android.content.Context
import com.openmrs.android_sdk.library.OpenmrsAndroid
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApplicationContextModule {

    @Provides
    @Singleton
    fun provideApplicationContext(): Context = OpenmrsAndroid.getInstance()!!.applicationContext
}
