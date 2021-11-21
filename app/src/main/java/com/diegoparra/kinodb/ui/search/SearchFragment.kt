package com.diegoparra.kinodb.ui.search

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.diegoparra.kinodb.R
import com.diegoparra.kinodb.databinding.SearchFragmentBinding
import com.diegoparra.kinodb.models.Movie
import com.diegoparra.kinodb.ui.shared.MoviesAdapter
import com.diegoparra.kinodb.utils.Data
import com.diegoparra.kinodb.utils.EventObserver
import com.diegoparra.kinodb.utils.getErrorMessage
import com.diegoparra.kinodb.utils.hideKeyboard
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.FlowPreview

@FlowPreview
@AndroidEntryPoint
class SearchFragment : Fragment() {

    private var _binding: SearchFragmentBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SearchViewModel by viewModels()
    private lateinit var adapter: MoviesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = SearchFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setUpSearchView()
        setUpRecyclerView()
        subscribeUi()
    }

    private fun setUpSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean = false
            override fun onQueryTextChange(p0: String?): Boolean {
                p0?.let { viewModel.setQuery(p0) }
                return true
            }
        })
        binding.searchView.setOnQueryTextFocusChangeListener { view, b -> if (!b) view.hideKeyboard() }
        binding.moviesList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                binding.moviesList.requestFocus()
                super.onScrollStateChanged(recyclerView, newState)
            }
        })
    }

    private fun setUpRecyclerView() {
        binding.moviesList.setHasFixedSize(true)
        adapter = MoviesAdapter(viewModel::onMovieClick)
        binding.moviesList.adapter = adapter
    }

    private fun subscribeUi() {
        viewModel.moviesResult.observe(viewLifecycleOwner) {
            binding.progressBar.isVisible = it is SearchResultState.Loading
            binding.errorMessage.isVisible = it is SearchResultState.Failure
            when (it) {
                is SearchResultState.Loading -> {
                }
                is SearchResultState.EmptyQuery -> renderEmptyQueryState()
                is SearchResultState.NoResults -> renderNoResultsState(it.source)
                is SearchResultState.Success -> renderSuccess(it.data)
                is SearchResultState.Failure -> renderFailure(it.exception)
            }
        }

        viewModel.navigateMovieDetails.observe(viewLifecycleOwner, EventObserver {
            val action = SearchFragmentDirections.actionGlobalMovieDetailsFragment(it)
            findNavController().navigate(action)
        })
    }

    private fun renderEmptyQueryState() {
        adapter.submitList(emptyList())
        binding.errorMessage.isVisible = true
        binding.errorMessage.text = getString(R.string.empty_query)
    }

    private fun renderNoResultsState(source: Data.Source) {
        adapter.submitList(emptyList())
        binding.errorMessage.isVisible = true
        binding.errorMessage.text = if (source == Data.Source.LOCAL) {
            getString(R.string.no_results_local)
        } else {
            getString(R.string.no_results_network)
        }
    }

    private fun renderSuccess(data: Data<List<Movie>>) {
        adapter.submitList(data.content)
    }

    private fun renderFailure(error: Exception) {
        binding.errorMessage.text = error.getErrorMessage(binding.root.context)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}