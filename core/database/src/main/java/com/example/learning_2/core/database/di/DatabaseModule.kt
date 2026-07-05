package com.example.learning_2.core.database.di

import android.content.Context
import androidx.room.Room
import com.example.learning_2.core.database.AppDatabase
import com.example.learning_2.core.database.dao.ExcursionDao
import com.example.learning_2.core.database.dao.VacationDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

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
}
