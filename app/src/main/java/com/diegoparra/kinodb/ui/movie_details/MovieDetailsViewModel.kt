package com.diegoparra.kinodb.ui.movie_details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.diegoparra.kinodb.data.MoviesRepository
import com.diegoparra.kinodb.models.Movie
import com.diegoparra.kinodb.utils.Resource
import com.diegoparra.kinodb.utils.toResource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieDetailsViewModel @Inject constructor(
    private val moviesRepo: MoviesRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val movieId = savedStateHandle.get<String>(MOVIE_ID_SAVED_STATE_KEY)!!

    private val _movie = MutableStateFlow<Resource<Movie>>(Resource.Loading)
    val movie = _movie.asLiveData()

    init {
        viewModelScope.launch {
            _movie.value = moviesRepo.getMovieById(movieId).toResource()
        }
    }

    companion object {
        const val MOVIE_ID_SAVED_STATE_KEY = "movie_id"
    }

}