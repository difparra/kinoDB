package com.diegoparra.kinodb.data

import com.diegoparra.kinodb.data.local.entities.GenreEntity
import com.diegoparra.kinodb.data.local.entities.MovieEntity
import com.diegoparra.kinodb.data.local.entities.MovieWithGenresDb
import com.diegoparra.kinodb.data.network.dtos.GenresResponse
import com.diegoparra.kinodb.data.network.dtos.MovieListResponse
import com.diegoparra.kinodb.data.network.dtos.MovieResponse

interface MoviesDtoToEntityMappers {

    fun toGenreEntityList(genresResponse: GenresResponse): List<GenreEntity>
    fun toMovieWithGenresList(movieListResponse: MovieListResponse): List<MovieWithGenresDb>
    fun toMovieWithGenresDb(movieResponse: MovieResponse): MovieWithGenresDb
/*
    fun toGenreList(genresResponse: GenresResponse): List<Genre>
    fun toMovieList(movieListResponse: MovieListResponse): List<Movie>
    fun toMovie(movieResponse: MovieResponse): Movie
     */

}