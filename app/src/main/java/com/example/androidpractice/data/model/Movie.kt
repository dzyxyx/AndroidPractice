package com.example.androidpractice.data.model


data class Movie(
    val id: Int,
    val title: String,
    val year: Int,
    val genre: String,
    val rating: Float,
    val description: String,
    val director: String,
    val posterUrl: String? = null
)