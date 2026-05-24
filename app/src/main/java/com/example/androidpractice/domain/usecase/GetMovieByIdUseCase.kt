package com.example.androidpractice.domain.usecase

import com.example.androidpractice.domain.model.Movie
import com.example.androidpractice.domain.repository.MovieRepository

class GetMovieByIdUseCase(private val repository: MovieRepository) {
    suspend operator fun invoke(id: Int): Movie = repository.getMovieById(id)
}