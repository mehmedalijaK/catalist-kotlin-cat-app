package com.raf.catalist.cats.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raf.catalist.cats.repository.BreedsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

class BreedDetailsViewModel constructor(
    private val breedId: String,
    private val repository: BreedsRepository = BreedsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(BreedDetailsState(breedId = breedId))
    val state = _state.asStateFlow()
    private fun setState(reducer: BreedDetailsState.() -> BreedDetailsState) =
        _state.getAndUpdate(reducer)


    init{
        observeBreedDetails()
        fetchBreedDetails()
    }

    private fun observeBreedDetails() {
        viewModelScope.launch {
            repository.observeBreedDetails(breedId = breedId)
                .filterNotNull()
                .collect {
                    setState { copy(data = it) }
                }
        }
    }

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


