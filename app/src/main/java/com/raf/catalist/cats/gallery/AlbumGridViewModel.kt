package com.raf.catalist.cats.gallery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raf.catalist.cats.album.model.AlbumUiModel
import com.raf.catalist.cats.api.model.ImageApiModel
import com.raf.catalist.cats.details.BreedDetailsState
import com.raf.catalist.cats.repository.BreedsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AlbumGalleryViewModel(
    private val breedId: String,
    private val repository: BreedsRepository = BreedsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AlbumGalleryContract.AlbumGalleryUiState())
    val state = _state.asStateFlow()
    private fun setState(reducer: AlbumGalleryContract.AlbumGalleryUiState.() -> AlbumGalleryContract.AlbumGalleryUiState) = _state.update(reducer)

    init {
        fetchAlbums()
    }

    private fun fetchAlbums() {
        viewModelScope.launch {
            setState { copy(loading = true) }
            try {
                val albums = withContext(Dispatchers.IO) {
                    repository.getImages(breedId = breedId)
                }
                setState { copy(albums = albums.map { it.asAlbumUiModel() }) }

            } catch (error: Exception) {
                // TODO Handle error
            }
            setState { copy(loading = false) }
        }
    }


    private fun ImageApiModel.asAlbumUiModel() = AlbumUiModel(
        id = this.id,
        url = this.url,
        width = this.width,
        height = this.height
    )

}