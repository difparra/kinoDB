package com.diegoparra.kinodb.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.diegoparra.kinodb.data.local.entities.FavouriteEntity
import com.diegoparra.kinodb.data.local.entities.GenreEntity
import com.diegoparra.kinodb.data.local.entities.GenreMovieCrossRef
import com.diegoparra.kinodb.data.local.entities.MovieEntity

@Database(
    entities = [
        MovieEntity::class, GenreEntity::class, GenreMovieCrossRef::class, FavouriteEntity::class
    ], version = 1, exportSchema = false
)
@TypeConverters(Converters::class)
abstract class KinoDatabase : RoomDatabase() {

    companion object {
        const val DB_NAME = "com.diegoparra.kinodb.kinodb"
    }

    abstract fun moviesDao(): MoviesDao
    abstract fun favouritesDao(): FavouritesDao

}