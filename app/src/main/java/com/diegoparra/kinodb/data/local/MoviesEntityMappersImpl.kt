package com.diegoparra.kinodb.data.local

import com.diegoparra.kinodb.data.local.entities.GenreEntity
import com.diegoparra.kinodb.data.local.entities.MovieEntity
import com.diegoparra.kinodb.data.local.entities.MovieWithGenresDb
import com.diegoparra.kinodb.models.Genre
import com.diegoparra.kinodb.models.Movie

object MoviesEntityMappersImpl : MoviesEntityMappers {

    override fun toGenreList(genresDb: List<GenreEntity>): List<Genre> =
        genresDb.map { toGenre(it) }

    private fun toGenre(genre: GenreEntity): Genre = Genre(
        id = genre.genreId,
        name = genre.name
    )


    override fun toMovieList(movies: List<MovieEntity>): List<Movie> = movies.map { toMovie(it) }

    private fun toMovie(movieEntity: MovieEntity) = with(movieEntity) {
        Movie(
            id = movieId,
            genres = emptyList(),
            posterUrl = posterUrl,
            backdropUrl = backdropUrl,
            title = title,
            overview = overview,
            language = language,
            releaseDate = releaseDate,
            voteAverage = voteAverage,
            runtimeMinutes = runtimeMinutes,
            homepageUrl = homepageUrl
        )
    }

    override fun toMovie(movieWithGenresDb: MovieWithGenresDb): Movie =
        with(movieWithGenresDb.movie) {
            Movie(
                id = movieId,
                genres = movieWithGenresDb.genres.map { toGenre(it) },
                posterUrl = posterUrl,
                backdropUrl = backdropUrl,
                title = title,
                overview = overview,
                language = language,
                releaseDate = releaseDate,
                voteAverage = voteAverage,
                runtimeMinutes = runtimeMinutes,
                homepageUrl = homepageUrl
            )
        }

}