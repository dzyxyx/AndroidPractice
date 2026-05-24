package com.example.androidpractice.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.androidpractice.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.profileStore: DataStore<Preferences> by preferencesDataStore(name = "profile")

class ProfileDataStore(private val context: Context) {

    companion object {
        private val KEY_FULL_NAME = stringPreferencesKey("full_name")
        private val KEY_POSITION = stringPreferencesKey("position")
        private val KEY_PHOTO_URI = stringPreferencesKey("photo_uri")
        private val KEY_RESUME_URL = stringPreferencesKey("resume_url")
        private val KEY_RESUME_LOCAL_URI = stringPreferencesKey("resume_local_uri")
    }

    val profile: Flow<UserProfile> = context.profileStore.data.map { prefs ->
        UserProfile(
            fullName = prefs[KEY_FULL_NAME] ?: "",
            position = prefs[KEY_POSITION] ?: "",
            photoUri = prefs[KEY_PHOTO_URI] ?: "",
            resumeUrl = prefs[KEY_RESUME_URL] ?: "",
            resumeLocalUri = prefs[KEY_RESUME_LOCAL_URI] ?: ""
        )
    }

    suspend fun saveProfile(profile: UserProfile) {
        context.profileStore.edit { prefs ->
            prefs[KEY_FULL_NAME] = profile.fullName
            prefs[KEY_POSITION] = profile.position
            prefs[KEY_PHOTO_URI] = profile.photoUri
            prefs[KEY_RESUME_URL] = profile.resumeUrl
            prefs[KEY_RESUME_LOCAL_URI] = profile.resumeLocalUri
        }
    }
}
