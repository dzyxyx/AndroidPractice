package com.example.androidpractice.ui.moviedetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.androidpractice.data.model.Movie
import com.example.androidpractice.data.repository.MovieRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


class MovieDetailViewModel(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _movie = MutableStateFlow<Movie?>(null)
    val movie: StateFlow<Movie?> = _movie

    init {
        savedStateHandle.get<Int>("movieId")?.let { movieId ->
            _movie.value = MovieRepository.getMovieById(movieId)
        }
    }
}