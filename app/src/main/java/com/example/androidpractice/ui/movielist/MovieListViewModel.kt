package com.example.androidpractice.ui.movielist

import androidx.lifecycle.ViewModel
import com.example.androidpractice.data.model.Movie
import com.example.androidpractice.data.repository.MovieRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


class MovieListViewModel : ViewModel() {
    private val _movies = MutableStateFlow<List<Movie>>(emptyList())
    val movies: StateFlow<List<Movie>> = _movies

    init {
        _movies.value = MovieRepository.getMovies()
    }
}