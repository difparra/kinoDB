package com.diegoparra.kinodb.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.diegoparra.kinodb.R
import com.diegoparra.kinodb.databinding.HomeFragmentBinding
import com.diegoparra.kinodb.utils.EventObserver
import com.diegoparra.kinodb.utils.Resource
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: HomeFragmentBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by viewModels()
    private lateinit var adapter: GenreWithMoviesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = HomeFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.genreWithMoviesList.setHasFixedSize(true)
        adapter = GenreWithMoviesAdapter(viewModel::onMovieClick)
        binding.genreWithMoviesList.adapter = adapter

        subscribeUi()
    }

    private fun subscribeUi() {
        viewModel.loading.observe(viewLifecycleOwner) {
            binding.progressBar.isVisible = it
        }
        viewModel.genreAndMovies.observe(viewLifecycleOwner) {
            if (it.isNullOrEmpty()) {
                binding.errorMessage.text = getString(R.string.info_not_available)
            }
            adapter.submitList(it)
        }
        viewModel.navigateMovieDetails.observe(viewLifecycleOwner, EventObserver {
            val action = HomeFragmentDirections.actionHomeFragmentToMovieDetailsFragment(it)
            findNavController().navigate(action)
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}