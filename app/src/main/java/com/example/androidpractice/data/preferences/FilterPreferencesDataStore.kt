package com.example.androidpractice.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "filter_settings")

class FilterPreferencesDataStore(private val context: Context) {

    companion object {
        private val KEY_GENRE = stringPreferencesKey("genre")
        private val KEY_MIN_RATING = doublePreferencesKey("min_rating")
        private val KEY_MIN_YEAR = intPreferencesKey("min_year")
    }

    val filterSettings: Flow<FilterSettings> = context.dataStore.data.map { prefs ->
        FilterSettings(
            genre = prefs[KEY_GENRE] ?: "",
            minRating = prefs[KEY_MIN_RATING] ?: 0.0,
            minYear = prefs[KEY_MIN_YEAR] ?: 0
        )
    }

    suspend fun saveFilterSettings(settings: FilterSettings) {
        context.dataStore.edit { prefs ->
            prefs[KEY_GENRE] = settings.genre
            prefs[KEY_MIN_RATING] = settings.minRating
            prefs[KEY_MIN_YEAR] = settings.minYear
        }
    }
}
