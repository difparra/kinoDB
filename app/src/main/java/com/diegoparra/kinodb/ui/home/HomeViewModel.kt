package com.diegoparra.kinodb.ui.home

import androidx.lifecycle.*
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

    private val _toastFailure = MutableLiveData<Event<Exception>>()
    val toastFailure: LiveData<Event<Exception>> = _toastFailure.distinctUntilChanged()

    //      ---------   Loading genres

    private val _genresAndMovies =
        MutableStateFlow<Resource<List<GenreWithMovies>>>(Resource.Loading)
    val genresAndMovies = _genresAndMovies.asLiveData()

    init {
        viewModelScope.launch {
            _genresAndMovies.value = moviesRepo
                .getGenres()
                .map {
                    Timber.d("Loaded genres: ${it.joinToString { it.name }}. Proceding to load movies for each genre.")
                    addMoviesByGenreToListAsync(it)
                }
                .toResource()
        }
    }

    private suspend fun addMoviesByGenreToListAsync(genres: List<Genre>): List<GenreWithMovies> =
        genres
            .mapAsync { genre ->
                Timber.d("Loading movies for genre $genre")
                moviesRepo.getMoviesByGenre(genre.id)
                    .map { GenreWithMovies(genre, it) }
                    .onFailure {
                        Timber.e("Error loading movies for genre $genre\nException: $it\nException class: ${it.javaClass}")
                        _toastFailure.value = Event(it)
                    }
                    .getOrNull()
            }
            .filterNotNull()


    //      ---------   OnMovieClick

    private val _navigateMovieDetails = MutableLiveData<Event<String>>()
    val navigateMovieDetails: LiveData<Event<String>> = _navigateMovieDetails

    fun onMovieClick(movieId: String) {
        _navigateMovieDetails.value = Event(movieId)
    }

}