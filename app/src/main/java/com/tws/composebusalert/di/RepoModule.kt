package com.tws.composebusalert.di

import com.tws.composebusalert.repo.AuthorizationRepo
import com.tws.composebusalert.repo.impl.AuthorizationRepoImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepoModule {

    @Singleton
    @Binds
    abstract fun provideAuthorizationRepo(authorizationRepoImpl: AuthorizationRepoImpl) : AuthorizationRepo

   /* @Singleton
    @Binds
    abstract fun provideUserRepository(userRepositoryImpl: AuthorizationRepoImpl): AuthorizationRepo
*/
}