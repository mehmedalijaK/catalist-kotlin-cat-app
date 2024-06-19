package com.raf.catalist.users.auth

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.raf.catalist.R
import com.raf.catalist.cats.list.BreedsListScreen
import com.raf.catalist.cats.list.BreedsListViewModel
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.raf.catalist.cats.list.BreedListUiEvent
import com.raf.catalist.db.user.User
import com.skydoves.cloudy.Cloudy


@ExperimentalMaterial3Api
fun NavGraphBuilder.user(
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
        if(state.value.user == null)
            LoginScreen(eventPublisher = {
                userViewModel.publishEvent(it)
            },)
        else navController.navigate("home")
    }


}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun LoginScreen(
    eventPublisher: (UserUiEvent) -> Unit,
) {
    var name by remember { mutableStateOf(TextFieldValue("")) }
    var lastName by remember { mutableStateOf(TextFieldValue("")) }
    var username by remember { mutableStateOf(TextFieldValue("")) }
    var email by remember { mutableStateOf(TextFieldValue("")) }

    var nameError by remember { mutableStateOf(false) }
    var lastNameError by remember { mutableStateOf(false) }
    var usernameError by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf(false) }

    fun validate(): Boolean {
        var isValid = true
        nameError = name.text.isEmpty()
        if (nameError) isValid = false

        lastNameError = lastName.text.isEmpty()
        if (lastNameError) isValid = false

        usernameError = username.text.isEmpty() || username.text.contains(" ")
        if (usernameError) isValid = false

        emailError = email.text.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email.text).matches()
        if (emailError) isValid = false

        return isValid
    }

    Scaffold(
        content = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Image(
                    painter = painterResource(id = R.drawable.cat),
                    contentDescription = "Cat Background",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
//                        .clip(RoundedCornerShape(30.dp))
                        .background(Color.Black.copy(alpha = 0.5f)) // Semi-transparent black overlay
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "GLAD YOU'RE HERE!",
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        textAlign = TextAlign.Center,
                        color = Color.White
                    )
                    Text(
                        text = "Please enter your information to continue.",
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    CustomTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Name") },
                        isError = nameError,
                        errorMessage = "Name cannot be empty"
                    )
                    CustomTextField(
                        value = lastName,
                        onValueChange = { lastName = it },
                        label = { Text("Last Name") },
                        isError = lastNameError,
                        errorMessage = "Last name cannot be empty"
                    )
                    CustomTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text("Username") },
                        isError = usernameError,
                        errorMessage = "Username cannot be empty or contain spaces"
                    )
                    CustomTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        isError = emailError,
                        errorMessage = "Email must be valid"
                    )

                    Button(
                        onClick = {
                            if (validate()) {
                                eventPublisher(UserUiEvent.CreateUser(
                                    firstName = name.text,
                                    lastName = lastName.text,
                                    mail = email.text,
                                    username = username.text
                                ))
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp)
                            .height(50.dp),
                        shape = RoundedCornerShape(25)
                    ) {
                        Text(text = "Sign In")
                    }
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTextField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    label: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    errorMessage: String? = null
) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(bottom = 8.dp)) {
        TextField(
            value = value,
            onValueChange = onValueChange,
            label = label,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
            ,
            singleLine = true,
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.Transparent,
                focusedContainerColor = Color.Black.copy(alpha = 0.3f),
                errorContainerColor = Color.Transparent
            ),
            isError = isError
        )
        if (isError && errorMessage != null) {
            Text(
                text = errorMessage,
                color = Color.Red,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 13.dp)
            )
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun LoginScreenPreview() {
//    LoginScreen()
//}