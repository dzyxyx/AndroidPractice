package com.example.androidpractice.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteMovieDao {

    @Query("SELECT * FROM favorite_movies")
    fun getAllFavorites(): Flow<List<FavoriteMovieEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(movie: FavoriteMovieEntity)

    @Delete
    suspend fun deleteFavorite(movie: FavoriteMovieEntity)

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_movies WHERE id = :movieId)")
    fun isMovieFavorite(movieId: Int): Flow<Boolean>

    @Query("SELECT * FROM favorite_movies WHERE id = :movieId")
    suspend fun getFavoriteById(movieId: Int): FavoriteMovieEntity?
}
