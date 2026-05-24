package com.example.androidpractice.ui.movielist

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidpractice.data.preferences.FilterPreferencesDataStore
import com.example.androidpractice.data.repository.MovieRepositoryImpl
import com.example.androidpractice.di.FilterBadgeCache
import com.example.androidpractice.domain.model.Movie
import com.example.androidpractice.domain.usecase.GetMoviesUseCase
import com.example.androidpractice.domain.usecase.SearchMoviesUseCase
import com.example.androidpractice.ui.UiState
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@OptIn(FlowPreview::class)
class MovieListViewModel(application: Application) : AndroidViewModel(application), KoinComponent {

    private val repository = MovieRepositoryImpl()
    private val getMoviesUseCase = GetMoviesUseCase(repository)
    private val searchMoviesUseCase = SearchMoviesUseCase(repository)
    private val filterDataStore = FilterPreferencesDataStore(application)
    private val badgeCache: FilterBadgeCache by inject()

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
                val filters = filterDataStore.filterSettings.first()
                badgeCache.update(!filters.isDefault())
                _state.value = UiState.Success(getMoviesUseCase(filters))
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
