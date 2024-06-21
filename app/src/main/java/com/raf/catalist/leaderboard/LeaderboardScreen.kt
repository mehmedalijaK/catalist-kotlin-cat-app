package com.raf.catalist.leaderboard

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.raf.catalist.leaderboard.model.QuizResult


@ExperimentalMaterial3Api
fun NavGraphBuilder.leaderboard(
    route: String,
    navController: NavController
) = composable(route = route){

    val leaderboardViewModel = hiltViewModel<LeaderboardViewModel>()
//

    val state by leaderboardViewModel.state.collectAsState()


    LeaderboardList(state.leaders)
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaderboardList(quizResults: List<LeaderboardUiModel>) {
    Scaffold (
        topBar = {
            TopAppBar(
                title = { Text(text = "Leaderboard") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                )
            )
        }, content = { paddingValue ->
            LazyColumn (modifier = Modifier.padding(paddingValue)){
                items(quizResults.size) { index ->
                    LeaderboardItem(quizResult = quizResults[index], position = index)
                }
            }
        }
    )


}

@Composable
fun LeaderboardItem(quizResult: LeaderboardUiModel, position: Int) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Nickname and Result
        Column {
            Text(text = quizResult.nickname, style = MaterialTheme.typography.bodyLarge)
            Text(text = "Score: ${quizResult.result}", style = MaterialTheme.typography.bodyLarge)
        }

        Spacer(modifier = Modifier.weight(1f))

        // Position
        Text(text = "#${position + 1}", style = MaterialTheme.typography.headlineLarge, color = Color.Gray)
    }
}