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
    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("Tokenhgzdjcgdctzk")

        //        private val Context.dataStore:DataStore<Preferences> by preferencesDataStore("Token")
        val TOKEN = stringPreferencesKey("token")
        val NO = stringPreferencesKey("no")
        val SCREEN = stringPreferencesKey("screen")
        val PROFILEID = stringPreferencesKey("ProfileId")
        val DRIVER_NAME = stringPreferencesKey("DriverName")
        val IMAGE_URL = stringPreferencesKey("ImageUrl")
        val ADDRESS = stringPreferencesKey("Address")

        val ROUTEID = stringPreferencesKey("routeId")
        val ROUTE_NAME = stringPreferencesKey("RouteName")
        val PICKUP_ID = stringPreferencesKey("PickUpId")
        val DROP_ID = stringPreferencesKey("DropId")

        val BRANCHID = stringPreferencesKey("BranchId")
        val VEHICLEID = stringPreferencesKey("VehicleId")

        val STARTSERVICEID = stringPreferencesKey("StartServiceId")

    }

    val getStartServiceId:Flow<String?> = context.dataStore.data
        .map { preference: Preferences ->
            preference[STARTSERVICEID] ?: ""
        }
    val getToken: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[TOKEN] ?: ""
        }
    val getVehicleId: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[VEHICLEID] ?: ""
        }


    val getProfileId: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[PROFILEID] ?: ""
        }
    val getDriverName: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[DRIVER_NAME] ?: ""
        }
    val getImageUrl: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[IMAGE_URL] ?: ""
        }
    val getAddress: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[ADDRESS] ?: ""
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
    val getRouteName: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[ROUTE_NAME] ?: ""
        }
    val getPickUpId: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[PICKUP_ID] ?: ""
        }
    val getDropId: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[DROP_ID] ?: ""
        }

    suspend fun saveToken(token: String) {
        try {
            context.dataStore.edit { preferences ->
                preferences[TOKEN] = token
            }
        } catch (e: Exception) {
            Log.e("Storedata", "Token Exception")
        }
    }

    suspend fun saveProfileId(profileId: String) {
        try {
            context.dataStore.edit { preferences ->
                preferences[PROFILEID] = profileId
            }
        } catch (e: Exception) {
            Log.e("Storedata", "profileId Exception")
        }
    }

    suspend fun saveDriverName(drivername: String) {
        try {
            context.dataStore.edit { preferences ->
                preferences[DRIVER_NAME] = drivername
            }
        } catch (e: Exception) {
            Log.e("Storedata", "profileId Exception")
        }
    }

    suspend fun saveImageUrl(imageUrl: String) {
        try {
            context.dataStore.edit { preferences ->
                preferences[IMAGE_URL] = imageUrl
            }
        } catch (e: Exception) {
            Log.e("Storedata", "profileId Exception")
        }
    }

    suspend fun saveAddress(address: String) {
        try {
            context.dataStore.edit { preferences ->
                preferences[ADDRESS] = address
            }
        } catch (e: Exception) {
            Log.e("Storedata", "profileId Exception")
        }
    }

    suspend fun saveBranchId(branchId: String) {
        try {
            context.dataStore.edit { preferences ->
                preferences[BRANCHID] = branchId
            }
        } catch (e: Exception) {
            Log.e("Storedata", "BRANCHID Exception")
        }
    }


    suspend fun saveNo(no: String) {
        try {
            context.dataStore.edit { preferences ->
                preferences[NO] = no
            }
        } catch (e: Exception) {
            Log.e("Storedata", "NO Exception")
        }
    }

    suspend fun saverouteId(routeId: String) {
        try {
            context.dataStore.edit { preferences ->
                preferences[ROUTEID] = routeId
            }
        } catch (e: Exception) {
            Log.e("Storedata", "NO Exception")
        }
    }

    suspend fun saveScreen(screen: String) {
        try {
            context.dataStore.edit { preferences ->
                preferences[SCREEN] = screen
            }
        } catch (e: Exception) {
            Log.e("Storedata", "NO Exception")
        }
    }

    suspend fun saveRouteName(routeName: String) {
        try {
            context.dataStore.edit { preferences ->
                preferences[ROUTE_NAME] = routeName
            }
        } catch (e: Exception) {
            Log.e("Storedata", "NO Exception")
        }
    }

    suspend fun savePickUpId(id: String) {
        try {
            context.dataStore.edit { preferences ->
                preferences[PICKUP_ID] = id
            }
        } catch (e: Exception) {
            Log.e("Storedata", "NO Exception")
        }
    }

    suspend fun saveDropId(id: String) {
        try {
            context.dataStore.edit { preferences ->
                preferences[DROP_ID] = id
            }
        } catch (e: Exception) {
            Log.e("Storedata", "NO Exception")
        }
    }

    suspend fun saveVehicleId(id: String) {
        try {
            context.dataStore.edit { preferences ->
                preferences[VEHICLEID] = id
            }
        } catch (e: Exception) {
            Log.e("Storedata", "NO Exception")
        }
    }

    suspend fun saveStartService(id:String){
        try{
            context.dataStore.edit { preference ->
                preference[STARTSERVICEID] =id
            }
        }catch(e:Exception){
            Log.e("StoreData","No Exception ")
        }
    }

    suspend fun clearData() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}




