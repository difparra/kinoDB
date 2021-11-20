package com.diegoparra.kinodb.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.diegoparra.kinodb.data.MoviesRepository
import com.diegoparra.kinodb.models.Genre
import com.diegoparra.kinodb.models.GenreWithMovies
import com.diegoparra.kinodb.utils.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
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


    //      ---------   Loading genres

    private val _genres = MutableStateFlow<List<Genre>>(emptyList())
    init {
        viewModelScope.launch {
            _loading.value = true
            moviesRepo.getGenres().fold(
                onSuccess = { _genres.value = it },
                onFailure = { _failure.value = Event(it) }
            )
        }
    }


    //      ---------   Loading movies by genre

    private val _genreAndMovies = _genres
        .onEach {
            Timber.d("Calling onEach to set loading to true")
            _loading.value = true
        }
        .map { genres ->
            Timber.d("Loaded genres: ${genres.joinToString { it.name }}. Proceding to load movies for each genre.")

            //  Loading async
            genres.mapAsync { genre ->
                Timber.d("Loading movies for genre $genre")
                moviesRepo.getMoviesByGenre(genre.id)
                    .map { GenreWithMovies(genre, it) }
                    .onFailure {
                        Timber.e("Error loading movies for genre $genre\nException: $it\nException class: ${it.javaClass}")
                        _failure.value = Event(it)
                    }
                    .getOrNull()
            }.filterNotNull()

            /*
            //  Loading sync
            genres.mapNotNull { genre ->
                Timber.d("Loading movies for genre $genre")
                moviesRepo.getMoviesByGenre(genre.id)
                    .map { GenreWithMovies(genre, it) }
                    .onFailure {
                        Timber.e("Error loading movies for genre $genre\nException: $it\nException class: ${it.javaClass}")
                        _failure.value = Event(it)
                    }
                    .getOrNull()
            }*/
        }
        .onEach {
            Timber.d("Calling onEach to set loading to false")
            _loading.value = false
        }
    val genreAndMovies = _genreAndMovies.asLiveData()



    //      ---------   OnMovieClick

    private val _navigateMovieDetails = MutableLiveData<Event<String>>()
    val navigateMovieDetails = _navigateMovieDetails

    fun onMovieClick(movieId: String) {
        _navigateMovieDetails.value = Event(movieId)
    }

}