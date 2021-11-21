package com.diegoparra.kinodb.ui.home

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.diegoparra.kinodb.data.MoviesRepository
import com.diegoparra.kinodb.models.Genre
import com.diegoparra.kinodb.models.GenreWithMovies
import com.diegoparra.kinodb.models.Movie
import com.diegoparra.kinodb.test_utils.MainCoroutineRule
import com.diegoparra.kinodb.test_utils.getOrAwaitValue
import com.diegoparra.kinodb.utils.Data
import com.diegoparra.kinodb.utils.Either
import com.diegoparra.kinodb.utils.Resource
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import java.time.LocalDate
import java.util.*

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class HomeViewModelTest {

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var moviesRepository: MoviesRepository

    private lateinit var homeViewModel: HomeViewModel


    private val genre1 = Genre("1", "Action")
    private val genre2 = Genre("2", "Comedy")
    private val movie1 = Movie(
        "1", listOf(genre1, genre2), "/backdrop.com", "/poster.com",
        "The Suicide Squad", "Overview", Locale.forLanguageTag("en"),
        LocalDate.of(2021, 11, 21), 90, 120,
        "/homepageUrl.com"
    )
    private val movie2 = Movie(
        "2", listOf(genre1), "/backdrop.com", "/poster.com",
        "The Suicide Squad", "Overview", Locale.forLanguageTag("en"),
        LocalDate.of(2021, 11, 21), 90, 120,
        "/homepageUrl.com"
    )


    @Test
    fun genreAndMovies_success() = mainCoroutineRule.runBlockingTest {
        val genres = listOf(genre1, genre2)
        val moviesGenre1 = listOf(movie1, movie2)
        val moviesGenre2 = listOf(movie1)

        Mockito.`when`(moviesRepository.getGenres())
            .thenReturn(Either.Right(Data.fromServer(genres)))
        Mockito.`when`(moviesRepository.getMoviesByGenre(genre1.id))
            .thenReturn(Either.Right(Data.fromServer(moviesGenre1)))
        Mockito.`when`(moviesRepository.getMoviesByGenre(genre2.id))
            .thenReturn(Either.Right(Data.fromServer(moviesGenre2)))
        homeViewModel = HomeViewModel(moviesRepository)

        val data =
            listOf(GenreWithMovies(genre1, moviesGenre1), GenreWithMovies(genre2, moviesGenre2))
        assertThat(homeViewModel.genresAndMovies.getOrAwaitValue())
            .isEqualTo(Resource.Success(data))
    }

    @Test
    fun genreAndMovies_failureFromApiAndNoLocalData() = mainCoroutineRule.runBlockingTest {
        val exception = NullPointerException()
        Mockito.`when`(moviesRepository.getGenres()).thenReturn(Either.Left(exception))
        homeViewModel = HomeViewModel(moviesRepository)

        assertThat(homeViewModel.genresAndMovies.getOrAwaitValue())
            .isEqualTo(Resource.Error(exception))
    }

    @Test
    fun onMovieClick_setNavigateMovieDetails() = mainCoroutineRule.runBlockingTest {
        Mockito.`when`(moviesRepository.getGenres())
            .thenReturn(Either.Right(Data.fromServer(listOf(genre1, genre2))))
        Mockito.`when`(moviesRepository.getMoviesByGenre(genre1.id))
            .thenReturn(Either.Right(Data.fromServer(listOf(movie1, movie2))))
        Mockito.`when`(moviesRepository.getMoviesByGenre(genre2.id))
            .thenReturn(Either.Right(Data.fromServer(listOf(movie1))))
        homeViewModel = HomeViewModel(moviesRepository)

        val movieId = "1"
        homeViewModel.onMovieClick(movieId)
        assertThat(homeViewModel.navigateMovieDetails.getOrAwaitValue().peekContent())
            .isEqualTo(movieId)
    }

}