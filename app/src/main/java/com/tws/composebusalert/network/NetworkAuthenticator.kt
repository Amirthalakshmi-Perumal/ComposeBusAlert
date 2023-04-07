package com.tws.composebusalert.network

import com.tws.composebusalert.di.Settings
import com.tws.composebusalert.preference.PreferenceManager
import com.tws.composebusalert.responses.Route
import com.tws.composebusalert.webservice.AppSettingDataSource
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import javax.inject.Inject

/**
 * Authenticator class to handle the token failure
 * @param appSettingDataSource is the service call which has base app api calls
 * */
class NetworkAuthenticator(private val appSettingDataSource: AppSettingDataSource) : Authenticator {

    @Inject
    lateinit var settings: Settings

    @Inject
    lateinit var preferenceManager: PreferenceManager

  /*  override fun authenticate(route: Route?, response: Response): Request? {
        var refreshToken: String? = settings.token
        try {
            refreshToken = appSettingDataSource.refreshToken().blockingGet().token
        } catch (e: Exception) {
            e.printStackTrace()
        }

        settings.token = refreshToken
        refreshToken?.let { preferenceManager.storeAndUpdateAccessToken(it) }
        return response.request.newBuilder()
            .header("Authorization", String.format("Bearer %s", settings.token))
            .build()
    }
*/
    override fun authenticate(route: okhttp3.Route?, response: Response): Request? {

      var refreshToken: String? = settings.token
      try {
          refreshToken = appSettingDataSource.refreshToken().blockingGet().token
      } catch (e: Exception) {
          e.printStackTrace()
      }
      settings.token = refreshToken
      refreshToken?.let { preferenceManager.storeAndUpdateAccessToken(it) }
      return response.request.newBuilder()
          .header("Authorization", String.format("Bearer %s", settings.token))
          .build()
    }
}
