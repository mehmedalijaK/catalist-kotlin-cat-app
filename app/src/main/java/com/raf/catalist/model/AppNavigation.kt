package com.raf.catalist.model

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation() {

    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "home" ){
        composable(route = "home"){
            BreedsListScreen(

            )
        }
        composable(
            route = "details"
        ){
            BreedDetailScreen(

            )
        }
    }
}