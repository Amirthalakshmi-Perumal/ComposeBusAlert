package com.tws.composebusalert.datastore

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import androidx.datastore.preferences.core.Preferences

class StoreData(private val context: Context) {
    companion object{
        private val Context.dataStore:DataStore<Preferences> by preferencesDataStore("Token")
        val TOKEN = stringPreferencesKey("token")
    }
    val getEmail: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[TOKEN] ?: ""
        }
    suspend fun saveToken(token: String) {
        try {
            context.dataStore.edit { preferences ->
                preferences[TOKEN] = token
            }
        } catch (e: Exception) {
          Log.e("Storedata","Token Exception")
        }
    }
}