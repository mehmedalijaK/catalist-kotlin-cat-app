package com.raf.catalist.cats.gallery

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import coil.compose.SubcomposeAsyncImage
import com.mxalbert.zoomable.Zoomable
import com.raf.catalist.R
import com.raf.catalist.cats.album.AlbumGridScreen
import com.raf.catalist.cats.album.AlbumGridViewModel
import com.raf.catalist.cats.details.BreedDetailsViewModel


fun NavGraphBuilder.albumGallery(
    route: String,
    arguments: List<NamedNavArgument>,
    onClose: () -> Unit)
        = composable(
    route = route,
    arguments = arguments
) { navBackStackEntry ->
    val albumGalleryViewModel = hiltViewModel<AlbumGalleryViewModel>(navBackStackEntry)

    val state = albumGalleryViewModel.state.collectAsState()
    AlbumGalleryScreen(state = state.value, onClose = onClose)

}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun AlbumGalleryScreen(
    state: AlbumGalleryContract.AlbumGalleryUiState,
    onClose: () -> Unit
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
        content = { paddingValues ->
            val pagerState = rememberPagerState (
                pageCount = {
                    state.albums.size
                }
            )
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
                pageSpacing = 10.dp,
                contentPadding = paddingValues,
                key = {
                    val album = state.albums[it]
                    album.id
                }
            ) { pageIndex ->
                val album = state.albums[pageIndex]
                Box(contentAlignment =  Alignment.BottomCenter){
                    Zoomable {
                        SubcomposeAsyncImage(
                            model = album.url,
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
                            contentScale = ContentScale.FillWidth, // Crop to fit the aspect ratio
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    Text(
                        modifier = Modifier
                            .background(color = Color.Black.copy(alpha = 0.5f))
                            .fillMaxWidth()
                            .padding(all = 8.dp),
                        text = "Photo ${pageIndex + 1} of ${state.albums.size}",
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = Color.White,
                    )
                }
            }
        }
    )
}