package com.example.androidpractice.di

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class FilterBadgeCache {
    private val _hasActiveFilters = MutableStateFlow(false)
    val hasActiveFilters: StateFlow<Boolean> = _hasActiveFilters

    fun update(isActive: Boolean) {
        _hasActiveFilters.value = isActive
    }
}
