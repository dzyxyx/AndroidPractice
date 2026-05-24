package com.example.androidpractice.ui.favorites

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidpractice.data.local.AppDatabase
import com.example.androidpractice.data.local.FavoritesRepositoryImpl
import com.example.androidpractice.domain.model.Movie
import com.example.androidpractice.domain.usecase.GetFavoritesUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class FavoritesViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = AppDatabase.getDatabase(application).favoriteMovieDao()
    private val repository = FavoritesRepositoryImpl(dao)
    private val getFavoritesUseCase = GetFavoritesUseCase(repository)

    val favorites: StateFlow<List<Movie>> = getFavoritesUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
