package com.raf.catalist.cats.list

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.modifier.modifierLocalMapOf
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.raf.catalist.cats.domain.BreedData

@ExperimentalMaterial3Api
fun NavGraphBuilder.breedsListScreen(
    route: String,
    navController: NavController
) {
    composable(route = route){
        val breedsListViewModel = viewModel<BreedsListViewModel>()
//        Will create mutableState, so we do not have to create coroutines
        val state by breedsListViewModel.state.collectAsState()

        BreedsListScreen(
            state = state
        )
    }
}

@Composable
@ExperimentalMaterial3Api
fun BreedsListScreen(
    state: BreedsListState
) {

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
        /*TODO: LIST*/
    }

}