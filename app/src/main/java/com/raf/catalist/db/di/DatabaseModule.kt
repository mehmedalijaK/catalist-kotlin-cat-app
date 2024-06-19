package com.raf.catalist.db.di

import com.raf.catalist.db.AppDatabase
import com.raf.catalist.db.AppDatabaseBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun providesDatabase(builder: AppDatabaseBuilder) : AppDatabase = builder.build()
}