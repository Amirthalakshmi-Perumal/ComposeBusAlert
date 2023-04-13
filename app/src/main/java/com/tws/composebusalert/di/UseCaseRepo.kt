package com.tws.composebusalert.di

import com.tws.composebusalert.preference.PreferenceManager
import com.tws.composebusalert.repo.AuthorizationRepo
import com.tws.composebusalert.usecase.AuthUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton



@Module
@InstallIn(ViewModelComponent::class)
object UseCaseRepo {


    @Provides
    @ViewModelScoped
    fun provideAuthUseCase(
        authorizationRepo: AuthorizationRepo
    ) : AuthUseCase{
        return AuthUseCase(authorizationRepo)
    }
/*
  @Provides
    @ViewModelScoped
    fun provideAuthUseCase(
        authorizationRepo: AuthorizationRepo,
        preferenceManager: PreferenceManager
    ) : AuthUseCase{
        return AuthUseCase(authorizationRepo, preferenceManager)
    }
*/

}
