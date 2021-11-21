package com.diegoparra.kinodb.di

import android.content.Context
import androidx.room.Room
import com.diegoparra.kinodb.data.local.KinoDatabase
import com.diegoparra.kinodb.data.local.MoviesDao
import com.diegoparra.kinodb.data.local.MoviesEntityMappers
import com.diegoparra.kinodb.data.local.MoviesEntityMappersImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RoomModule {

    @Singleton
    @Provides
    fun providesKinoDatabase(@ApplicationContext appContext: Context): KinoDatabase {
        return Room
            .databaseBuilder(appContext, KinoDatabase::class.java, KinoDatabase.DB_NAME)
            .build()
    }

    @Singleton
    @Provides
    fun providesMoviesDao(kinoDatabase: KinoDatabase): MoviesDao {
        return kinoDatabase.moviesDao()
    }

    @Provides
    fun providesMovieEntityMappers(): MoviesEntityMappers {
        return MoviesEntityMappersImpl
    }

}