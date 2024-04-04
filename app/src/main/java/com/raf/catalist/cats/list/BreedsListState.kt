package com.raf.catalist.cats.list

import com.raf.catalist.cats.domain.BreedData

data class BreedsListState(
     val loading: Boolean = false,
     val breeds : List<BreedData> = emptyList(),
     val error: ListError? = null
){

//     Class with a closed set of subclasses. Define a restricted class hierarchy in which
//     subclasses are predefined and finite.
     sealed class ListError{
          data class LoadingListFailed(val cause: Throwable? = null) : ListError()
     }
}
