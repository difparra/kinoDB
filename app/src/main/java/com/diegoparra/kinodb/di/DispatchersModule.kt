package com.diegoparra.kinodb.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import javax.inject.Qualifier

@Module
@InstallIn(SingletonComponent::class)
object DispatchersModule {

    @Provides
    @IoDispatcher
    fun providesIoDispatcher() = Dispatchers.IO

}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class IoDispatcher