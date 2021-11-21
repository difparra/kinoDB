package com.diegoparra.kinodb.ui.favourites

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.diegoparra.kinodb.data.MoviesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FavouritesViewModel @Inject constructor(
    private val moviesRepo: MoviesRepository
) : ViewModel() {
    // TODO: Implement the ViewModel
}