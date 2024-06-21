package com.raf.catalist.users.details

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.raf.catalist.users.auth.UserContract
import com.raf.catalist.users.auth.UserUiEvent
import com.raf.catalist.users.auth.UserViewModel
import java.util.regex.Pattern

@ExperimentalMaterial3Api
fun NavGraphBuilder.userEdit(
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
            UserEdit(userDetails = user, state, eventPublisher = {
                userViewModel.publishEvent(it)
            }, navController)

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
fun UserEdit(
    userDetails: UserDetailsData,
    state: State<UserContract.UserUiState>,
    eventPublisher: (UserUiEvent) -> Unit,
    navController: NavController,
) {

    val scrollState = rememberLazyListState()
    val (firstName, setFirstName) = remember { mutableStateOf(userDetails.name) }
    val (lastName, setLastName) = remember { mutableStateOf(userDetails.surname) }
    val (email, setEmail) = remember { mutableStateOf(userDetails.email) }
    val (username, setUsername) = remember { mutableStateOf(userDetails.username) }

    val (firstNameError, setFirstNameError) = remember { mutableStateOf("") }
    val (lastNameError, setLastNameError) = remember { mutableStateOf("") }
    val (emailError, setEmailError) = remember { mutableStateOf("") }
    val (usernameError, setUsernameError) = remember { mutableStateOf("") }

    fun validateInputs(): Boolean {
        var isValid = true

        if (firstName != null) {
            if (firstName.isBlank()) {
                setFirstNameError("First name is mandatory")
                isValid = false
            } else {
                setFirstNameError("")
            }
        }

        if (lastName != null) {
            if (lastName.isBlank()) {
                setLastNameError("Last name is mandatory")
                isValid = false
            } else {
                setLastNameError("")
            }
        }

        if (email != null) {
            if (email.isBlank()) {
                setEmailError("Email is mandatory")
                isValid = false
            } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                setEmailError("Email format is invalid")
                isValid = false
            } else {
                setEmailError("")
            }
        }

        if (username != null) {
            if (username.isBlank()) {
                setUsernameError("Username is mandatory")
                isValid = false
            } else if (!Pattern.matches("^[a-zA-Z0-9_]*$", username)) {
                setUsernameError("Username can contain only letters, numbers, and underscores")
                isValid = false
            } else {
                setUsernameError("")
            }
        }

        return isValid
    }

    Scaffold(
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

                if (firstName != null) {
                    UserDetailsItemEditable(label = "Name", value = firstName, onValueChange = setFirstName, error = firstNameError)
                }
                if (lastName != null) {
                    UserDetailsItemEditable(label = "Surname", value = lastName, onValueChange = setLastName, error = lastNameError)
                }
                if (email != null) {
                    UserDetailsItemEditable(label = "Email", value = email, onValueChange = setEmail, error = emailError)
                }
                if (username != null) {
                    UserDetailsItemEditable(label = "Username", value = username, onValueChange = setUsername, error = usernameError)
                }

                // Edit button
                Button(
                    onClick = {
                        if (validateInputs()) {
                            eventPublisher(
                                UserUiEvent.updateUser(
                                id = state.value.user?.id,
                                firstName = "" + firstName,
                                lastName =  "" + lastName,
                                mail =  "" + email,
                                username =  "" + username,
                                ranking = state.value.user?.ranking
                            ))
                            navController.popBackStack()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 7.dp)
                        .padding(horizontal = 11.dp),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Submit",
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
    )
}

@Composable
fun UserDetailsItemEditable(label: String, value: String, onValueChange: (String) -> Unit, error: String) {
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 11.dp)) {
        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(text = label, fontWeight = FontWeight.Bold)
            TextField(value = value, onValueChange = onValueChange, modifier = Modifier.fillMaxWidth())
        }
        if (error.isNotEmpty()) {
            Text(text = error, color = Color.Red, modifier = Modifier.padding(start = 12.dp, top = 4.dp))
        }
    }
}