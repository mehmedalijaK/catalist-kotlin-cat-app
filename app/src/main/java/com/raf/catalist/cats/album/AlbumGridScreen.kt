package com.raf.catalist.cats.album

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import coil.compose.SubcomposeAsyncImage

import com.raf.catalist.R
import com.raf.catalist.cats.album.model.AlbumUiModel

fun NavGraphBuilder.catAlbumGrid(
    route: String,
    arguments: List<NamedNavArgument>,
    onAlbumClick: (String) -> Unit,
    onClose: () -> Unit)
        = composable(
    route = route,
    arguments = arguments
) { navBackStackEntry ->
    val dataId = navBackStackEntry.arguments?.getString("id")
        ?: throw IllegalArgumentException("id is required.")

    val albumViewModel = viewModel<AlbumGridViewModel>(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return AlbumGridViewModel(breedId = dataId) as T
            }
        },
    )

    val state = albumViewModel.state.collectAsState()
    AlbumGridScreen(state = state.value, onClose = onClose, onAlbumClick = onAlbumClick, breedId = dataId)

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlbumGridScreen(
    state: AlbumGridContract.AlbumGridUiState,
    onClose: () -> Unit,
    onAlbumClick: (String) -> Unit,
    breedId: String
){
    Scaffold (
        topBar = {
            TopAppBar(
                title = { Text(text = "Gallery") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                ),
                navigationIcon = {
                    IconButton(
                        onClick = {onClose()}
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_arrow_back_24),
                            contentDescription = "Back arrow"
                        )
                    }
                },
            )
        },
        content = {
            paddingValues ->
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = paddingValues,
                modifier = Modifier.fillMaxSize()
            ) {
                itemsIndexed( items = state.albums,
                    key = {
                        index : Int, item : AlbumUiModel ->
                        item.id
                    })
                {
                        index: Int, item: AlbumUiModel ->
                    Card(
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth()
                            .aspectRatio(1f).clickable {
                                onAlbumClick(breedId)
                            }
                    ) {
                        SubcomposeAsyncImage(
                            model = item.url,
                            loading = {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(36.dp)
                                    )
                                }
                            },
                            contentDescription = null,
                            contentScale = ContentScale.Crop, // Crop to fit the aspect ratio
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
    )
}