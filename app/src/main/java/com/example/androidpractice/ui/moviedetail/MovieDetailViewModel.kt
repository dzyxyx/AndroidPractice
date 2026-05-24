package com.example.androidpractice.ui.moviedetail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.androidpractice.data.local.AppDatabase
import com.example.androidpractice.data.local.FavoritesRepositoryImpl
import com.example.androidpractice.data.repository.MovieRepositoryImpl
import com.example.androidpractice.domain.model.Movie
import com.example.androidpractice.domain.usecase.AddToFavoritesUseCase
import com.example.androidpractice.domain.usecase.GetMovieByIdUseCase
import com.example.androidpractice.domain.usecase.IsMovieFavoriteUseCase
import com.example.androidpractice.domain.usecase.RemoveFromFavoritesUseCase
import com.example.androidpractice.ui.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MovieDetailViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {

    private val getMovieByIdUseCase = GetMovieByIdUseCase(MovieRepositoryImpl())

    private val dao = AppDatabase.getDatabase(application).favoriteMovieDao()
    private val favoritesRepository = FavoritesRepositoryImpl(dao)
    private val addToFavoritesUseCase = AddToFavoritesUseCase(favoritesRepository)
    private val removeFromFavoritesUseCase = RemoveFromFavoritesUseCase(favoritesRepository)
    private val isMovieFavoriteUseCase = IsMovieFavoriteUseCase(favoritesRepository)

    private var movieId: Int? = null

    private val _state = MutableStateFlow<UiState<Movie>>(UiState.Loading)
    val state: StateFlow<UiState<Movie>> = _state

    val isInFavorites: StateFlow<Boolean>

    init {
        val id = savedStateHandle.get<Int>("movieId")
        movieId = id
        if (id != null) loadMovie(id)
        isInFavorites = if (id != null) {
            isMovieFavoriteUseCase(id)
                .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)
        } else {
            MutableStateFlow(false)
        }
    }

    fun loadMovie(id: Int) {
        viewModelScope.launch {
            _state.value = UiState.Loading

            // Сначала проверяем локальную БД (работает офлайн)
            val localMovie = favoritesRepository.getFavoriteById(id)
            if (localMovie != null) {
                _state.value = UiState.Success(localMovie)
            }

            // Пробуем обновить из сети
            try {
                val networkMovie = getMovieByIdUseCase(id)
                _state.value = UiState.Success(networkMovie)
            } catch (e: Exception) {
                // Если локальные данные уже показаны — не показываем ошибку
                if (localMovie == null) {
                    _state.value = UiState.Error(e.message ?: "Неизвестная ошибка")
                }
            }
        }
    }

    fun retry() {
        movieId?.let { loadMovie(it) }
    }

    fun toggleFavorite() {
        val currentState = _state.value
        if (currentState is UiState.Success) {
            viewModelScope.launch {
                if (isInFavorites.value) {
                    removeFromFavoritesUseCase(currentState.data)
                } else {
                    addToFavoritesUseCase(currentState.data)
                }
            }
        }
    }
}
