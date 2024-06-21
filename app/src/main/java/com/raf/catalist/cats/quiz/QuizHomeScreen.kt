package com.raf.catalist.cats.quiz

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.raf.catalist.R
import com.raf.catalist.cats.list.BreedListItem

@ExperimentalMaterial3Api
fun NavGraphBuilder.quizHome(
    route: String,
    navController: NavController
) = composable(route = route){

    val quizHomeViewModel = hiltViewModel<QuizHomeViewModel>()
    val state by quizHomeViewModel.state.collectAsState()

    Log.d("msg", state.games.toString())
    QuizHomeScreen(navController, state)

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizHomeScreen(navController: NavController, state: QuizHomeUiState) {

    val scrollState = rememberLazyListState()

    Scaffold (
        topBar = {
            TopAppBar(
                title = { Text(text = LocalContext.current.getString(R.string.app_name)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                )
            )
        }, content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp, vertical = 24.dp),

            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "Hello!", fontSize = 20.sp)
                        Text(text = "Ready to play?", fontSize = 14.sp, color = Color.Gray)
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .width(80.dp)
                                .height(33.dp)
                                .clip(shape = RoundedCornerShape(5.dp, 5.dp, 5.dp, 5.dp))
                                .background(MaterialTheme.colorScheme.primary)
                             ,
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    painter = painterResource(R.drawable.baseline_diamond_24),
                                    contentDescription = "Diamond",
                                    tint = Color.White
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = "200", fontSize = 16.sp, color = Color.White)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Play and Win section
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(8.dp))
                        .padding(16.dp)
                ) {
                    Column {
                        Text(
                            text = "Play and Win",
                            fontSize = 20.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Text(
                            text = "Start a quiz now and enjoy",
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.7f),
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        Button(onClick = {
                            navController.navigate("quiz") {
                                popUpTo(navController.graph.startDestinationId) {
                                    inclusive = false
                                }
                            }
                        },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White,
                            )) {
                            Text(text = "Get Started", color = Color.Black)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Categories
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Categories", fontSize = 20.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Categories List
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                ) {
                    CategoryItem(name = "Guess the Fact")
                    Spacer(modifier = Modifier.width(14.dp))
                    CategoryItem(name = "Guess the Cat")
                    Spacer(modifier = Modifier.width(14.dp))
                    CategoryItem(name = "Left or Right Cat")
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Recent
                Text(text = "Recent results", fontSize = 20.sp)

                Spacer(modifier = Modifier.height(8.dp))

                if(state.games.isEmpty()){
                    RecentItem(
                        name = "You did not play so far",
                        status = "Good Luck!"
                    )
                }else{
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        state = scrollState
                    ) {
                        items(state.games) { item ->
                            RecentItem(
                                name = "${item.score} points",
                                status = "${item.rightAnswers} / 20 right answers"
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun CategoryItem(name: String) {
    Box(
        modifier = Modifier
            .size(100.dp)
            .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(8.dp))
            .padding(16.dp)
            .clickable {  },
        contentAlignment = Alignment.Center
    ) {
        Text(text = name, textAlign = TextAlign.Center)
    }
}

@Composable
fun RecentItem(name: String, status: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(8.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = name, fontSize = 16.sp)
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = status,
            fontSize = 14.sp
        )
    }
}