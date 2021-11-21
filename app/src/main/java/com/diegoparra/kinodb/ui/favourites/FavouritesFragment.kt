package com.diegoparra.kinodb.ui.favourites

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.diegoparra.kinodb.R
import com.diegoparra.kinodb.databinding.FavouritesFragmentBinding
import com.diegoparra.kinodb.models.Movie
import com.diegoparra.kinodb.ui.search.SearchResultState
import com.diegoparra.kinodb.ui.shared.MoviesAdapter
import com.diegoparra.kinodb.utils.EventObserver
import com.diegoparra.kinodb.utils.Resource
import com.diegoparra.kinodb.utils.getErrorMessage
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FavouritesFragment : Fragment() {

    private var _binding: FavouritesFragmentBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FavouritesViewModel by viewModels()
    private lateinit var adapter: MoviesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FavouritesFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.favouritesList.setHasFixedSize(true)
        adapter = MoviesAdapter(viewModel::onMovieClick)
        binding.favouritesList.adapter = adapter
        subscribeUi()
    }

    private fun subscribeUi() {
        viewModel.movies.observe(viewLifecycleOwner) {
            binding.progressBar.isVisible = it is Resource.Loading
            binding.errorMessage.isVisible = it is Resource.Error
            when (it) {
                is Resource.Loading -> {
                }
                is Resource.Success -> renderFavouriteMovies(it.data)
                is Resource.Error -> renderFailure(it.failure)
            }
        }
        viewModel.navigateMovieDetails.observe(viewLifecycleOwner, EventObserver {
            val action = FavouritesFragmentDirections.actionGlobalMovieDetailsFragment(it)
            findNavController().navigate(action)
        })
    }

    private fun renderFavouriteMovies(movies: List<Movie>) {
        adapter.submitList(movies)
        if (movies.isEmpty()) {
            binding.errorMessage.isVisible = true
            binding.errorMessage.text = getString(R.string.empty_favourites)
        }
    }

    private fun renderFailure(exception: Exception) {
        binding.errorMessage.text = exception.getErrorMessage(binding.root.context)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}