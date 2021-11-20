package com.diegoparra.kinodb.data

import com.diegoparra.kinodb.data.network.MoviesApi
import com.diegoparra.kinodb.data.network.MoviesDtoMappers
import com.diegoparra.kinodb.di.IoDispatcher
import com.diegoparra.kinodb.models.Genre
import com.diegoparra.kinodb.models.Movie
import com.diegoparra.kinodb.utils.Either
import com.diegoparra.kinodb.utils.runCatching
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class MoviesRepositoryImpl @Inject constructor(
    private val api: MoviesApi,
    private val dtoMappers: MoviesDtoMappers,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) : MoviesRepository {

    override suspend fun getGenres(): Either<Exception, List<Genre>> = withContext(dispatcher) {
        Timber.d("getGenres() called")
        Either.runCatching {
            val genreDtos = api.getGenres()
            dtoMappers.toGenreList(genreDtos)
                .also { Timber.d("returning from getGenres():\n ${it.joinToString("\n")}") }
        }
    }

    override suspend fun getMoviesByGenre(genreId: String): Either<Exception, List<Movie>> =
        withContext(dispatcher) {
            Timber.d("getMoviesByGenre called with genreId = $genreId")
            Either.runCatching {
                val movieDtos = api.getMoviesByGenre(genreId)
                dtoMappers.toMovieList(movieDtos)
                    .also {
                        Timber.d(
                            "returning from getMoviesByGenre($genreId):\n ${
                                it.joinToString(
                                    "\n"
                                )
                            }"
                        )
                    }
            }
        }

    override suspend fun getMovieById(movieId: String): Either<Exception, Movie> =
        withContext(dispatcher) {
            Timber.d("getMovieById called with movieId = $movieId")
            Either.runCatching {
                val movieDto = api.getMovieById(movieId)
                dtoMappers.toMovie(movieDto)
                    .also { Timber.d("returning from getMovieById($movieId):\n $it") }
            }
        }

    override suspend fun searchMovieByName(title: String): Either<Exception, List<Movie>> =
        withContext(dispatcher) {
            Either.runCatching {
                Timber.d("searchMovieByName called with title = $title")
                val movieDtos = api.searchMovieByName(title)
                dtoMappers.toMovieList(movieDtos)
                    .also {
                        Timber.d("returning from searchMovieByName($title):\n ${it.joinToString("\n")}")
                    }
            }
        }

}