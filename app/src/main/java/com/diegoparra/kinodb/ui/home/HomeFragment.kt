package com.diegoparra.kinodb.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.diegoparra.kinodb.databinding.HomeFragmentBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: HomeFragmentBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = HomeFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.loading.observe(viewLifecycleOwner) {
            binding.progressBar.isVisible = it
        }
        viewModel.genres.observe(viewLifecycleOwner) {
            binding.text.text = it.joinToString("\n") { it.name }
        }

        //  Observe so that flows/live data are started and collected
        viewModel.moviesByGenre.observe(viewLifecycleOwner) {}
        viewModel.movieById.observe(viewLifecycleOwner) {}
        viewModel.movieSearch.observe(viewLifecycleOwner) {}
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}