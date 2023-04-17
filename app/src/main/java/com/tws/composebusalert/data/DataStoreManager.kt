package com.tws.composebusalert.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map

const val MERCHANT_DATASTORE ="merchant_datastore"
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = MERCHANT_DATASTORE)

class DataStoreManager (val context: Context) {

    companion object {
        val EMAIL = stringPreferencesKey("EMAIL")
        val MOBILE_NUMBER = stringPreferencesKey("MOBILE_NUMBER")
        val BUSINESS_NAME = stringPreferencesKey("BUSINESS_NAME")
        val BUSINESS_ADDRESS = stringPreferencesKey("BUSINESS_ADDRESS")
        val BUSINESS_WEBSITE = stringPreferencesKey("BUSINESS_WEBSITE")
        val BUSINESS_CATEGORY = stringPreferencesKey("BUSINESS_CATEGORY")
        val BUSINESS_LOCATION = stringPreferencesKey("BUSINESS_LOCATION")
    }

    suspend fun saveToDataStore(merchantDetail: Detail) {
        context.dataStore.edit {
            it[EMAIL] = merchantDetail.emailAddress
            it[MOBILE_NUMBER] = merchantDetail.mobileNumber
            it[BUSINESS_NAME] = merchantDetail.businessName
            it[BUSINESS_ADDRESS] = merchantDetail.businessAddress
            it[BUSINESS_WEBSITE] = merchantDetail.businessWebsite
            it[BUSINESS_CATEGORY] = merchantDetail.businessCategory
            it[BUSINESS_LOCATION] = merchantDetail.businessLocation
        }
    }

    fun getFromDataStore() = context.dataStore.data.map {
        Detail(
            emailAddress = it[EMAIL] ?: "",
            mobileNumber = it[MOBILE_NUMBER] ?: "",
            businessName = it[BUSINESS_NAME] ?: ""
        )
    }
}