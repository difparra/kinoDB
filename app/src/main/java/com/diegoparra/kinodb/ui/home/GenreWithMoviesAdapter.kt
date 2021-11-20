package com.diegoparra.kinodb.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.diegoparra.kinodb.databinding.ListItemGenreWithMoviesBinding
import com.diegoparra.kinodb.models.GenreWithMovies

class GenreWithMoviesAdapter(
    private val onMovieClick: (movieId: String) -> Unit
) : ListAdapter<GenreWithMovies, GenreWithMoviesAdapter.ViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent, onMovieClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(
        private val binding: ListItemGenreWithMoviesBinding,
        onMovieClick: (movieId: String) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        private val adapter = MoviesAdapter(onMovieClick)

        init {
            binding.moviesList.setHasFixedSize(true)
            binding.moviesList.adapter = adapter
        }

        fun bind(genreWithMovies: GenreWithMovies) {
            binding.genre.text = genreWithMovies.genre.name
            adapter.submitList(genreWithMovies.movies)
        }

        companion object {
            fun from(parent: ViewGroup, onMovieClick: (movieId: String) -> Unit): ViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                return ViewHolder(
                    ListItemGenreWithMoviesBinding.inflate(inflater, parent, false),
                    onMovieClick
                )
            }
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<GenreWithMovies>() {
        override fun areItemsTheSame(oldItem: GenreWithMovies, newItem: GenreWithMovies) =
            oldItem.genre.id == newItem.genre.id

        override fun areContentsTheSame(oldItem: GenreWithMovies, newItem: GenreWithMovies) =
            oldItem == newItem
    }

}