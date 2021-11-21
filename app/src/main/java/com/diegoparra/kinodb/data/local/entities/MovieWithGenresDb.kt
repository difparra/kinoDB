package com.diegoparra.kinodb.data.local.entities

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class MovieWithGenresDb(
    @Embedded val movie: MovieEntity,
    @Relation(
        parentColumn = "movieId",
        entityColumn = "genreId",
        associateBy = Junction(GenreMovieCrossRef::class)
    )
    val genres: List<GenreEntity>
)