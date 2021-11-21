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
import com.diegoparra.kinodb.utils.getErrorMessage
import com.diegoparra.kinodb.utils.getLogMessage
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.net.UnknownHostException

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
        viewModel.genresAndMovies.observe(viewLifecycleOwner) {
            binding.progressBar.isVisible = it is Resource.Loading

            if (it is Resource.Success) {
                val data = it.data
                adapter.submitList(data)

                if (data.isNullOrEmpty()) {
                    binding.errorMessage.text = getString(R.string.info_not_available)
                    binding.errorMessage.isVisible = true
                }
            }

            binding.errorMessage.isVisible = it is Resource.Error
            if (it is Resource.Error) {
                val errorMessage = it.failure.getErrorMessage(binding.root.context)
                binding.errorMessage.text = errorMessage
            }
        }

        viewModel.toastFailure.observe(viewLifecycleOwner, EventObserver {
            if (it is UnknownHostException) {
                Snackbar.make(
                    binding.root,
                    R.string.network_connection_error,
                    Snackbar.LENGTH_SHORT
                ).show()
            } else {
                Timber.e(it.getLogMessage())
            }
        })

        viewModel.navigateMovieDetails.observe(viewLifecycleOwner, EventObserver {
            val action = HomeFragmentDirections.actionGlobalMovieDetailsFragment(it)
            findNavController().navigate(action)
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}