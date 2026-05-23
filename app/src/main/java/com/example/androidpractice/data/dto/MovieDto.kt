package com.example.androidpractice.data.dto

import com.google.gson.annotations.SerializedName

data class MovieDto(
    val id: Int,
    val name: String?,
    val alternativeName: String?,
    val year: Int?,
    val description: String?,
    val shortDescription: String?,
    val type: String?,
    val movieLength: Int?,
    val rating: RatingDto?,
    val poster: PosterDto?,
    val genres: List<GenreDto>?,
    val countries: List<CountryDto>?,
    val persons: List<PersonDto>?,
)

data class RatingDto(
    val kp: Double?,
    val imdb: Double?,
)

data class PosterDto(
    val url: String?,
    val previewUrl: String?,
)

data class GenreDto(val name: String?)

data class CountryDto(val name: String?)

data class PersonDto(
    val name: String?,
    val enName: String?,
    val profession: String?,
    @SerializedName("enProfession") val enProfession: String?,
)