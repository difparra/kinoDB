package com.diegoparra.kinodb.data.network.dtos

import com.google.gson.annotations.SerializedName

data class MovieResponse(
    val id: String,
    @SerializedName("imdb_id") val imdbId: String?,
    val adult: Boolean?,
    val budget: Int?,
    val genres: List<GenreDto>?,
    @SerializedName("homepage") val homepageUrl: String?,
    @SerializedName("backdrop_path") val backdropPath: String?,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("original_title") val originalTitle: String?,
    val title: String?,
    @SerializedName("original_language") val originalLanguage: String?,
    val overview: String?,
    @SerializedName("release_date") val releaseDate: String?,
    val popularity: Double?,
    @SerializedName("vote_count") val voteCount: Int?,
    @SerializedName("vote_average") val voteAverage: Float?,
    val runtime: Int?,
    val status: String?,        //  Allowed values: Rumored, Planned, in Production, Post Production, Released, Cancelled
    val tagline: String?,
    val video: Boolean?
)