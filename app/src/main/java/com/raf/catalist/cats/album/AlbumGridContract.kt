package com.raf.catalist.cats.album

import com.raf.catalist.cats.album.model.AlbumUiModel

interface AlbumGridContract {
    data class AlbumGridUiState(
        val loading: Boolean = false,
        val albums: List<AlbumUiModel> = emptyList(),
        val breedId: String = "",
    )
}