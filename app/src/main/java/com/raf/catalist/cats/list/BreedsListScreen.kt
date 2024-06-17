package com.raf.catalist.cats.list

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import coil.compose.SubcomposeAsyncImage
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import coil.compose.SubcomposeAsyncImage
import com.raf.catalist.R
import com.raf.catalist.cats.domain.BreedData
import com.raf.catalist.cats.list.model.BreedUiModel
import com.raf.catalist.core.compose.NoDataMessage
import com.raf.catalist.core.compose.SearchBarM3

@ExperimentalMaterial3Api
fun NavGraphBuilder.breedsListScreen(
    route: String,
    navController: NavController
) = composable(route = route){

    val breedsListViewModel = viewModel<BreedsListViewModel>()

//  Will create mutableState, so we do not have to create coroutines
    val state by breedsListViewModel.state.collectAsState()
    Log.d("Assist chip", state.query) // TODO: set query
    BreedsListScreen(
        state = state,
        eventPublisher = {
            breedsListViewModel.publishEvent(it)
        },
        onItemClick = {
            navController.navigate(route = "breeds/${it.id}")
        }
    )
}



@Composable
@ExperimentalMaterial3Api
fun BreedsListScreen(
    state: BreedsListState,
    eventPublisher: (BreedListUiEvent) -> Unit,
    onItemClick: (BreedUiModel) -> Unit
) {
    Scaffold (
        topBar = {
             TopAppBar(
                 title = { Text(text = LocalContext.current.getString(R.string.app_name)) },
                 colors = TopAppBarDefaults.topAppBarColors(
                     containerColor = MaterialTheme.colorScheme.primary,
                     titleContentColor = Color.White
                 )
             )
        },

        content = {
            if(state.loading){
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ){
                    CircularProgressIndicator()
                }
            }else if(state.error != null){
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ){
                    val errorMessage = when (state.error){
                        is BreedsListState.ListError.LoadingListFailed ->
                            "Failed to load, internet connection error. Error: ${state.error.cause?.message}"
                    }
                    Text(text = errorMessage)
                }
            }else{
                BreedsList(
                    paddingValues = it,
                    items = state.breeds,
                    onItemClick = onItemClick,
                    eventPublisher = eventPublisher,
                    query = state.query
                )
            }
        }
    )
}

@Composable
fun BreedsList(
    paddingValues: PaddingValues,
    items: List<BreedUiModel>,
    onItemClick: (BreedUiModel) -> Unit,
    eventPublisher: (BreedListUiEvent) -> Unit,
    query : String = ""
){

    val scrollState = rememberLazyListState()

    Column (
        modifier = Modifier.padding(paddingValues),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
    ){
        SearchBarM3(eventPublisher = eventPublisher, initQuery = query)
        Spacer(modifier = Modifier.height(10.dp))
        if(items.isEmpty()){
            NoDataMessage()
        }else{
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                state = scrollState
            ) {
                items(items) { item ->
                    BreedListItem(
                        data = item,
                        onClick = { onItemClick(item) },
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
fun BreedListItem(
    data: BreedUiModel,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(11.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ){
            SubcomposeAsyncImage(
                contentDescription = "cat.desc",
                modifier = Modifier
                    .height(150.dp),
                model = data.image?.url,
                contentScale = ContentScale.Crop, // Crop the image to fit
                loading = {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(36.dp),
                        )
                    }
                },
            )
            Column (
                modifier = Modifier.padding(10.dp)
            ){
                Text(text = data.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 21.sp,)
                if(data.altNames != ""){
                    Column( modifier = Modifier.padding(vertical = 5.dp)
                    ) {
                        Text(text = "Alternative names: ", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                        Text(text = data.altNames, fontSize = 15.sp)
                    }
                }
                Text(
                    modifier = Modifier.padding(vertical = 15.dp),
                    text = data.description.take(250), // Take the first 250 characters
                    maxLines = 3, // Limit to a single line
                    overflow = TextOverflow.Ellipsis, // Add ellipsis (...) if the text overflows
                    fontSize = 15.sp
                )

                val temperamentList = data.temperament.split(", ").take(3)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ){
                    for (temp in temperamentList) {
                        AssistChip(
                            onClick = { Log.d("Assist chip", "hello world") },
                            label = { Text(text = temp, fontSize = 12.sp) }
                        )
                    }
                    IconButton(onClick = {onClick()}) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = null
                        )
                    }
                }
            }
        }
    }
}


