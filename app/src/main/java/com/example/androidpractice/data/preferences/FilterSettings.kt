package com.example.androidpractice.data.preferences

data class FilterSettings(
    val genre: String = "",
    val minRating: Double = 0.0,
    val minYear: Int = 0
) {
    fun isDefault(): Boolean = genre.isBlank() && minRating == 0.0 && minYear == 0
}
