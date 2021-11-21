package com.diegoparra.kinodb.data

import com.diegoparra.kinodb.data.local.FavouritesDao
import com.diegoparra.kinodb.data.local.MoviesDao
import com.diegoparra.kinodb.data.local.MoviesEntityMappersImpl
import com.diegoparra.kinodb.data.local.entities.GenreEntity
import com.diegoparra.kinodb.data.local.entities.MovieEntity
import com.diegoparra.kinodb.data.network.MoviesApi
import com.diegoparra.kinodb.data.network.MoviesDtoMappersImpl
import com.diegoparra.kinodb.data.network.dtos.*
import com.diegoparra.kinodb.utils.Data
import com.diegoparra.kinodb.utils.Either
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class MoviesRepositoryImplTest {

    private val testDispatcher = TestCoroutineDispatcher()

    @Mock
    private lateinit var moviesApi: MoviesApi

    @Mock
    private lateinit var moviesDao: MoviesDao

    @Mock
    private lateinit var favouritesDao: FavouritesDao

    private val dtoMappers = MoviesDtoMappersImpl
    private val entityMappers = MoviesEntityMappersImpl
    private val dtoToEntityMappers = MoviesDtoToEntityMappersImpl

    private lateinit var moviesRepository: MoviesRepositoryImpl


    //      ----------  Sample data

    private val genreDto1 = GenreDto("1", "Action")
    private val genreDto2 = GenreDto("2", "Comedy")

    private val movieListItemDto1 = MovieListItemDto(
        "1", false, listOf(genreDto1, genreDto2).map { it.id },
        "/backdrop.com", "/poster.com",
        "The Suicide Squad", "The Suicide Squad",
        "en", "Overview", "2021-11-21",
        0.0, 0, 9.0f, false
    )
    private val movieListItemDto2 = MovieListItemDto(
        "2", false, listOf(genreDto1, genreDto2).map { it.id },
        "/backdrop.com", "/poster.com",
        "Jungle Cruise", "Jungle Cruise",
        "en", "Overview", "2021-11-21",
        0.0, 0, 9.0f, false
    )
    private val movieDto1 = MovieResponse(
        "1", "1", false, 1000, listOf(genreDto1, genreDto2),
        "/homePage.com", "/backdrop.com", "/poster.com",
        "The Suicide Squad", "The Suicide Squad",
        "en", "Overview", "2021-11-21",
        0.0, 0, 9.0f, 120, null, "Tagline", false
    )
    private val movieDto2 = MovieResponse(
        "2", "2", false, 1000, listOf(genreDto1, genreDto2),
        "/homePage.com", "/backdrop.com", "/poster.com",
        "Jungle Cruise", "Jungle Cruise",
        "en", "Overview", "2021-11-21",
        0.0, 0, 9.0f, 120, null, "Tagline", false
    )


    //      ----------      setUp and tearDown methods

    @Before
    fun setUp() {
        moviesRepository = MoviesRepositoryImpl(
            moviesApi,
            dtoMappers,
            moviesDao,
            entityMappers,
            dtoToEntityMappers,
            favouritesDao,
            testDispatcher
        )
    }

    @After
    fun tearDown() = testDispatcher.cleanupTestCoroutines()


    @Test
    fun getGenres_apiSuccess() = testDispatcher.runBlockingTest {
        val genresResponse = GenresResponse(listOf(genreDto1, genreDto2))
        Mockito.`when`(moviesApi.getGenres()).thenReturn(genresResponse)

        val result = moviesRepository.getGenres()

        assertThat(result).isInstanceOf(Either.Right::class.java)
        assertThat(result.getOrNull())
            .isEqualTo(Data.fromServer(dtoMappers.toGenreList(genresResponse)))
        //  Verify local data was updated
        Mockito.verify(moviesDao)
            .insertOrUpdateAllGenres(dtoToEntityMappers.toGenreEntityList(genresResponse))
    }

    @Test
    fun getGenres_apiFailure_noLocalData() = testDispatcher.runBlockingTest {
        val exception = NullPointerException()
        Mockito.`when`(moviesApi.getGenres()).thenThrow(exception)
        Mockito.`when`(moviesDao.getGenres()).thenReturn(emptyList())

        val result = moviesRepository.getGenres()

        assertThat(result).isInstanceOf(Either.Left::class.java)
        assertThat(result.leftOrNull()).isEqualTo(exception)
    }

    @Test
    fun getGenres_apiFailure_existLocalData() = testDispatcher.runBlockingTest {
        val exception = NullPointerException()
        Mockito.`when`(moviesApi.getGenres()).thenThrow(exception)
        val genre1 = GenreEntity("1", "Action")
        val genre2 = GenreEntity("2", "Comedy")
        Mockito.`when`(moviesDao.getGenres()).thenReturn(listOf(genre1, genre2))

        val result = moviesRepository.getGenres()

        assertThat(result).isInstanceOf(Either.Right::class.java)
        assertThat(result.getOrNull()).isEqualTo(
            Data.fromLocal(entityMappers.toGenreList(listOf(genre1, genre2)))
        )
    }

    @Test
    fun getMoviesByGenre_apiSuccess() = testDispatcher.runBlockingTest {
        val genreId = "1"
        val moviesListResponse = MovieListResponse(
            page = 1, results = listOf(movieListItemDto1, movieListItemDto2),
            totalResults = 20, totalPages = 1
        )
        Mockito.`when`(moviesApi.getMoviesByGenre(genreId)).thenReturn(moviesListResponse)

        val result = moviesRepository.getMoviesByGenre(genreId)

        assertThat(result).isInstanceOf(Either.Right::class.java)
        assertThat(result.getOrNull())
            .isEqualTo(Data.fromServer(dtoMappers.toMovieList(moviesListResponse)))
        Mockito.verify(moviesDao)
            .insertAllMovieWithGenres(dtoToEntityMappers.toMovieWithGenresList(moviesListResponse))
    }

    @Test
    fun getMovieById_apiSuccess() = testDispatcher.runBlockingTest {
        val movieId = movieListItemDto1.id
        val movieResponse = movieDto1
        Mockito.`when`(moviesApi.getMovieById(movieId)).thenReturn(movieResponse)

        val result = moviesRepository.getMovieById(movieId)

        assertThat(result).isInstanceOf(Either.Right::class.java)
        assertThat(result.getOrNull())
            .isEqualTo(Data.fromServer(dtoMappers.toMovie(movieResponse)))
        Mockito.verify(moviesDao)
            .insertMovieWithGenres(dtoToEntityMappers.toMovieWithGenresDb(movieResponse))
    }

    @Test
    fun searchMovieByName_apiSuccess() = testDispatcher.runBlockingTest {
        val query = "abc"
        val moviesListResponse = MovieListResponse(
            page = 1, results = listOf(movieListItemDto1, movieListItemDto2),
            totalResults = 20, totalPages = 1
        )
        Mockito.`when`(moviesApi.searchMovieByName(query)).thenReturn(moviesListResponse)

        val result = moviesRepository.searchMovieByName(query)

        assertThat(result).isInstanceOf(Either.Right::class.java)
        assertThat(result.getOrNull())
            .isEqualTo(Data.fromServer(dtoMappers.toMovieList(moviesListResponse)))
        Mockito.verify(moviesDao)
            .insertAllMovieWithGenres(dtoToEntityMappers.toMovieWithGenresList(moviesListResponse))
    }


}