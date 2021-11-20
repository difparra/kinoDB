package com.diegoparra.kinodb.data.network.dtos

import com.google.gson.annotations.SerializedName

data class MovieListResponse(
    val page: Int,
    val results: List<MovieListItemDto>,
    @SerializedName("total_results") val totalResults: Int?,
    @SerializedName("total_pages") val totalPages: Int?
)