package com.diegoparra.kinodb.ui.shared

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.diegoparra.kinodb.databinding.GridItemMovieBinding
import com.diegoparra.kinodb.models.Movie
import com.diegoparra.kinodb.utils.loadImage

class MoviesAdapter(
    private val onMovieClick: (movieId: String) -> Unit
) : ListAdapter<Movie, MoviesAdapter.ViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent, onMovieClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(
        private val binding: GridItemMovieBinding,
        private val onMovieClick: (movieId: String) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        private var movie: Movie? = null

        init {
            binding.root.setOnClickListener {
                movie?.let { onMovieClick(it.id) }
            }
        }

        fun bind(movie: Movie) {
            this.movie = movie
            binding.image.loadImage(movie.posterUrl)
            binding.title.text = movie.title
        }

        companion object {
            fun from(parent: ViewGroup, onMovieClick: (movieId: String) -> Unit): ViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                return ViewHolder(
                    GridItemMovieBinding.inflate(inflater, parent, false),
                    onMovieClick
                )
            }
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Movie>() {
        override fun areItemsTheSame(oldItem: Movie, newItem: Movie) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Movie, newItem: Movie) =
            oldItem == newItem
    }

}