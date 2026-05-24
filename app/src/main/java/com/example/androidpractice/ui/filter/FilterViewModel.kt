package com.example.androidpractice.ui.filter

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidpractice.data.preferences.FilterPreferencesDataStore
import com.example.androidpractice.data.preferences.FilterSettings
import com.example.androidpractice.di.FilterBadgeCache
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class FilterViewModel(application: Application) : AndroidViewModel(application), KoinComponent {

    private val filterDataStore = FilterPreferencesDataStore(application)
    private val badgeCache: FilterBadgeCache by inject()

    private val _settings = MutableStateFlow(FilterSettings())
    val settings: StateFlow<FilterSettings> = _settings

    init {
        viewModelScope.launch {
            val saved = filterDataStore.filterSettings.first()
            _settings.value = saved
            badgeCache.update(!saved.isDefault())
        }
    }

    fun setGenre(genre: String) {
        _settings.value = _settings.value.copy(genre = genre)
    }

    fun setMinRating(rating: Double) {
        _settings.value = _settings.value.copy(minRating = rating)
    }

    fun setMinYear(year: Int) {
        _settings.value = _settings.value.copy(minYear = year)
    }

    fun saveAndApply(onDone: () -> Unit) {
        viewModelScope.launch {
            filterDataStore.saveFilterSettings(_settings.value)
            badgeCache.update(!_settings.value.isDefault())
            onDone()
        }
    }

    fun reset(onDone: () -> Unit) {
        viewModelScope.launch {
            val defaults = FilterSettings()
            _settings.value = defaults
            filterDataStore.saveFilterSettings(defaults)
            badgeCache.update(false)
            onDone()
        }
    }
}
