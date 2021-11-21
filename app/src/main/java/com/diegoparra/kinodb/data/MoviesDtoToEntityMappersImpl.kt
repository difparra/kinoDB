package com.diegoparra.kinodb.data

import com.diegoparra.kinodb.data.local.entities.GenreEntity
import com.diegoparra.kinodb.data.local.entities.MovieEntity
import com.diegoparra.kinodb.data.local.entities.MovieWithGenresDb
import com.diegoparra.kinodb.data.network.MoviesApi
import com.diegoparra.kinodb.data.network.dtos.*
import com.diegoparra.kinodb.utils.LocalDateUtils
import com.diegoparra.kinodb.utils.LocaleUtils

object MoviesDtoToEntityMappersImpl : MoviesDtoToEntityMappers {

    override fun toGenreEntityList(genresResponse: GenresResponse): List<GenreEntity> =
        genresResponse.genres.map { toGenreEntity(it) }

    private fun toGenreEntity(genreDto: GenreDto) = with(genreDto) {
        GenreEntity(
            genreId = id,
            name = name ?: ""
        )
    }


    override fun toMovieWithGenresList(movieListResponse: MovieListResponse): List<MovieWithGenresDb> =
        movieListResponse.results.map { toMovieWithGenresDb(it) }

    private fun toMovieWithGenresDb(movieListItemDto: MovieListItemDto) = with(movieListItemDto) {
        MovieWithGenresDb(
            movie = MovieEntity(
                movieId = id,
                posterUrl = getImageUrlOrNull(posterPath) ?: "",
                backdropUrl = getImageUrlOrNull(backdropPath) ?: "",
                title = if (!title.isNullOrBlank()) title else originalTitle ?: "",
                overview = overview ?: "",
                language = LocaleUtils.forLanguageTagOrNull(originalLanguage),
                releaseDate = LocalDateUtils.parseOrNull(releaseDate),
                voteAverage = (voteAverage?.times(10))?.toInt(),
                popularity = popularity ?: 0.0,
                runtimeMinutes = null,
                homepageUrl = null
            ),
            genres = genreIds?.map { GenreEntity(it, "") } ?: emptyList(),
        )
    }

    override fun toMovieWithGenresDb(movieResponse: MovieResponse): MovieWithGenresDb =
        with(movieResponse) {
            MovieWithGenresDb(
                movie = MovieEntity(
                    movieId = id,
                    posterUrl = getImageUrlOrNull(posterPath) ?: "",
                    backdropUrl = getImageUrlOrNull(backdropPath) ?: "",
                    title = if (!title.isNullOrBlank()) title else originalTitle ?: "",
                    overview = overview ?: "",
                    language = LocaleUtils.forLanguageTagOrNull(originalLanguage),
                    releaseDate = LocalDateUtils.parseOrNull(releaseDate),
                    voteAverage = (voteAverage?.times(10))?.toInt(),
                    popularity = popularity ?: 0.0,
                    runtimeMinutes = null,
                    homepageUrl = null
                ),
                genres = genres?.map { toGenreEntity(it) } ?: emptyList(),
            )
        }


    private fun getImageUrlOrNull(relativePath: String?) =
        relativePath?.let { MoviesApi.IMAGE_URL_PREFIX + it }

}