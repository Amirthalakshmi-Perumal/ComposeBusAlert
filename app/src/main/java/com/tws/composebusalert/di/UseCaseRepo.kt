package com.tws.composebusalert.di

import com.tws.composebusalert.repo.AuthorizationRepo
import com.tws.composebusalert.usecase.AuthUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton



@Module
@InstallIn(SingletonComponent::class)
object UseCaseRepo {

    @Singleton
    @Provides
    fun provideAuthUseCase(
        authorizationRepo: AuthorizationRepo
    ) : AuthUseCase{
        return AuthUseCase(authorizationRepo)
    }

}
