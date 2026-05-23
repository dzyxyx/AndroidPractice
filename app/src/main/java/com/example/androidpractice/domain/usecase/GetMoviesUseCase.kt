package com.example.androidpractice.domain.usecase

import com.example.androidpractice.domain.model.Movie
import com.example.androidpractice.domain.repository.MovieRepository

class GetMoviesUseCase(private val repository: MovieRepository) {
    suspend operator fun invoke(): List<Movie> = repository.getMovies()
}