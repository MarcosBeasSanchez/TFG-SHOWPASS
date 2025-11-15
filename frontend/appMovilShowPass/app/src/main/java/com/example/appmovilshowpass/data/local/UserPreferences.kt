package com.example.appmovilshowpass.data.local

import android.content.Context
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

val Context.dataStore by preferencesDataStore("user_prefs")

object UserPreferencesKeys {
    val USER_PHOTO = stringPreferencesKey("user_photo")
}
