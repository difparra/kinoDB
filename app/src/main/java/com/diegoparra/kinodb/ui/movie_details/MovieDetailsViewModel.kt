package com.diegoparra.kinodb.ui.movie_details

import androidx.lifecycle.*
import com.diegoparra.kinodb.data.MoviesRepository
import com.diegoparra.kinodb.models.Movie
import com.diegoparra.kinodb.utils.Resource
import com.diegoparra.kinodb.utils.getOrElse
import com.diegoparra.kinodb.utils.map
import com.diegoparra.kinodb.utils.toResource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieDetailsViewModel @Inject constructor(
    private val moviesRepo: MoviesRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _movieId = savedStateHandle.get<String>(MOVIE_ID_SAVED_STATE_KEY)!!

    private val _movie = MutableStateFlow<Resource<Movie>>(Resource.Loading)
    val movie = _movie.asLiveData()

    init {
        viewModelScope.launch {
            _movie.value = moviesRepo
                .getMovieById(_movieId)
                .map { it.content }
                .toResource()
        }
    }


    val isFavourite: LiveData<Boolean> =
        moviesRepo.isFavourite(_movieId)
            .map { it.getOrElse { false } }
            .asLiveData()

    fun toggleFavourite() {
        viewModelScope.launch {
            moviesRepo.toggleFavourite(_movieId)

        }
    }

    companion object {
        const val MOVIE_ID_SAVED_STATE_KEY = "movie_id"
    }

}