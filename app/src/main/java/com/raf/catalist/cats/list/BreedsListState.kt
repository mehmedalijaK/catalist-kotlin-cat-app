package com.raf.catalist.cats.list

import com.raf.catalist.cats.api.model.BreedApiModel
import com.raf.catalist.cats.domain.BreedData
import com.raf.catalist.cats.list.model.BreedUiModel

data class BreedsListState(
    val loading: Boolean = false,
    val breeds: List<BreedUiModel> = emptyList(),
    val error: ListError? = null,
    val query: String = ""
){

//     Class with a closed set of subclasses. Define a restricted class hierarchy in which
//     subclasses are predefined and finite.
     sealed class ListError{
          data class LoadingListFailed(val cause: Throwable? = null) : ListError()
     }
}
