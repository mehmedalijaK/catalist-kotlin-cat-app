package com.raf.catalist.cats.api.DI

import com.raf.catalist.cats.api.BreedsApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.create
import javax.inject.Singleton
@Module
@InstallIn(SingletonComponent::class)

object BreedModule {
    @Provides
    @Singleton
    fun provideBreedsApi(retrofit: Retrofit): BreedsApi = retrofit.create()
}
