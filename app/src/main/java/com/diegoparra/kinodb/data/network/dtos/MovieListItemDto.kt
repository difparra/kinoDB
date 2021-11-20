package com.diegoparra.kinodb.data.network.dtos

import com.google.gson.annotations.SerializedName

data class MovieListItemDto(
    val id: String,
    val adult: Boolean?,
    @SerializedName("genre_ids") val genreIds: List<String>?,
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
    val video: Boolean?
)