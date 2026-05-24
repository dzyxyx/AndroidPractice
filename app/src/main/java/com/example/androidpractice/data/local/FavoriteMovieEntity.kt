package com.example.androidpractice.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_movies")
data class FavoriteMovieEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val alternativeTitle: String?,
    val year: Int?,
    val description: String?,
    val rating: Double?,
    val posterUrl: String?,
    val genres: String,
    val countries: String,
    val director: String?,
    val movieLength: Int?
)
