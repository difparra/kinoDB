package com.diegoparra.kinodb.data.network

import com.diegoparra.kinodb.data.network.dtos.GenresResponse
import com.diegoparra.kinodb.data.network.dtos.MovieResponse
import com.diegoparra.kinodb.data.network.dtos.MovieListResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MoviesApi {

    @GET("genre/movie/list")
    suspend fun getGenres(): GenresResponse

    @GET("discover/movie")
    suspend fun getMoviesByGenre(@Query("with_genres") genreId: String): MovieListResponse

    @GET("movie/{movie_id}")
    suspend fun getMovieById(@Path("movie_id") movieId: String): MovieResponse

    @GET("search/movie")
    suspend fun searchMovieByName(@Query("query") name: String): MovieListResponse


    companion object {
        const val TMDB_KEY = "0189ad5be185d0efe0ef8b1ccd3c7462"
        const val BASE_URL = "https://api.themoviedb.org/3/"
        const val IMAGE_URL_PREFIX = "https://image.tmdb.org/t/p/original"
    }

}