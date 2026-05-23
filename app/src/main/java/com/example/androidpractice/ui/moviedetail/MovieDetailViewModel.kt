package com.example.androidpractice.ui.moviedetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidpractice.data.repository.MovieRepositoryImpl
import com.example.androidpractice.domain.model.Movie
import com.example.androidpractice.domain.usecase.GetMovieByIdUseCase
import com.example.androidpractice.ui.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MovieDetailViewModel(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val getMovieByIdUseCase = GetMovieByIdUseCase(MovieRepositoryImpl())
    private var movieId: Int? = null

    private val _state = MutableStateFlow<UiState<Movie>>(UiState.Loading)
    val state: StateFlow<UiState<Movie>> = _state

    init {
        savedStateHandle.get<Int>("movieId")?.let { id ->
            movieId = id
            loadMovie(id)
        }
    }

    fun loadMovie(id: Int) {
        viewModelScope.launch {
            _state.value = UiState.Loading
            try {
                val movie = getMovieByIdUseCase(id)
                _state.value = UiState.Success(movie)
            } catch (e: Exception) {
                _state.value = UiState.Error(e.message ?: "Неизвестная ошибка")
            }
        }
    }

    fun retry() {
        movieId?.let { loadMovie(it) }
    }
}