package com.raf.catalist.cats.gallery

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raf.catalist.cats.album.model.AlbumUiModel
import com.raf.catalist.cats.api.model.ImageApiModel
import com.raf.catalist.cats.details.breedId
import com.raf.catalist.cats.repository.BreedsRepository
import com.raf.catalist.db.breed.Image
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AlbumGalleryViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: BreedsRepository
) : ViewModel() {

    private val breedId: String = savedStateHandle.breedId
    private val _state = MutableStateFlow(AlbumGalleryContract.AlbumGalleryUiState())
    val state = _state.asStateFlow()
    private fun setState(reducer: AlbumGalleryContract.AlbumGalleryUiState.() -> AlbumGalleryContract.AlbumGalleryUiState) = _state.update(reducer)

    init {
        fetchAlbums()
        observeAlbums()
    }

    private fun observeAlbums() {
        viewModelScope.launch {
            // Which will observe all changes to our passwords
            withContext(Dispatchers.IO){
                repository.observeAlbumsFlow(breedId).collect {
                    setState { copy(albums = it.map {it.asImageUi()}) }
                }
            }

        }
    }

    private fun fetchAlbums() {
        viewModelScope.launch {
            setState { copy(loading = true) }
            try {
                withContext(Dispatchers.IO) {
                    repository.getImages(breedId = breedId)
                }
            } catch (error: Exception) {
                setState { copy(error = error) }
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

    private fun Image.asImageUi() =
        AlbumUiModel(
            id = this.id,
            url = "" + this.url,
            width = this.width,
            height = this.height,
        )

}

inline val SavedStateHandle.breedId: String
    get() = checkNotNull(get("breedId")) { "breedId is mandatory" }