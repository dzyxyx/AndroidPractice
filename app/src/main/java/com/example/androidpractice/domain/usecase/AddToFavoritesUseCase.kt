package com.example.androidpractice.domain.usecase

import com.example.androidpractice.domain.model.Movie
import com.example.androidpractice.domain.repository.FavoritesRepository

class AddToFavoritesUseCase(private val repository: FavoritesRepository) {
    suspend operator fun invoke(movie: Movie) = repository.addToFavorites(movie)
}
