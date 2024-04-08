package com.raf.catalist.cats.details

import android.telecom.Call.Details
import com.raf.catalist.cats.domain.BreedData
import com.raf.catalist.cats.list.model.BreedUiModel

data class BreedDetailsState (
    val breedId: String,
    val loading: Boolean = false,
    val data: BreedUiModel? = null,
    val error: DetailsError? = null
){
    sealed class DetailsError {
        data class DataFetchFailed(val cause: Throwable? = null) : DetailsError()
    }
}