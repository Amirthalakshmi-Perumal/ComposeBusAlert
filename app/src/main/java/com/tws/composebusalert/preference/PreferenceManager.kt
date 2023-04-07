package com.tws.composebusalert.preference

import android.content.Context
import android.content.SharedPreferences
import android.location.Location
import com.google.gson.Gson
import com.tws.composebusalert.di.Settings
import com.tws.composebusalert.responses.StartLocationServiceResponse
import javax.inject.Inject

const val PREF_PARAM_TOKEN = "token"
const val PREF_PARAM_USER_ID = "userId"
const val PREF_STUDENT_CODE = "student_codes"
const val PREF_DRIVER_CODE = "driver_code"
const val PREF_DEVICE_TOKEN = "device_token"
const val PREF_DRIVER_PROFILE_ID = "driver_profile_id"
const val PREF_DRIVER_ROUTE_PICKUP_ID = "driver_route_pickup_id"
const val PREF_DRIVER_ROUTE_DROP_ID = "driver_route_drop_id"
const val PREF_DRIVER_BRANCH_ID = "driver_branch_id"
const val PREF_DRIVER_ROUTE_NAME = "driver_route_name"
const val PREF_USER_PHONE = "user_phone"
const val PREF_LOCATION_SERVICE = "location_service"
const val PREF_VEHICLE_ID = "vehicle_id"
const val PREF_LAST_LOCATION = "last_location"
const val PREF_IS_STARTED = "is_started"

/**
 * Preference manager class to store and retrieve data
 * @param context is required to the shared Preference
 * */
class PreferenceManager @Inject constructor(
    context: Context,
    private val settings: Settings
) {

/*    @Inject
    lateinit var settings: Settings*/
    private val preferences: SharedPreferences =
        context.getSharedPreferences("prefs", Context.MODE_PRIVATE)

    init {
        settings.token = getValue(PREF_PARAM_TOKEN)
        settings.pickupId = getValue(PREF_DRIVER_ROUTE_PICKUP_ID)
        settings.dropId = getValue(PREF_DRIVER_ROUTE_DROP_ID)
        settings.branchId = getValue(PREF_DRIVER_BRANCH_ID)
        settings.phoneNo = getValue(PREF_USER_PHONE)
    }

    /**
     * Used to store the string the values in preference
     * @param [key] preference key used to store values
     * @param [id] preference value
     * */
    fun storeValue(key: String, id: String) {
        preferences.edit().putString(key, id).apply()
    }

    /**
     * Used to get the string the values from preference
     * @param [key] preference key used to store values
     */
    fun getValue(key: String): String? =
        preferences.getString(key, "")

    private fun storeStringSet(key: String, list: MutableSet<String>) {
        val set: MutableSet<String> = HashSet()
        set.addAll(list)
        preferences.edit().putStringSet(key, set).apply()
    }

    private fun getStringSet(key: String): MutableSet<String>? =
        preferences.getStringSet(key, null)

 /**
     * Update the auth token to preference and also update the static variable [Settings.token]
     * [storeAndUpdateAccessToken] is used
     * @param token auth token received on driver login
     * */
    fun storeAndUpdateAccessToken(token: String) {
        storeValue(PREF_PARAM_TOKEN, token)
        settings.token = token
    }

    var lastLocation: Location?
        get() = convertStringToJson(preferences.getString(PREF_LAST_LOCATION, null))
        set(value) = preferences.edit().putString(PREF_LAST_LOCATION, convertJsonToString(value))
            .apply()

    var startLocationServiceResponseModel: StartLocationServiceResponse?
        get() = convertStringToJson(preferences.getString(PREF_LOCATION_SERVICE, null))
        set(value) = preferences.edit().putString(PREF_LOCATION_SERVICE, convertJsonToString(value))
            .apply()

    private fun <T> convertJsonToString(model: T): String? {
        return try {
            Gson().toJson(model)
        } catch (e: Exception) {
            null
        }
    }

    private inline fun <reified T> convertStringToJson(jsonString: String?): T? {
        return try {
            Gson().fromJson(jsonString, T::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

}