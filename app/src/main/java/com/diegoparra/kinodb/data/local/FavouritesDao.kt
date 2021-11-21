package com.diegoparra.kinodb.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.diegoparra.kinodb.data.local.entities.FavouriteEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import java.time.Instant

@Dao
abstract class FavouritesDao {

    @Query("Select movieId from Favourite")
    abstract fun getFavouritesIds(): List<String>

    @Query("Select movieId from Favourite")
    protected abstract fun _observeFavouritesIds(): Flow<List<String>>
    fun observeFavouritesIds(): Flow<List<String>> = _observeFavouritesIds().distinctUntilChanged()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    protected abstract suspend fun _addFavourite(favourite: FavouriteEntity)
    suspend fun addFavourite(movieId: String) {
        _addFavourite(FavouriteEntity(movieId = movieId, updatedAt = Instant.now()))
    }

    @Query("DELETE FROM Favourite WHERE movieId = :movieId")
    abstract suspend fun removeFavourite(movieId: String)

    @Query("SELECT EXISTS(SELECT * FROM Favourite WHERE movieId = :movieId)")
    protected abstract fun _observeIsFavourite(movieId: String): Flow<Boolean>
    fun observeIsFavourite(movieId: String): Flow<Boolean> =
        _observeIsFavourite(movieId).distinctUntilChanged()

    @Query("SELECT EXISTS(SELECT * FROM Favourite WHERE movieId = :movieId)")
    abstract fun isFavourite(movieId: String): Boolean

}