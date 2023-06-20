package com.tws.composebusalert.datastore

import android.content.Context
import android.icu.text.Normalizer.NO
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.tws.composebusalert.responses.PassengerDetailResponse
import com.tws.composebusalert.responses.StoppingListDS
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class StoreData(private val context: Context) {
    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("Tokenhgzdjcgdctzk")
        private val TOKEN_KEY = stringPreferencesKey("jwt_token")
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
//val STOPPINGS= listOf (stringPreferencesKey("Stoppings"))

        val STOPPINGS = stringPreferencesKey("Stoppings")
        val CARETAKERID = stringPreferencesKey("CareTaker")


        val PASS = stringPreferencesKey("PASS")

        var rRRresponses: List<PassengerDetailResponse>? = null
    }

    val getStartServiceId: Flow<String?> = context.dataStore.data
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
    val getStoppingList: Flow<List<StoppingListDS>> = context.dataStore.data
        .map { preferences ->
            val itemsJson = preferences[STOPPINGS] ?: "[]"
            Gson().fromJson(itemsJson, Array<StoppingListDS>::class.java).toList()
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
    val getCareTakerId: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[CARETAKERID] ?: ""
        }

    suspend fun saveCareTakerId(id: String) {
        try {
            context.dataStore.edit { preferences ->
                preferences[CARETAKERID] = id
            }
        } catch (e: Exception) {
            Log.e("Storedata", "Token Exception")
        }
    }

 /*   suspend fun savePass(id: List<PassengerDetailResponse>?) {
        try {
            context.dataStore.edit { preferences ->
                preferences[PASS] = id
            }
        } catch (e: Exception) {
            Log.e("Storedata", "Token Exception")
        }
    }*/

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

    suspend fun saveStartService(id: String) {
        try {
            context.dataStore.edit { preference ->
                preference[STARTSERVICEID] = id
            }
        } catch (e: Exception) {
            Log.e("StoreData", "No Exception ")
        }
    }

    suspend fun saveStoppings(items: List<StoppingListDS>) {
        val itemsJson = Gson().toJson(items)
        try {
            context.dataStore.edit { preference ->
                preference[STOPPINGS] = itemsJson
            }
        } catch (e: Exception) {
            Log.e("StoreData", "No Exception ")
        }
    }


    suspend fun clearData() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    //Just for Refresh Token
    fun getToken(): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[TOKEN_KEY]
        }
    }

    suspend fun saveTokenR(token: String) {
        context.dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = token
        }
    }

    suspend fun deleteToken() {
        context.dataStore.edit { preferences ->
            preferences.remove(TOKEN_KEY)
        }
    }



}




