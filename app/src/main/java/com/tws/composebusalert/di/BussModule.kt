package com.tws.composebusalert.di


import android.app.Application
import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.tws.composebusalert.BuildConfig
import com.tws.composebusalert.network.HttpInterceptor
import com.tws.composebusalert.network.NetworkAuthenticator
import com.tws.composebusalert.preference.PreferenceManager
import com.tws.composebusalert.viewmodel.DriverLoginViewModel
import com.tws.composebusalert.webservice.AppSettingDataSource
import com.tws.composebusalert.webservice.BusDataSource
import com.tws.composebusalert.webservice.UserDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object BussModule {

    private val client = OkHttpClient.Builder().build()
   /* @Provides
    fun provideDriverLoginViewModel(
        aaa: Aaaa,
        daaaa: Addd,
        aaImpl: AaImpl,
        application: Application
    ): DriverLoginViewModel {
        return DriverLoginViewModel(aaa, daaaa, aaImpl, application)
    }*/
  /*  private val client1 = OkHttpClient.Builder().addInterceptor { chain ->
        val newRequest = chain.request().newBuilder().addHeader(
            "Authorization",
            "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJwcm9maWxlIjoiZjRmMGRiYTctMTc0MS00YzRjLWI1YzUtNDBkMGJiN2QwMmNiIiwiaWF0IjoxNjgxMzY0OTQ4LCJleHAiOjE2ODE0NTEzNDh9.tz1hma10j10Hrv5J_G2O19htZRsHXD22zBK2Ld5BzJg"
        ).build()
        chain.proceed(newRequest)
    }.build()*/

    @Provides
    @Singleton
    fun createOkHttpClient(@ApplicationContext context: Context): OkHttpClient {

        val clientBuilder = OkHttpClient.Builder().connectTimeout(TIME_OUT, TimeUnit.SECONDS)
            .readTimeout(TIME_OUT, TimeUnit.SECONDS).writeTimeout(TIME_OUT, TimeUnit.SECONDS)

        clientBuilder.addInterceptor(HttpInterceptor(context))

        if (BuildConfig.DEBUG) {
            val loggingInterceptor = HttpLoggingInterceptor()
            loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            clientBuilder.addInterceptor(loggingInterceptor)
        }
        return clientBuilder.authenticator(
            NetworkAuthenticator(createAppSettingWebService(clientBuilder.build()))
        )
//                return clientBuilder
            .build()
    }

    private fun createAppSettingWebService(okHttpClient: OkHttpClient): AppSettingDataSource {
        val retrofit = Retrofit.Builder().baseUrl(SERVER_URL).client(okHttpClient)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create()).build()

        return retrofit.create(AppSettingDataSource::class.java)
    }

    @Provides
    @Singleton
    fun createBussDataSource(
        okHttpClient: OkHttpClient
    ): BusDataSource {
        val retrofit = Retrofit.Builder().baseUrl(SERVER_URL).client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create()).build()

        return retrofit.create(BusDataSource::class.java)
    }


    @Provides
    @Singleton
    fun providePreferenceManager(
        @ApplicationContext context: Context, settings: Settings
    ): PreferenceManager {
        return PreferenceManager(context, settings)
    }

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder().baseUrl(SERVER_URL)
            .addConverterFactory(GsonConverterFactory.create()).client(client).build()
    }

   /* @Provides
    @Singleton
    fun provideRetrofit1(): Retrofit {
        return Retrofit.Builder().baseUrl(SERVER_URL)
            .addConverterFactory(GsonConverterFactory.create()).client(client1).build()
    }*/

    @Provides
    @Singleton
    fun provideUserApi(retrofit: Retrofit): UserDataSource {
        return retrofit.create(UserDataSource::class.java)
    }

  /*  @Provides
    @Singleton
    fun provideUserApi1(retrofit: Retrofit): UserDataSource {
        return retrofit.create(UserDataSource::class.java)
    }*/

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

}