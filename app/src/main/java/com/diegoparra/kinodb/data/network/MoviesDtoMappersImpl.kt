package com.diegoparra.kinodb.data.network

import com.diegoparra.kinodb.data.network.dtos.*
import com.diegoparra.kinodb.models.Genre
import com.diegoparra.kinodb.models.Movie
import com.diegoparra.kinodb.utils.LocalDateUtils
import com.diegoparra.kinodb.utils.LocaleUtils

object MoviesDtoMappersImpl : MoviesDtoMappers {

    override fun toGenreList(genresResponse: GenresResponse): List<Genre> =
        genresResponse.genres.map { toGenre(it) }

    private fun toGenre(genreDto: GenreDto) = with(genreDto) {
        Genre(
            id = id,
            name = name ?: ""
        )
    }

    override fun toMovieList(movieListResponse: MovieListResponse): List<Movie> =
        movieListResponse.results.map { toMovie(it) }

    private fun toMovie(movieListItemDto: MovieListItemDto) = with(movieListItemDto) {
        Movie(
            id = id,
            genres = genreIds?.map { Genre(it, "") } ?: emptyList(),
            posterUrl = getImageUrlOrNull(posterPath) ?: "",
            backdropUrl = getImageUrlOrNull(backdropPath) ?: "",
            title = if (!title.isNullOrBlank()) title else originalTitle ?: "",
            overview = overview ?: "",
            language = LocaleUtils.forLanguageTagOrNull(originalLanguage),
            releaseDate = LocalDateUtils.parseOrNull(releaseDate),
            voteAverage = (voteAverage?.times(10))?.toInt(),
            runtimeMinutes = null,
            homepageUrl = null
        )
    }

    override fun toMovie(movieResponse: MovieResponse): Movie = with(movieResponse) {
        Movie(
            id = id,
            genres = genres?.map { toGenre(it) } ?: emptyList(),
            posterUrl = getImageUrlOrNull(posterPath) ?: "",
            backdropUrl = getImageUrlOrNull(backdropPath) ?: "",
            title = if (!title.isNullOrBlank()) title else originalTitle ?: "",
            overview = overview ?: "",
            language = LocaleUtils.forLanguageTagOrNull(originalLanguage),
            releaseDate = LocalDateUtils.parseOrNull(releaseDate),
            voteAverage = (voteAverage?.times(10))?.toInt(),
            runtimeMinutes = runtime,
            homepageUrl = homepageUrl
        )
    }

    private fun getImageUrlOrNull(relativePath: String?) =
        relativePath?.let { MoviesApi.IMAGE_URL_PREFIX + it }

}