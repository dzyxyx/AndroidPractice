package com.example.androidpractice.data.repository

import com.example.androidpractice.data.api.RetrofitClient
import com.example.androidpractice.data.dto.toDomain
import com.example.androidpractice.data.preferences.FilterSettings
import com.example.androidpractice.domain.model.Movie
import com.example.androidpractice.domain.repository.MovieRepository

class MovieRepositoryImpl : MovieRepository {

    private val api = RetrofitClient.movieApi

    override suspend fun getMovies(filters: FilterSettings): List<Movie> =
        api.getMovies(
            genre = filters.genre.takeIf { it.isNotBlank() },
            minRating = if (filters.minRating > 0.0) "${filters.minRating}-10" else null,
            minYear = if (filters.minYear > 0) "${filters.minYear}-2030" else null,
        ).docs.map { it.toDomain() }

    override suspend fun getMovieById(id: Int): Movie =
        api.getMovieById(id).toDomain()

    override suspend fun searchMovies(query: String): List<Movie> =
        api.searchMovies(query).docs.map { it.toDomain() }
}
