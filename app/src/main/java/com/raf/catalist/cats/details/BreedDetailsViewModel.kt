package com.raf.catalist.cats.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raf.catalist.cats.repository.BreedsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class BreedDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: BreedsRepository
) : ViewModel() {

    private val breedId: String = savedStateHandle.breedId

    private val _state = MutableStateFlow(BreedDetailsState(breedId = breedId))
    val state = _state.asStateFlow()
    private fun setState(reducer: BreedDetailsState.() -> BreedDetailsState) =
        _state.getAndUpdate(reducer)


    init{
//        observeBreedDetails()
        fetchBreedDetails()
    }

//    private fun observeBreedDetails() {
//        viewModelScope.launch {
//            repository.observeBreedDetails(breedId = breedId)
//                .filterNotNull()
//                .collect {
//                    setState { copy(data = it) }
//                }
//        }
//    }

    private fun fetchBreedDetails() {
        viewModelScope.launch {
            setState { copy(loading = true) }
            try {
                withContext(Dispatchers.IO) {
                    repository.fetchBreedDetails(breedId = breedId)
                }
            } catch (error: IOException) {
                setState {
                    copy(error = BreedDetailsState.DetailsError.DataFetchFailed(cause = error))
                }
            } finally {
                setState { copy(loading = false) }
            }
        }
    }
}


inline val SavedStateHandle.breedId: String
    get() = checkNotNull(get("breedId")) { "breedId is mandatory" }