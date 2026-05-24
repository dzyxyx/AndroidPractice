package com.example.androidpractice.domain.repository

import com.example.androidpractice.data.preferences.FilterSettings
import com.example.androidpractice.domain.model.Movie

interface MovieRepository {
    suspend fun getMovies(filters: FilterSettings = FilterSettings()): List<Movie>
    suspend fun getMovieById(id: Int): Movie
    suspend fun searchMovies(query: String): List<Movie>
}