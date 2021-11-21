package com.diegoparra.kinodb.ui.movie_details

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import com.diegoparra.kinodb.data.MoviesRepository
import com.diegoparra.kinodb.models.Genre
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
class MovieDetailsViewModelTest {

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var moviesRepository: MoviesRepository

    private lateinit var viewModel: MovieDetailsViewModel

    private val genre1 = Genre("1", "Action")
    private val genre2 = Genre("2", "Comedy")
    private val movie = Movie(
        "1", listOf(genre1, genre2), "/backdrop.com", "/poster.com",
        "The Suicide Squad", "Overview", Locale.forLanguageTag("en"),
        LocalDate.of(2021, 11, 21), 90, 120,
        "/homepageUrl.com"
    )


    @Test
    fun movie_success() = mainCoroutineRule.runBlockingTest {
        val movieId = movie.id
        Mockito.`when`(moviesRepository.getMovieById(movieId))
            .thenReturn(Either.Right(Data.fromServer(movie)))
        viewModel = MovieDetailsViewModel(
            moviesRepository,
            SavedStateHandle(mapOf(MovieDetailsViewModel.MOVIE_ID_SAVED_STATE_KEY to movieId))
        )

        assertThat(viewModel.movie.getOrAwaitValue()).isEqualTo(Resource.Success(movie))
    }


}