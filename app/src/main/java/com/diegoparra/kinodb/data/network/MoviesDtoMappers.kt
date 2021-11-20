package com.diegoparra.kinodb.data.network

import com.diegoparra.kinodb.data.network.dtos.GenresResponse
import com.diegoparra.kinodb.data.network.dtos.MovieListResponse
import com.diegoparra.kinodb.data.network.dtos.MovieResponse
import com.diegoparra.kinodb.models.Genre
import com.diegoparra.kinodb.models.Movie

interface MoviesDtoMappers {

    fun toGenreList(genresResponse: GenresResponse): List<Genre>
    fun toMovieList(movieListResponse: MovieListResponse): List<Movie>
    fun toMovie(movieResponse: MovieResponse): Movie

}