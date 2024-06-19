package com.raf.catalist.cats.gallery

import com.raf.catalist.cats.album.model.AlbumUiModel

interface AlbumGalleryContract {
    data class AlbumGalleryUiState(
        val loading: Boolean = false,
        val albums: List<AlbumUiModel> = emptyList(),
        val error: Exception? = null,
    )
}