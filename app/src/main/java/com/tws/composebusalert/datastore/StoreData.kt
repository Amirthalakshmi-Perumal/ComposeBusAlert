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
import com.tws.composebusalert.datastore.StoreData.Companion.TOKEN

class StoreData(private val context: Context) {
    companion object{
        private val Context.dataStore:DataStore<Preferences> by preferencesDataStore("Tokenhgzdjcgdctzk")
        //        private val Context.dataStore:DataStore<Preferences> by preferencesDataStore("Token")
        val TOKEN = stringPreferencesKey("token")
        val NO = stringPreferencesKey("no")
        val ROUTEID = stringPreferencesKey("routeId")
        val SCREEN = stringPreferencesKey("screen")
        val PROFILEID = stringPreferencesKey("ProfileId")
        val BRANCHID = stringPreferencesKey("BranchId")

    }
    val getToken: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[TOKEN] ?: ""
        }
    val getProfileId: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[PROFILEID] ?: ""
        }

    val getBranchId: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[BRANCHID] ?: ""
        }

    val getNo: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[NO] ?: ""
        }
 val getrouteId: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[ROUTEID] ?: ""
        }
    val getScreen: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[SCREEN] ?: ""
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
     suspend fun saveProfileId(profileId: String) {
        try {
            context.dataStore.edit { preferences ->
                preferences[PROFILEID] = profileId
            }
        } catch (e: Exception) {
            Log.e("Storedata","profileId Exception")
        }
    }

  suspend fun saveBranchId(branchId: String) {
        try {
            context.dataStore.edit { preferences ->
                preferences[BRANCHID] = branchId
            }
        } catch (e: Exception) {
            Log.e("Storedata","BRANCHID Exception")
        }
    }


    suspend fun saveNo(no: String) {
        try {
            context.dataStore.edit { preferences ->
                preferences[NO] = no
            }
        } catch (e: Exception) {
            Log.e("Storedata","NO Exception")
        }
    }
      suspend fun saverouteId(routeId: String) {
        try {
            context.dataStore.edit { preferences ->
                preferences[ROUTEID] = routeId
            }
        } catch (e: Exception) {
            Log.e("Storedata","NO Exception")
        }
    }
 suspend fun screen(screen: String) {
        try {
            context.dataStore.edit { preferences ->
                preferences[SCREEN] = screen
            }
        } catch (e: Exception) {
            Log.e("Storedata","NO Exception")
        }
    }

    suspend fun clearData() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}




