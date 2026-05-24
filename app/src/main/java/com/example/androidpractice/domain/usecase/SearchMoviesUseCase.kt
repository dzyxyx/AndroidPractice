package com.example.androidpractice.domain.usecase

import com.example.androidpractice.domain.model.Movie
import com.example.androidpractice.domain.repository.MovieRepository

class SearchMoviesUseCase(private val repository: MovieRepository) {
    suspend operator fun invoke(query: String): List<Movie> = repository.searchMovies(query)
}
