package com.diegoparra.kinodb.data.local

import androidx.room.*
import com.diegoparra.kinodb.data.local.entities.GenreEntity
import com.diegoparra.kinodb.data.local.entities.GenreMovieCrossRef
import com.diegoparra.kinodb.data.local.entities.MovieEntity
import com.diegoparra.kinodb.data.local.entities.MovieWithGenresDb
import com.diegoparra.kinodb.utils.removeCaseAndAccents

@Dao
abstract class MoviesDao {

    @Query("SELECT * FROM Genre ORDER BY name")
    abstract suspend fun getGenres(): List<GenreEntity>

    @Query("SELECT * FROM Movie ORDER BY popularity DESC")
    abstract suspend fun getAllMovies(): List<MovieEntity>

    @Query(
        """
        SELECT Movie.* FROM Movie 
        INNER JOIN GenreMovieCrossRef ON Movie.movieId = GenreMovieCrossRef.movieId 
        WHERE GenreMovieCrossRef.genreId = :genreId
        ORDER BY Movie.popularity DESC
    """
    )
    abstract suspend fun getMoviesByGenre(genreId: String): List<MovieEntity>

    @Transaction
    @Query("SELECT * FROM Movie WHERE movieId = :movieId")
    abstract suspend fun getMovieById(movieId: String): MovieWithGenresDb?

    @Query("SELECT * FROM Movie WHERE normalisedTitle LIKE ('%' || :normalisedQuery || '%') ORDER BY popularity DESC")
    protected abstract suspend fun _searchMovieByName(normalisedQuery: String): List<MovieEntity>
    suspend fun searchMovieByName(query: String) = _searchMovieByName(query.removeCaseAndAccents())


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun insertGenre(genre: GenreEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertOrUpdateGenre(genre: GenreEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertOrUpdateAllGenres(genres: List<GenreEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertOrUpdateMovie(movie: MovieEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertOrUpdateAllMovies(movie: List<MovieEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertGenreMovieCrossRef(genreMovieCrossRef: GenreMovieCrossRef)

    @Transaction
    open suspend fun insertMovieWithGenres(movieWithGenresDb: MovieWithGenresDb) {
        insertOrUpdateMovie(movieWithGenresDb.movie)
        movieWithGenresDb.genres.forEach {
            if (it.name.isBlank()) {
                insertGenre(it)
            } else {
                insertOrUpdateGenre(it)
            }

            insertGenreMovieCrossRef(
                GenreMovieCrossRef(
                    genreId = it.genreId,
                    movieId = movieWithGenresDb.movie.movieId
                )
            )
        }
    }

    @Transaction
    open suspend fun insertAllMovieWithGenres(movieWithGenresDb: List<MovieWithGenresDb>) {
        val listWithoutRepeatedItems = movieWithGenresDb.toHashSet().toList()
        listWithoutRepeatedItems.forEach {
            insertMovieWithGenres(it)
        }
    }

}