package com.diegoparra.kinodb.ui.favourites

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.diegoparra.kinodb.data.MoviesRepository
import com.diegoparra.kinodb.models.Movie
import com.diegoparra.kinodb.utils.Event
import com.diegoparra.kinodb.utils.Resource
import com.diegoparra.kinodb.utils.toResource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

@HiltViewModel
class FavouritesViewModel @Inject constructor(
    private val moviesRepo: MoviesRepository
) : ViewModel() {

    val movies: LiveData<Resource<List<Movie>>> =
        moviesRepo.getFavourites()
            .map { it.toResource() }
            .onStart { emit(Resource.Loading) }
            .asLiveData()


    private val _navigateMovieDetails = MutableLiveData<Event<String>>()
    val navigateMovieDetails: LiveData<Event<String>> = _navigateMovieDetails

    fun onMovieClick(movieId: String) {
        _navigateMovieDetails.value = Event(movieId)
    }

}