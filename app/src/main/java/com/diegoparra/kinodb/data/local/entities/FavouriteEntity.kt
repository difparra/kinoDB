package com.diegoparra.kinodb.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant

@Entity(tableName = "Favourite")
data class FavouriteEntity(
    @PrimaryKey val movieId: String,
    val updatedAt: Instant
)