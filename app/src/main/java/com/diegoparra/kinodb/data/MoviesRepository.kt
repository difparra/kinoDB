package com.diegoparra.kinodb.data

import com.diegoparra.kinodb.models.Genre
import com.diegoparra.kinodb.models.Movie
import com.diegoparra.kinodb.utils.Either

interface MoviesRepository {

    suspend fun getGenres(): Either<Exception, List<Genre>>
    suspend fun getMoviesByGenre(genreId: String): Either<Exception, List<Movie>>
    suspend fun getMovieById(movieId: String): Either<Exception, Movie>
    suspend fun searchMovieByName(title: String): Either<Exception, List<Movie>>

}