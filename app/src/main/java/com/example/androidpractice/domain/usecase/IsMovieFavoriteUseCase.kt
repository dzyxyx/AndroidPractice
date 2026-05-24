package com.example.androidpractice.domain.usecase

import com.example.androidpractice.domain.repository.FavoritesRepository
import kotlinx.coroutines.flow.Flow

class IsMovieFavoriteUseCase(private val repository: FavoritesRepository) {
    operator fun invoke(movieId: Int): Flow<Boolean> = repository.isMovieFavorite(movieId)
}
