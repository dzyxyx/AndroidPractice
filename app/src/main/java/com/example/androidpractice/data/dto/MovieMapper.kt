package com.example.androidpractice.data.dto

import com.example.androidpractice.domain.model.Movie

fun MovieDto.toDomain(): Movie = Movie(
    id = id,
    title = name ?: alternativeName ?: "Без названия",
    alternativeTitle = alternativeName,
    year = year,
    description = description ?: shortDescription,
    rating = rating?.kp?.takeIf { it > 0.0 },
    posterUrl = poster?.url,
    genres = genres?.mapNotNull { it.name }?.joinToString(", ") ?: "",
    countries = countries?.mapNotNull { it.name }?.joinToString(", ") ?: "",
    director = persons
        ?.firstOrNull { it.enProfession == "director" }
        ?.name,
    movieLength = movieLength,
)