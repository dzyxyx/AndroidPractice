package com.example.androidpractice.domain.model

data class Movie(
    val id: Int,
    val title: String,
    val alternativeTitle: String?,
    val year: Int?,
    val description: String?,
    val rating: Double?,
    val posterUrl: String?,
    val genres: String,
    val countries: String,
    val director: String?,
    val movieLength: Int?,
)