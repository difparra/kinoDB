package com.diegoparra.kinodb.models

import androidx.annotation.IntRange
import java.time.LocalDate
import java.util.Locale

data class Movie(
    val id: String,
    val genres: List<Genre>,
    val posterPath: String,
    val backdropPath: String,
    val title: String,
    val overview: String,
    val language: Locale?,
    val releaseDate: LocalDate?,
    @IntRange(from = 0, to = 100) val voteAverage: Int?
)