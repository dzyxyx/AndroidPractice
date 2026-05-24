package com.example.androidpractice.data.local

import com.example.androidpractice.domain.model.Movie
import com.example.androidpractice.domain.repository.FavoritesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavoritesRepositoryImpl(
    private val dao: FavoriteMovieDao
) : FavoritesRepository {

    override fun getFavorites(): Flow<List<Movie>> =
        dao.getAllFavorites().map { list -> list.map { it.toDomain() } }

    override suspend fun addToFavorites(movie: Movie) =
        dao.insertFavorite(movie.toEntity())

    override suspend fun removeFromFavorites(movie: Movie) =
        dao.deleteFavorite(movie.toEntity())

    override fun isMovieFavorite(movieId: Int): Flow<Boolean> =
        dao.isMovieFavorite(movieId)

    override suspend fun getFavoriteById(movieId: Int): Movie? =
        dao.getFavoriteById(movieId)?.toDomain()
}

private fun FavoriteMovieEntity.toDomain(): Movie = Movie(
    id = id,
    title = title,
    alternativeTitle = alternativeTitle,
    year = year,
    description = description,
    rating = rating,
    posterUrl = posterUrl,
    genres = genres,
    countries = countries,
    director = director,
    movieLength = movieLength
)

private fun Movie.toEntity(): FavoriteMovieEntity = FavoriteMovieEntity(
    id = id,
    title = title,
    alternativeTitle = alternativeTitle,
    year = year,
    description = description,
    rating = rating,
    posterUrl = posterUrl,
    genres = genres,
    countries = countries,
    director = director,
    movieLength = movieLength
)
