package com.example.androidpractice.data.repository

import com.example.androidpractice.data.api.RetrofitClient
import com.example.androidpractice.data.dto.toDomain
import com.example.androidpractice.domain.model.Movie
import com.example.androidpractice.domain.repository.MovieRepository

class MovieRepositoryImpl : MovieRepository {

    private val api = RetrofitClient.movieApi

    override suspend fun getMovies(): List<Movie> =
        api.getMovies().docs.map { it.toDomain() }

    override suspend fun getMovieById(id: Int): Movie =
        api.getMovieById(id).toDomain()

    override suspend fun searchMovies(query: String): List<Movie> =
        api.searchMovies(query).docs.map { it.toDomain() }
}