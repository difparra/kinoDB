package com.diegoparra.kinodb.data

import com.diegoparra.kinodb.data.local.FavouritesDao
import com.diegoparra.kinodb.data.local.MoviesDao
import com.diegoparra.kinodb.data.local.MoviesEntityMappers
import com.diegoparra.kinodb.data.network.MoviesApi
import com.diegoparra.kinodb.data.network.MoviesDtoMappers
import com.diegoparra.kinodb.di.IoDispatcher
import com.diegoparra.kinodb.models.Genre
import com.diegoparra.kinodb.models.Movie
import com.diegoparra.kinodb.utils.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject

class MoviesRepositoryImpl @Inject constructor(
    private val api: MoviesApi,
    private val dtoMappers: MoviesDtoMappers,
    private val dao: MoviesDao,
    private val entityMappers: MoviesEntityMappers,
    private val dtoToEntityMappers: MoviesDtoToEntityMappers,
    private val favouritesDao: FavouritesDao,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) : MoviesRepository {

    override suspend fun getGenres(): Either<Exception, Data<List<Genre>>> =
        callApiUpdateLocalAndReturnData(
            methodName = "getGenres",
            param = Unit,
            apiData = { api.getGenres() },
            dtoMapper = dtoMappers::toGenreList,
            daoData = { dao.getGenres() },
            entityMapper = entityMappers::toGenreList,
            updateLocal = {
                val genresDb = dtoToEntityMappers.toGenreEntityList(it)
                dao.insertOrUpdateAllGenres(genresDb)
            }
        )


    override suspend fun getMoviesByGenre(genreId: String): Either<Exception, Data<List<Movie>>> =
        callApiUpdateLocalAndReturnData(
            methodName = "getMoviesByGenre",
            param = genreId,
            apiData = api::getMoviesByGenre,
            dtoMapper = dtoMappers::toMovieList,
            daoData = dao::getMoviesByGenre,
            entityMapper = entityMappers::toMovieList,
            updateLocal = {
                val moviesDb = dtoToEntityMappers.toMovieWithGenresList(it)
                dao.insertAllMovieWithGenres(moviesDb)
            }
        )


    override suspend fun getMovieById(movieId: String): Either<Exception, Data<Movie>> =
        callApiUpdateLocalAndReturnData(
            methodName = "getMovieById",
            param = movieId,
            apiData = api::getMovieById,
            dtoMapper = dtoMappers::toMovie,
            daoData = dao::getMovieById,
            entityMapper = entityMappers::toMovie,
            updateLocal = {
                val movieDb = dtoToEntityMappers.toMovieWithGenresDb(it)
                dao.insertMovieWithGenres(movieDb)
            }
        )


    override suspend fun searchMovieByName(title: String): Either<Exception, Data<List<Movie>>> =
        callApiUpdateLocalAndReturnData(
            methodName = "searchMovieByName",
            param = title,
            apiData = api::searchMovieByName,
            dtoMapper = dtoMappers::toMovieList,
            daoData = dao::searchMovieByName,
            entityMapper = entityMappers::toMovieList,
            updateLocal = {
                val moviesDb = dtoToEntityMappers.toMovieWithGenresList(it)
                dao.insertAllMovieWithGenres(moviesDb)
            },
            returnOnFailure = { localData, e ->
                Either.Right(Data.fromLocal(localData ?: emptyList()))
            }
        )


    override suspend fun toggleFavourite(movieId: String) = withContext(dispatcher) {
        favouritesDao.isFavourite(movieId).let { isFavourite ->
            if (isFavourite) {
                favouritesDao.removeFavourite(movieId)
            } else {
                favouritesDao.addFavourite(movieId)
            }
        }
    }

    override fun isFavourite(movieId: String): Flow<Either<Exception, Boolean>> {
        return favouritesDao.observeIsFavourite(movieId)
            .map { Either.runCatching { it } }
            .flowOn(dispatcher)
    }

    override fun getFavourites(): Flow<Either<Exception, List<Movie>>> {
        return favouritesDao.observeFavouritesIds()
            .map {
                coroutineScope {
                    val moviesAsyncList = it.map { movieId ->
                        async { getMovieById(movieId).map { it.content } }
                    }
                    val movies = moviesAsyncList.awaitAll()
                    movies.reduceFailuresOrRight()
                }
            }
            .flowOn(dispatcher)
    }


    //      ----------      Util functions

    private suspend fun <P, Dto, Entity, T> callApiUpdateLocalAndReturnData(
        methodName: String,
        param: P,
        apiData: suspend (P) -> Dto,
        dtoMapper: (Dto) -> T,
        daoData: suspend (P) -> Entity?,
        entityMapper: (Entity) -> T,
        updateLocal: suspend (Dto) -> Unit,
        returnOnSuccess: (serverData: T) -> Either<Exception, Data<T>> = {
            Either.Right(Data.fromServer(it))
        },
        returnOnFailure: (localData: T?, Exception) -> Either<Exception, Data<T>> = { localData, e ->
            if (localData == null || (localData is List<*> && localData.isEmpty())) {
                Either.Left(e)
            } else {
                Either.Right(Data.fromLocal(localData))
            }
        }
    ): Either<Exception, Data<T>> = withContext(dispatcher) {
        Timber.d("$methodName called!. param = $param")
        Either.runCatching { apiData(param) }
            .onSuccess {
                Timber.d("$methodName. Data successfully collected from api. Saving data locally.")
                updateLocal(it)
            }
            .fold(
                onSuccess = {
                    val serverData = dtoMapper(it)
                    returnOnSuccess(serverData)
                },
                onFailure = {
                    Timber.d("$methodName. Exception while getting data from api. ${it.getLogMessage()}")
                    val localData = daoData(param)?.let(entityMapper)
                    returnOnFailure(localData, it)
                }
            )
            .logResult(
                onSuccess = {
                    "returning from $methodName($param). Source = ${it.source}," +
                            "content = ${it.content.let { if (it is List<*>) it.joinToString("\n") else it }}"
                },
                onFailure = { "exception from $methodName($param):\n ${it.getLogMessage()}" }
            )
    }

    private fun <L, R> Either<L, R>.logResult(
        onSuccess: (R) -> String,
        onFailure: (L) -> String
    ): Either<L, R> {
        return this.onSuccess { Timber.d(onSuccess(it)) }
            .onFailure { Timber.e(onFailure(it)) }
    }

}