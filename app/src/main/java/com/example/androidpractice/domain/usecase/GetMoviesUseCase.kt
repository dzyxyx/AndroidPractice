package com.example.androidpractice.domain.usecase

import com.example.androidpractice.data.preferences.FilterSettings
import com.example.androidpractice.domain.model.Movie
import com.example.androidpractice.domain.repository.MovieRepository

class GetMoviesUseCase(private val repository: MovieRepository) {
    suspend operator fun invoke(filters: FilterSettings = FilterSettings()): List<Movie> =
        repository.getMovies(filters)
}
