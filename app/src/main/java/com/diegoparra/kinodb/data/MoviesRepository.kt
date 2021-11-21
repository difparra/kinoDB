package com.diegoparra.kinodb.data

import com.diegoparra.kinodb.models.Genre
import com.diegoparra.kinodb.models.Movie
import com.diegoparra.kinodb.utils.Data
import com.diegoparra.kinodb.utils.Either

interface MoviesRepository {

    suspend fun getGenres(): Either<Exception, Data<List<Genre>>>
    suspend fun getMoviesByGenre(genreId: String): Either<Exception, Data<List<Movie>>>
    suspend fun getMovieById(movieId: String): Either<Exception, Data<Movie>>
    suspend fun searchMovieByName(title: String): Either<Exception, Data<List<Movie>>>

}