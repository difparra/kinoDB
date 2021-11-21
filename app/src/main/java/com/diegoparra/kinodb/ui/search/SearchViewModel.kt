package com.diegoparra.kinodb.ui.search

import androidx.lifecycle.*
import com.diegoparra.kinodb.data.MoviesRepository
import com.diegoparra.kinodb.models.Movie
import com.diegoparra.kinodb.utils.Data
import com.diegoparra.kinodb.utils.Event
import com.diegoparra.kinodb.utils.fold
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@FlowPreview
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val moviesRepo: MoviesRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val query = savedStateHandle.getLiveData(QUERY_SAVED_STATE_KEY, "").asFlow()

    fun setQuery(query: String) {
        savedStateHandle.set(QUERY_SAVED_STATE_KEY, query)
    }


    private val _moviesResult = MutableStateFlow<SearchResultState>(SearchResultState.EmptyQuery)
    val moviesResult = _moviesResult.asLiveData()

    init {
        viewModelScope.launch {
            query.distinctUntilChanged()
                .debounce(200)
                .collect { query ->
                    _moviesResult.value = SearchResultState.Loading
                    _moviesResult.value = if (query.isEmpty()) {
                        SearchResultState.EmptyQuery
                    } else {
                        moviesRepo.searchMovieByName(query).fold(
                            onSuccess = { results ->
                                if (results.content.isEmpty()) {
                                    SearchResultState.NoResults(results.source)
                                } else {
                                    SearchResultState.Success(results)
                                }
                            },
                            onFailure = { SearchResultState.Failure(it) }
                        )
                    }
                }
        }
    }


    //      ---------   OnMovieClick

    private val _navigateMovieDetails = MutableLiveData<Event<String>>()
    val navigateMovieDetails: LiveData<Event<String>> = _navigateMovieDetails

    fun onMovieClick(movieId: String) {
        _navigateMovieDetails.value = Event(movieId)
    }


    companion object {
        const val QUERY_SAVED_STATE_KEY = "query"
    }

}

sealed class SearchResultState {
    object Loading : SearchResultState()
    object EmptyQuery : SearchResultState()
    class NoResults(val source: Data.Source) : SearchResultState()
    class Success(val data: Data<List<Movie>>) : SearchResultState()
    class Failure(val exception: Exception) : SearchResultState()
}