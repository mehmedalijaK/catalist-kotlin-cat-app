package com.raf.catalist.cats.list

import android.util.Log
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
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
import com.raf.catalist.R
import com.raf.catalist.cats.domain.BreedData
import com.raf.catalist.core.compose.NoDataMessage

@ExperimentalMaterial3Api
fun NavGraphBuilder.breedsListScreen(
    route: String,
    navController: NavController
) = composable(route = route){

    val breedsListViewModel = viewModel<BreedsListViewModel>()

//  Will create mutableState, so we do not have to create coroutines
    val state by breedsListViewModel.state.collectAsState()

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
    onItemClick: (BreedData) -> Unit
) {
    Scaffold (
        topBar = {
             TopAppBar(
                 title = { Text(text = LocalContext.current.getString(R.string.app_name)) },
                 colors = TopAppBarDefaults.topAppBarColors(
                     containerColor = MaterialTheme.colorScheme.primary,
                     titleContentColor = MaterialTheme.colorScheme.tertiary
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
                    eventPublisher = eventPublisher
                )
            }
        }
    )
}

@Composable
fun BreedsList(
    paddingValues: PaddingValues,
    items: List<BreedData>,
    onItemClick: (BreedData) -> Unit,
    eventPublisher: (BreedListUiEvent) -> Unit
){

    val scrollState = rememberScrollState()
    Column (
        modifier = Modifier.padding(paddingValues),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
    ){
        SearchBarM3(eventPublisher = eventPublisher)
        Spacer(modifier = Modifier.height(10.dp))
        if(items.isEmpty()){
            NoDataMessage()
        }else{
            Column(
                modifier = Modifier
                    .verticalScroll(scrollState)
                    .fillMaxSize(),
            ){
                items.forEach {
                    Column {
                        key(it.id) {
                            BreedListItem(
                                data = it,
                                onClick = {
                                    onItemClick(it)
                                },
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBarM3(
    eventPublisher: (BreedListUiEvent) -> Unit,
){

    var query by remember { mutableStateOf("") }
    var active by remember { mutableStateOf(false) }

    val searchHistory = listOf("Abyssinian", "Aegean", "American Bobtail","Abyssinian", "Aegean", "American Bobtail","Abyssinian", "Aegean", "American Bobtail","Abyssinian", "Aegean", "American Bobtail")

    val transition = updateTransition(targetState = active, label = "")
    val width by transition.animateDp(
        transitionSpec = {
            if (false isTransitioningTo true) {
                tween(durationMillis = 300, easing = LinearEasing)
            } else {
                tween(durationMillis = 300, easing = LinearEasing)
            }
        }, label = ""
    ) { state ->
        if (state) 0.dp else 16.dp
    }

    SearchBar(
        modifier = if (active) Modifier.fillMaxWidth() else Modifier.fillMaxWidth().padding(horizontal = width),
        query = query,
        onQueryChange = {query = it},
        onSearch = {newQuery ->
                   eventPublisher(BreedListUiEvent.RequestDataFilter(newQuery))
                    active = false
        },
        active = active,
        onActiveChange = {
            active = it
        },
        placeholder = { Text(text = "Search cats")},
        leadingIcon = {
            Icon(imageVector = Icons.Filled.Search, contentDescription = "search")
        },
        trailingIcon = {
            Row {
                IconButton(onClick = { }) {
                    Icon(painter = painterResource(id = R.drawable.baseline_mic_24), contentDescription = "Microphone")
                }
                if (active){
                    IconButton(onClick = { if (query.isNotEmpty()) query = "" else active = false}) {
                        Icon(imageVector = Icons.Filled.Close, contentDescription = "Close")
                    }
                }
            }
        },
    ) {
        searchHistory.takeLast(3).forEach { item ->
            ListItem(
                modifier = Modifier
                    .clickable { query = item }
                   ,
                headlineContent = {Text(text = item)},
                leadingContent = {
                    Icon(
                        painter = painterResource(R.drawable.baseline_history_24),
                        contentDescription = "History"
                    )
                }
            )
        }
    }
}

@Composable
fun BreedListItem(
    data: BreedData,
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
            Image(
                painter = painterResource(id = R.drawable.cat),
                contentDescription = "cat.desc",
                modifier = Modifier
                    .height(150.dp),
                contentScale = androidx.compose.ui.layout.ContentScale.Crop // Crop the image to fit
            )
            Column (
                modifier = Modifier.padding(10.dp)
            ){
                Text(text = data.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 21.sp,)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End,
                ) {
                    Text(text = "Alternative names: ", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                    if(data.altNames == "") Text(text = "/", fontSize = 15.sp)
                    else Text(text = data.altNames)
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


