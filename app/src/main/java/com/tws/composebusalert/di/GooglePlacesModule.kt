package com.tws.composebusalert.di

import com.tws.composebusalert.map_features.GooglePlacesApi
import com.tws.composebusalert.repo.GooglePlacesInfoRepository
import com.tws.composebusalert.repo.impl.GooglePlacesInfoRepositoryImplementation
import com.tws.composebusalert.usecase.GetDirectionInfo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object GooglePlacesModule {

    @Provides
    @Singleton
    fun provideGetDirectionInfo(repository: GooglePlacesInfoRepository): GetDirectionInfo {
        return GetDirectionInfo(repository = repository)
    }

    @Provides
    @Singleton
    fun provideDirectionInfoRepository(api: GooglePlacesApi): GooglePlacesInfoRepository{
        return GooglePlacesInfoRepositoryImplementation(api = api)
    }

    @Provides
    @Singleton
    fun provideGooglePlacesApi(): GooglePlacesApi{
        return Retrofit.Builder()
            .baseUrl(GooglePlacesApi.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GooglePlacesApi::class.java)
    }
}