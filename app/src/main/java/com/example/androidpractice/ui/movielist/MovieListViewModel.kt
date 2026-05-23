package com.example.androidpractice.ui.movielist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidpractice.data.repository.MovieRepositoryImpl
import com.example.androidpractice.domain.model.Movie
import com.example.androidpractice.domain.usecase.GetMoviesUseCase
import com.example.androidpractice.domain.usecase.SearchMoviesUseCase
import com.example.androidpractice.ui.UiState
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
class MovieListViewModel : ViewModel() {

    private val repository = MovieRepositoryImpl()
    private val getMoviesUseCase = GetMoviesUseCase(repository)
    private val searchMoviesUseCase = SearchMoviesUseCase(repository)

    private val _state = MutableStateFlow<UiState<List<Movie>>>(UiState.Loading)
    val state: StateFlow<UiState<List<Movie>>> = _state

    val searchQuery = MutableStateFlow("")

    init {
        loadMovies()
        observeSearch()
    }

    private fun observeSearch() {
        viewModelScope.launch {
            searchQuery
                .debounce(1500)
                .distinctUntilChanged()
                .collect { query ->
                    if (query.isBlank()) loadMovies() else search(query)
                }
        }
    }

    fun loadMovies() {
        viewModelScope.launch {
            _state.value = UiState.Loading
            try {
                _state.value = UiState.Success(getMoviesUseCase())
            } catch (e: Exception) {
                _state.value = UiState.Error(e.message ?: "Неизвестная ошибка")
            }
        }
    }

    private fun search(query: String) {
        viewModelScope.launch {
            _state.value = UiState.Loading
            try {
                _state.value = UiState.Success(searchMoviesUseCase(query))
            } catch (e: Exception) {
                _state.value = UiState.Error(e.message ?: "Неизвестная ошибка")
            }
        }
    }
}
