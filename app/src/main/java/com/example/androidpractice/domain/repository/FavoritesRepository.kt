package com.example.androidpractice.domain.repository

import com.example.androidpractice.domain.model.Movie
import kotlinx.coroutines.flow.Flow

interface FavoritesRepository {
    fun getFavorites(): Flow<List<Movie>>
    suspend fun addToFavorites(movie: Movie)
    suspend fun removeFromFavorites(movie: Movie)
    fun isMovieFavorite(movieId: Int): Flow<Boolean>
    suspend fun getFavoriteById(movieId: Int): Movie?
}
