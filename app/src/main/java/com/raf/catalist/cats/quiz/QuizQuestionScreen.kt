package com.raf.catalist.cats.quiz

import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
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
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.PaintingStyle.Companion.Stroke
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import com.mxalbert.zoomable.Zoomable
import com.raf.catalist.R
import com.raf.catalist.cats.album.model.AlbumUiModel
import com.raf.catalist.cats.list.BreedListUiEvent
import com.raf.catalist.cats.list.BreedsListState
import com.raf.catalist.cats.list.BreedsListViewModel
import com.raf.catalist.cats.list.model.BreedUiModel
import com.raf.catalist.cats.quiz.model.Answer
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.internal.wait
import kotlin.random.Random

@ExperimentalMaterial3Api
fun NavGraphBuilder.quiz(
    route: String,
    navController: NavController
) = composable(route = route){

//    val breedsListViewModel = hiltViewModel<BreedsListViewModel>()

//  Will create mutableState, so we do not have to create coroutines
//    val state by breedsListViewModel.state.collectAsState()
//    Log.d("Assist chip", state.query) // TODO: set query
    val quizViewModel = hiltViewModel<QuizViewModel>()
    val state by quizViewModel.state.collectAsState()

    if(state.generatingQuestions){
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ){
            CircularProgressIndicator()
        }
    }else{
        Quiz(
            state = state
        )
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Quiz(
    state: QuizUiState,
) {
    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }
    var selectedFirst by remember { mutableStateOf(false) }
    var selectedSecond by remember { mutableStateOf(false) }


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

            val selectedAnswer = remember { mutableStateOf("") }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp, vertical = 18.dp),
//                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Question",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${state.questionNo}/20",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    CircularCountdownTimer(totalTime = 5 * 60)
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .background(Color.LightGray)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(0.1f * (state.questionNo - 1))
                            .background(Color(0xFFFFAB00))
                    )
                }
                Text(
                    text = state.question[state.questionNo-1].questionText,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 16.dp)
                )

                LazyColumn(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 5.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    val models = arrayOf(
                        state.question[state.questionNo - 1].firstBreed.image?.url,
                        state.question[state.questionNo - 1].secondBreed.image?.url
                    )
                    val models1 = arrayOf(
                        state.question[state.questionNo - 1].firstBreed,
                        state.question[state.questionNo - 1].secondBreed
                    )
                    items(2) { index ->


                        Card(
                            modifier = Modifier
                                .padding(8.dp)
                                .fillMaxWidth()
                                .height(195.dp)
//                                .aspectRatio(1f)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(contentAlignment = Alignment.BottomCenter,
                                    modifier = if ((index == 0 && selectedFirst) || (index == 1 && selectedSecond)) {
                                        Modifier.border(width = 5.dp, color = Color(0xFF50C878), shape = RoundedCornerShape(8.dp))
                                    } else {
                                        Modifier
                                    },

                                ) {
                                    Zoomable {
                                        SubcomposeAsyncImage(
                                            model = models[index],
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
                                            contentScale = ContentScale.FillWidth,
                                            modifier = Modifier.fillMaxSize().clickable {
                                                if(index == 0){
                                                    if(selectedFirst){
                                                        selectedFirst = false
                                                    }else{
                                                        selectedFirst = true
                                                        selectedSecond = false
                                                    }
                                                }else{
                                                    if(selectedSecond){
                                                        selectedSecond = false
                                                    }else{
                                                        selectedSecond = true
                                                        selectedFirst = false
                                                    }
                                                }
                                            }
                                        )
                                    }
                                    Text(
                                        modifier = Modifier
                                            .background(color = Color.Black.copy(alpha = 0.5f))
                                            .padding(all = 8.dp),
                                        text = models1[index].name,
                                        textAlign = TextAlign.Center,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }

                }
                Button(
                    onClick = { /* Handle next button click */ },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFAB00)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Text(text = "Next", color = Color.White)
                }
            }
        }
    )
}

@Composable
fun AnswerButton(answer: String, selectedAnswer: String, onClick: (String) -> Unit) {
    val isSelected = answer == selectedAnswer
    val backgroundColor = if (isSelected) Color(0xFFF5B07D) else Color.White
    val textColor = Color.Black

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick(answer) }
            .background(backgroundColor, shape = RoundedCornerShape(8.dp))
            .padding(16.dp)
    ) {
        Text(text = answer, color = textColor)
    }
}

@Composable
fun CircularCountdownTimer(totalTime: Int) {
    var remainingTime by remember { mutableStateOf(totalTime) }
    val progress = remember { Animatable(1f) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(key1 = Unit) {
        coroutineScope.launch {
            while (remainingTime > 0) {
                remainingTime--
                progress.animateTo(
                    targetValue = remainingTime / totalTime.toFloat(),
                    animationSpec = tween(durationMillis = 1000)
                )
            }
        }
    }

    Box(contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.size(65.dp)) {
            drawArc(
                color = Color(0xFFFFAB00),
                startAngle = -90f,
                sweepAngle = 360 * progress.value,
                useCenter = false,
                style = Stroke(width = 22.2f, cap = StrokeCap.Round, join = StrokeJoin.Round)
            )
        }
        Text(
            text = String.format("%02d:%02d", remainingTime / 60, remainingTime % 60),
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

fun getRandomBreeds(breeds: List<BreedUiModel>): List<BreedUiModel> {
    if (breeds.size < 2) throw IllegalArgumentException("Breeds list must contain at least two elements.")

    val firstBreed = breeds.random(Random(System.currentTimeMillis()))
    var secondBreed: BreedUiModel

    do {
        secondBreed = breeds.random(Random(System.currentTimeMillis()))
    } while (secondBreed == firstBreed)

    return listOf(firstBreed, secondBreed)
}

@Composable
fun AnswerButtonWithImage(
    breed: BreedUiModel,
    answer: String,
    selectedAnswer: String,

) {
    val isSelected = answer == selectedAnswer
    val backgroundColor = if (isSelected) Color(0xFFF5B07D) else Color.White
    val textColor = Color.Black

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { }
            .background(backgroundColor, shape = RoundedCornerShape(8.dp))
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            SubcomposeAsyncImage(
                model = breed.image?.url,
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
                modifier = Modifier
                    .width(250.dp)
                    .height(100.dp),
                contentScale = ContentScale.Crop, // Crop to fit the aspect ratio
//                modifier = Modifier.fillMaxSize()
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = answer, color = textColor)
        }
    }
}
