package com.example.androidpractice.domain.repository

import com.example.androidpractice.domain.model.Movie

interface MovieRepository {
    suspend fun getMovies(): List<Movie>
    suspend fun getMovieById(id: Int): Movie
    suspend fun searchMovies(query: String): List<Movie>
}