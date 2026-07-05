package com.example.learning_2.di

import android.content.Context
import androidx.room.Room
import com.example.learning_2.dao.ExcursionDao
import com.example.learning_2.dao.VacationDao
import com.example.learning_2.data.repository.ExcursionRepository
import com.example.learning_2.data.repository.ExcursionRepositoryImpl
import com.example.learning_2.data.repository.VacationRepository
import com.example.learning_2.data.repository.VacationRepositoryImpl
import com.example.learning_2.database.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "vacation_database")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideVacationDao(db: AppDatabase): VacationDao = db.vacationDao()

    @Provides
    fun provideExcursionDao(db: AppDatabase): ExcursionDao = db.excursionDao()

    @Provides
    @Singleton
    fun provideVacationRepository(impl: VacationRepositoryImpl): VacationRepository = impl

    @Provides
    @Singleton
    fun provideExcursionRepository(impl: ExcursionRepositoryImpl): ExcursionRepository = impl
}
