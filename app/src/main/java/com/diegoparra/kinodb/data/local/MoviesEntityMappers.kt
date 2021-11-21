package com.diegoparra.kinodb.data.local

import com.diegoparra.kinodb.data.local.entities.GenreEntity
import com.diegoparra.kinodb.data.local.entities.MovieEntity
import com.diegoparra.kinodb.data.local.entities.MovieWithGenresDb
import com.diegoparra.kinodb.models.Genre
import com.diegoparra.kinodb.models.Movie

interface MoviesEntityMappers {

    fun toGenreList(genresDb: List<GenreEntity>): List<Genre>
    fun toMovieList(movies: List<MovieEntity>): List<Movie>
    fun toMovie(movieWithGenresDb: MovieWithGenresDb): Movie

}