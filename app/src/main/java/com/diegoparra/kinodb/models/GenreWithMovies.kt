package com.diegoparra.kinodb.models

data class GenreWithMovies(
    val genre: Genre,
    val movies: List<Movie>
)
