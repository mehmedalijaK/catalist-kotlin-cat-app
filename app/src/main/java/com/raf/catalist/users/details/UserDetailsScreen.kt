package com.raf.catalist.users.details

import android.annotation.SuppressLint
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.raf.catalist.R
import com.raf.catalist.cats.quiz.RecentItem
import com.raf.catalist.users.auth.UserContract
import com.raf.catalist.users.auth.UserViewModel

data class UserDetailsData(
    val ranking: Int?,
    val name: String?,
    val surname: String?,
    val email: String?,
    val username: String?
)

@ExperimentalMaterial3Api
fun NavGraphBuilder.userDetails(
    route: String,
    navController: NavController
) = composable(route = route){

    val userViewModel: UserViewModel = hiltViewModel()
    val state = userViewModel.state.collectAsState()

    if(state.value.loading){
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ){
            CircularProgressIndicator()
        }
    }else {
        if(state.value.user != null) {
            val user = UserDetailsData(
                ranking = state.value.user!!.ranking,
                name = state.value.user!!.firstName,
                surname = state.value.user!!.lastName,
                username = state.value.user!!.username,
                email = state.value.user!!.mail
            )
            UserDetails(userDetails = user, state)

        }
        else navController.navigate("home") {
            popUpTo(navController.graph.startDestinationId) {
                inclusive = true
            }
        }
    }


}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun UserDetails(
    userDetails: UserDetailsData,
    state: State<UserContract.UserUiState>,

    ) {
    val scrollState = rememberLazyListState()

    Scaffold (
        topBar = {
            TopAppBar(
                title = { Text(text = "User Details") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                )
            )
        },
        content = { paddingValues ->

            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(horizontal = 0.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {

                Text(text = "Your personal information", fontSize = 20.sp, modifier = Modifier.padding(vertical = 17.dp).padding(horizontal = 11.dp))

                UserDetailsItem(label = "Ranking", value = userDetails.ranking.toString())
                UserDetailsItem(label = "Name", value = userDetails.name.toString())
                UserDetailsItem(label = "Surname", value = userDetails.surname.toString())
                UserDetailsItem(label = "Email", value = userDetails.email.toString())
                UserDetailsItem(label = "Username", value = userDetails.username.toString())
                UserDetailsItem(label = "Best Score", value = ((state.value.games.maxByOrNull { it.score })?.score
                    ?: 0).toString())

                // Edit button
                Button(
                        onClick = {
                            {}
                        }, modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 7.dp).padding(horizontal = 11.dp),
                contentPadding = PaddingValues(16.dp),

                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Edit your profile",
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
                Text(text = "Your previous results", fontSize = 20.sp, modifier = Modifier.padding(vertical = 17.dp).padding(horizontal = 11.dp))

                if(state.value.games.isEmpty()){
                    RecentItem(
                        name = "You did not play so far",
                        status = "Good Luck!"
                    )
                }else{
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(horizontal = 11.dp),
                        state = scrollState
                    ) {
                        items(state.value.games) { item ->
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
fun UserDetailsItem(label: String, value: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = Color.Gray,
                shape = RoundedCornerShape(0.dp)
            )
            .height(60.dp),
    ) {
        Box(Modifier.padding(horizontal = 11.dp)){
            Text(text = label, fontWeight = FontWeight.Bold)
        }
        Box(Modifier.padding(horizontal = 11.dp)){
            Text(text = value)
        }
    }
}