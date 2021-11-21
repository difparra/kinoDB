package com.diegoparra.kinodb.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Genre")
data class GenreEntity(
    @PrimaryKey val genreId: String,
    val name: String
)