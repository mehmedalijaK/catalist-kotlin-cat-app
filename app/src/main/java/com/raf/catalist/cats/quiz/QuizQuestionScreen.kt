package com.raf.catalist.cats.quiz

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import coil.compose.SubcomposeAsyncImage
import com.mxalbert.zoomable.Zoomable
import com.raf.catalist.R
import com.raf.catalist.cats.list.model.BreedUiModel
import com.raf.catalist.cats.quiz.model.UserAnswer
import com.raf.catalist.db.game.Game
import com.raf.catalist.leaderboard.model.QuizResultUser
import kotlinx.coroutines.launch

@ExperimentalMaterial3Api
fun NavGraphBuilder.quiz(
    route: String,
    navController: NavController
) = composable(route = route){

    val quizViewModel = hiltViewModel<QuizViewModel>()
    val state by quizViewModel.state.collectAsState()

    if(state.generatingQuestions){
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ){
            CircularProgressIndicator()
        }
    }else if (!state.done){
        Quiz(
            state = state,
            eventPublisher = {quizViewModel.publishEvent(it)},
            navController = navController
        )
    }else{
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ){
            val result : Double = (state.numRight * 2.5 * (1 + (state.secondTime+120) / 300)).coerceAtMost(maximumValue = 100.00)
            LaunchedEffect(state.done) {
                quizViewModel.publishEvent(QuizUiEvent.postGame(Game(
                    score = result,
                    rightAnswers = state.numRight,
                    userId = 1
                )))
            }
            CongratulationsScreen(result, navController, state,  eventPublisher = {quizViewModel.publishEvent(it)})
        }

    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Quiz(
    state: QuizUiState,
    eventPublisher: (QuizUiEvent) -> Unit,
    navController: NavController
) {
    var selectedFirst by remember { mutableStateOf(false) }
    var selectedSecond by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf(false) }
    var remainingTime by remember { mutableStateOf(300) }


    var showDialog by remember { mutableStateOf(false) }

    // Back button handling
    BackHandler {
        showDialog = true
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(text = "End Quiz") },
            text = { Text(text = "Do you want to end the quiz and return to the home screen? Your progress will not be saved.") },
            confirmButton = {
                TextButton(onClick = {
                    showDialog = false
                    navController.popBackStack()
                }) {
                    Text("Yes")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("No")
                }
            }
        )
    }

    Scaffold (
        topBar = {
            TopAppBar(
                title = { Text(text = LocalContext.current.getString(R.string.app_name)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                ),
            )
        }, content = { paddingValues ->

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

                    CircularCountdownTimer(totalTime = 300, remainingTime = remainingTime, onTimeChange = { newTime -> remainingTime = newTime },
                        selectedFirst, selectedSecond, state, eventPublisher)
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
                            .fillMaxWidth((0.1f * (state.questionNo-1)) / 2)
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 5.dp),
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
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .clickable {
                                                    if (index == 0) {
                                                        if (selectedFirst) {
                                                            selectedFirst = false
                                                        } else {
                                                            selectedFirst = true
                                                            selectedSecond = false
                                                        }
                                                    } else {
                                                        if (selectedSecond) {
                                                            selectedSecond = false
                                                        } else {
                                                            selectedSecond = true
                                                            selectedFirst = false
                                                        }
                                                    }

                                                    error = false
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
                    onClick = {
                        if(!selectedFirst && !selectedSecond) {
                            error = true
                        }else{
                            if(selectedFirst)
                                eventPublisher(QuizUiEvent.nextQuestion(UserAnswer(questionNo = state.questionNo, indexAnswer = 0, timeRemaining = remainingTime)))
                            if(selectedSecond)
                                eventPublisher(QuizUiEvent.nextQuestion(UserAnswer(questionNo = state.questionNo, indexAnswer = 1, timeRemaining = remainingTime)))

                            selectedSecond = false
                            selectedFirst = false
                        }


                    },
                    colors = if(error) ButtonDefaults.outlinedButtonColors(containerColor = Color.Red)
                    else ButtonDefaults.buttonColors(containerColor = Color(0xFFFFAB00), ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Text(text = "Next", color = Color.White)
                }
            }
        },
    )
}

@SuppressLint("DefaultLocale")
@Composable
fun CircularCountdownTimer(totalTime: Int, remainingTime: Int , onTimeChange: (Int) -> Unit,
                           selectedFirst: Boolean, selectedSecond: Boolean,state: QuizUiState,
                           eventPublisher: (QuizUiEvent) -> Unit)   {

    val progress = remember { Animatable(1f) }
    val coroutineScope = rememberCoroutineScope()
    var time by remember { mutableStateOf(totalTime) }

    LaunchedEffect(key1 = Unit) {
        coroutineScope.launch {
            while (time > 0 && !state.done) {
                onTimeChange(time)
                time--
                progress.animateTo(
                    targetValue = time / totalTime.toFloat(),
                    animationSpec = tween(durationMillis = 1000)
                )
            }
        }
    }

    if(time == 0){
        if(selectedFirst)
            eventPublisher(QuizUiEvent.nextQuestion(UserAnswer(questionNo = state.questionNo, indexAnswer = 0, timeRemaining = 0)))
        else if(selectedSecond)
            eventPublisher(QuizUiEvent.nextQuestion(UserAnswer(questionNo = state.questionNo, indexAnswer = 1, timeRemaining = 0)))
        else
            eventPublisher(QuizUiEvent.nextQuestion(UserAnswer(questionNo = state.questionNo, indexAnswer = -1, timeRemaining = 0)))
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
            text = String.format("%02d:%02d", time / 60, time % 60),
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

fun getRandomBreeds(breeds: List<BreedUiModel>): List<BreedUiModel> {
    if (breeds.size < 2) throw IllegalArgumentException("Breeds list must contain at least two elements.")

    val firstBreed = breeds.random()
    var secondBreed: BreedUiModel

    do {
        secondBreed = breeds.random()
    } while (secondBreed == firstBreed)

    return listOf(firstBreed, secondBreed)
}


@Composable
fun CongratulationsScreen(
    result: Double,
    navController: NavController,
    state: QuizUiState,
    eventPublisher: (QuizUiEvent) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(150.dp)
                .clip(CircleShape)
                .background(Color(0xfff7bb6d))
        ) {
            Image(
                painter = painterResource(id = R.drawable.cat_ill), // Replace with your own drawable resource
                contentDescription = "Profile Image",
                modifier = Modifier.size(100.dp),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Your Score",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Gray
        )

        Text(
            text = "${state.numRight}/20",
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Congratulations!",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            text = "Great job! You have done well",
            fontSize = 16.sp,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(150.dp)
                    .height(45.dp)
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
                    Text(text = "${result} points", fontSize = 16.sp, color = Color.White)
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                navController.popBackStack()
            }, modifier = Modifier.fillMaxWidth().padding(top = 7.dp),
            contentPadding = PaddingValues(16.dp),

            ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Go to quiz home",
                    color = Color.White
                )
                Icon(
                    painter = painterResource(R.drawable.baseline_arrow_forward_ios_24),
                    contentDescription = "Arrow forward",
                    modifier = Modifier.padding(start = 4.dp),
                    tint = Color(0xFFFFFFFF)
                )
            }
        }

        Button(
            onClick = {
                eventPublisher(QuizUiEvent.postOnline(QuizResultUser(
                    result = result,
                    category = 3,
                    nickname = ""
                )))
                navController.popBackStack()
            }, modifier = Modifier.fillMaxWidth().padding(top = 7.dp),
            contentPadding = PaddingValues(16.dp),

            ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Share your result",
                    color = Color.White
                )
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
