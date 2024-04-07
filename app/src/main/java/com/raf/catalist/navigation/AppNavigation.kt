package com.raf.catalist.navigation

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.raf.catalist.cats.details.breedDetails
import com.raf.catalist.cats.list.breedsListScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation() {

//
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "home"
    ){
        breedsListScreen(
            route = "home",
            navController = navController
        )

        breedDetails(
            route = "breeds/{id}",
            navController = navController
        )
    }
}