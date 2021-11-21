package com.diegoparra.kinodb.data.local.entities

import androidx.room.Entity

@Entity(primaryKeys = ["genreId", "movieId"])
data class GenreMovieCrossRef(
    val genreId: String,
    val movieId: String
)