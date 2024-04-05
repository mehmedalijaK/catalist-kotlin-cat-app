package com.raf.catalist.cats.details

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.raf.catalist.R
import com.raf.catalist.cats.domain.BreedData
import com.raf.catalist.cats.list.BreedListItem
import com.raf.catalist.cats.list.BreedsList
import com.raf.catalist.cats.list.BreedsListState
import com.raf.catalist.cats.list.SearchBarM3
import java.util.concurrent.Flow


fun NavGraphBuilder.breedDetails(
    route: String,
    navController: NavController
) = composable(
    route = route
) {navBackStackEntry ->
    val dataId = navBackStackEntry.arguments?.getString("id")
        ?: throw IllegalArgumentException("id is required.")

    val breedDetailsViewModel = viewModel<BreedDetailsViewModel>(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                // We pass the passwordId which we read from arguments above
                return BreedDetailsViewModel(breedId = dataId) as T
            }
        },
    )

    val state = breedDetailsViewModel.state.collectAsState()

    BreedDetailScreen(
        state = state.value,
        onClick = {
            navController.navigate(route = "home")
        }
    )
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BreedDetailScreen(
    state: BreedDetailsState,
    onClick: () -> Unit
){
    Scaffold (
        topBar = {
            TopAppBar(
                title = { state.data?.let { Text(text = it.name) } },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.tertiary
                ),
                navigationIcon = {
                    IconButton(
                        onClick = {onClick()}
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
                        is BreedDetailsState.DetailsError.DataUpdateFailed ->
                            "Failed to load, internet connection error. Error: ${state.error.cause?.message}"
                    }
                    Text(text = errorMessage)
                }
            }else{
                state.data?.let { it1 -> BreedCard(it1, it) }
            }
        }
    )
}


@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BreedCard(
    data: BreedData,
    paddingValues: PaddingValues,
){
    val scrollState = rememberScrollState()
    Column (
        modifier = Modifier
            .padding(paddingValues)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
    ){
        Column(
            modifier = Modifier.fillMaxSize()
        ){
            Image(
                painter = painterResource(id = R.drawable.cat),
                contentDescription = "cat.desc",
                modifier = Modifier
                    .height(220.dp),
                contentScale = ContentScale.Crop // Crop the image to fit
            )
            Column (
                modifier = Modifier.padding(vertical = 15.dp, horizontal = 25.dp)
            ){
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = data.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 21.sp
                    )
                    Spacer(modifier = Modifier.weight(1f)) // Ensure flexible space between Text and AssistChip
                    if(data.rare != 0){
                        AssistChip(
                            onClick = { },
                            label = { Text("Rare") },
                            leadingIcon = {
                                Icon(
                                    Icons.Filled.Check,
                                    contentDescription = "Localized description",
                                    tint = Color(0xff10bf00)
                                )
                            },
                        )
                    }

                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End,
                ) {
                    Text(text = "Alternative names: ", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                    if(data.altNames.equals("")) Text(text = "/", fontSize = 15.sp)
                    else Text(text = data.altNames)
                }

                Text(
                    modifier = Modifier.padding(bottom = 15.dp),
                    text = data.description,
                    overflow = TextOverflow.Ellipsis, // Add ellipsis (...) if the text overflows
                    fontSize = 15.sp
                )

                val temperamentList = data.temperament.split(", ")
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ){
                    for (temp in temperamentList) {
                        AssistChip(
                            onClick = { Log.d("Assist chip", "hello world") },
                            label = { Text(text = temp, fontSize = 12.sp) },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                labelColor = MaterialTheme.colorScheme.tertiary
                            ),
                        )
                    }
                }

                Column (
                    modifier = Modifier.padding(vertical = 17.dp)
                ){
                    HorizontalDivider()
                    ListItem(
                        headlineContent = {Text("Origin country")},
                        supportingContent = { Text("Secondary text") },
                        trailingContent = { Text(data.origin) },
                        leadingContent = {
                            Icon(
                                Icons.Filled.LocationOn,
                                contentDescription = "Localized description",
                            )
                        }
                    )
                    HorizontalDivider()
                    ListItem(
                        headlineContent = {Text("Life span")},
                        supportingContent = { Text("Secondary text") },
                        trailingContent = { Text(data.lifeSpan + " years") },
                        leadingContent = {
                            Icon(
                                painter = painterResource(R.drawable.baseline_spa_24),
                                contentDescription = "Life",
                            )
                        }
                    )
                    HorizontalDivider()
                    ListItem(
                        headlineContent = {Text("Weight")},
                        supportingContent = { Text("Secondary text") },
                        trailingContent = { Text(data.weight.metric + " kg") },
                        leadingContent = {
                            Icon(
                                painter = painterResource(R.drawable.baseline_cookie_24),
                                contentDescription = "Weight",
                            )
                        }
                    )
                    HorizontalDivider()
                }

                Column(
                    modifier = Modifier.padding(top = 2.dp)
                ) {
                    for (dataPoint in listOf(
                        Pair("Adaptability", data.adaptability),
                        Pair("Affection level", data.affectionLevel),
                        Pair("Child friendly", data.childFriendly),
                        Pair("Dog friendly", data.dogFriendly),
                        Pair("Energy level", data.energyLevel),
                        Pair("Stranger friendly", data.strangerFriendly),
                        Pair("Intelligence", data.intelligence)
                    )) {
                        Widget(label = dataPoint.first, repeatValue = dataPoint.second)
                    }
                }
                val ctx = LocalContext.current
                Button(
                    onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(data.wikipediaUrl))
                        ctx.startActivity(intent)
                }, modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Visit website")
                        Icon(
                            painter = painterResource(R.drawable.baseline_arrow_forward_ios_24),
                            contentDescription = "Arrow forward",
                            modifier = Modifier.padding(start = 4.dp),
                            tint = Color(0xFFFFFFFF)
                        )
                    }
                }

            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun Widget(
    label: String,
    repeatValue: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RectangleShape,
        colors = CardDefaults.cardColors(
            containerColor = Color(0xfff5f5f5)
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 15.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center // Aligns items vertically in the center
        ) {
            // Left side of Row: Text
            Text(text = label)
            // Right side of Row: Icons
            HorizontalDivider(
                modifier = Modifier
                    .padding(vertical = 5.dp)
                    .width(150.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(repeatValue) {
                    Icon(
                        painter = painterResource(R.drawable.baseline_star_rate_24),
                        contentDescription = null,
                        tint = Color(0xFFFFA534)
                    )
                }
                repeat(5-repeatValue) {
                    Icon(
                        painter = painterResource(R.drawable.baseline_star_rate_24_second),
                        contentDescription = null,
                        tint = Color(0xffc7c5c5)
                    )
                }
            }
        }
    }
    Spacer(modifier = Modifier.height(16.dp)) // Adds spacing between cards
}