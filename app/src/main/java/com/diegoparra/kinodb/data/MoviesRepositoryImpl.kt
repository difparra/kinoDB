package com.diegoparra.kinodb.data

import com.diegoparra.kinodb.data.network.MoviesApi
import com.diegoparra.kinodb.data.network.MoviesDtoMappers
import com.diegoparra.kinodb.di.IoDispatcher
import com.diegoparra.kinodb.models.Genre
import com.diegoparra.kinodb.models.Movie
import com.diegoparra.kinodb.utils.*
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
        Either
            .runCatching {
                val genreDtos = api.getGenres()
                dtoMappers.toGenreList(genreDtos)
            }
            .logResult(
                onSuccess = { "returning from getGenres():\n ${it.joinToString("\n")}" },
                onFailure = { "exception from getGenres():\n ${it.getLogMessage()}" }
            )
    }

    override suspend fun getMoviesByGenre(genreId: String): Either<Exception, List<Movie>> =
        withContext(dispatcher) {
            Timber.d("getMoviesByGenre called with genreId = $genreId")
            Either
                .runCatching {
                    val movieDtos = api.getMoviesByGenre(genreId)
                    dtoMappers.toMovieList(movieDtos)
                }
                .logResult(
                    onSuccess = { "returning from getMoviesByGenre($genreId):\n ${it.joinToString("\n")}" },
                    onFailure = { "exception from getMoviesByGenre($genreId):\n ${it.getLogMessage()}" }
                )
        }

    override suspend fun getMovieById(movieId: String): Either<Exception, Movie> =
        withContext(dispatcher) {
            Timber.d("getMovieById called with movieId = $movieId")
            Either
                .runCatching {
                    val movieDto = api.getMovieById(movieId)
                    dtoMappers.toMovie(movieDto)
                }
                .logResult(
                    onSuccess = { "returning from getMovieById($movieId):\n $it" },
                    onFailure = { "exception from getMovieById($movieId):\n ${it.getLogMessage()}" }
                )
        }

    override suspend fun searchMovieByName(title: String): Either<Exception, List<Movie>> =
        withContext(dispatcher) {
            Either
                .runCatching {
                    Timber.d("searchMovieByName called with title = $title")
                    val movieDtos = api.searchMovieByName(title)
                    dtoMappers.toMovieList(movieDtos)
                }
                .logResult(
                    onSuccess = { "returning from searchMovieByName($title):\n ${it.joinToString("\n")}" },
                    onFailure = { "exception from searchMovieByName($title):\n ${it.getLogMessage()}" }
                )
        }


    //      ----------      Util functions to log result

    private fun <L, R> Either<L, R>.logResult(
        onSuccess: (R) -> String,
        onFailure: (L) -> String
    ): Either<L, R> {
        return this.onSuccess { Timber.d(onSuccess(it)) }
            .onFailure { Timber.e(onFailure(it)) }
    }

}