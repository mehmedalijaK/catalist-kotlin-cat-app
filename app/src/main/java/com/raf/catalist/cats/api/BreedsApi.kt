package com.raf.catalist.cats.api

import com.raf.catalist.cats.api.model.BreedApiModel
import com.raf.catalist.cats.api.model.ImageApiModel
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface BreedsApi {

    @GET("breeds")
    suspend fun getAllBreeds(): List<BreedApiModel>

//    Did not use getBreedDetails
    @GET("breeds/{breed_id}")
    suspend fun getBreedDetails(
        @Path("breed_id") breedId: String,
    ): BreedApiModel

    @GET("images/search")
    suspend fun getBengalImages(
        @Query("limit") limit: Int = 10,
        @Query("breed_ids") breedIds: String = "beng",
    ): List<ImageApiModel>
}