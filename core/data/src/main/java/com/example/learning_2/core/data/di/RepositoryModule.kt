package com.example.learning_2.core.data.di

import com.example.learning_2.core.data.repository.ExcursionRepository
import com.example.learning_2.core.data.repository.ExcursionRepositoryImpl
import com.example.learning_2.core.data.repository.VacationRepository
import com.example.learning_2.core.data.repository.VacationRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindVacationRepository(impl: VacationRepositoryImpl): VacationRepository

    @Binds
    @Singleton
    abstract fun bindExcursionRepository(impl: ExcursionRepositoryImpl): ExcursionRepository
}
