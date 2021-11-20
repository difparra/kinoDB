package com.diegoparra.kinodb.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.diegoparra.kinodb.data.MoviesRepository
import com.diegoparra.kinodb.models.Genre
import com.diegoparra.kinodb.models.Movie
import com.diegoparra.kinodb.utils.Either
import com.diegoparra.kinodb.utils.Event
import com.diegoparra.kinodb.utils.fold
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val moviesRepo: MoviesRepository
) : ViewModel() {

    private val _failure = MutableStateFlow<Event<Exception>?>(null)
    val failure = _failure.asLiveData()

    private val _loading = MutableStateFlow(false)
    val loading = _loading.asLiveData()

    private val _genres = MutableStateFlow<List<Genre>>(emptyList())
    val genres = _genres.asLiveData()

    init {
        viewModelScope.launch {
            _loading.value = true
            moviesRepo.getGenres().fold(
                onSuccess = { _genres.value = it },
                onFailure = { _failure.value = Event(it) }
            )
            _loading.value = false
        }
    }


    //  Additional code just to call methods on repository and check the logs.

    private val _moviesByGenre = _genres
        .filter { it.isNotEmpty() }
        .map { moviesRepo.getMoviesByGenre(it.first().id) }
    val moviesByGenre = _moviesByGenre.asLiveData()

    private val _movieById = _moviesByGenre
        .mapNotNull { if (it is Either.Right) it.b else null }
        .filter { it.isNotEmpty() }
        .map { moviesRepo.getMovieById(it.first().id) }
    val movieById = _movieById.asLiveData()

    private val _movieSearch = MutableStateFlow<List<Movie>>(emptyList())
    val movieSearch = _movieSearch.asLiveData()

    init {
        viewModelScope.launch {
            moviesRepo.searchMovieByName("suici")
        }
    }

}