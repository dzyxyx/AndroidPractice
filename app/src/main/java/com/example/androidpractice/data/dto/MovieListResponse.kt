package com.example.androidpractice.data.dto

data class MovieListResponse(
    val docs: List<MovieDto>,
    val total: Int,
    val limit: Int,
    val page: Int,
    val pages: Int,
)