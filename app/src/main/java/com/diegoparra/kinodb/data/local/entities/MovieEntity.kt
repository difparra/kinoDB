package com.diegoparra.kinodb.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.diegoparra.kinodb.utils.removeCaseAndAccents
import java.time.LocalDate
import java.util.Locale

@Entity(tableName = "Movie")
data class MovieEntity(
    @PrimaryKey val movieId: String,
    val posterUrl: String,
    val backdropUrl: String,
    val title: String,
    val normalisedTitle: String = title.removeCaseAndAccents(),
    val overview: String,
    val language: Locale?,
    val releaseDate: LocalDate?,
    val voteAverage: Int?,
    val popularity: Double,
    val runtimeMinutes: Int?,
    val homepageUrl: String?
)