package com.raf.catalist.navigation

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.raf.catalist.cats.album.catAlbumGrid
import com.raf.catalist.cats.details.breedDetails
import com.raf.catalist.cats.gallery.albumGallery
import com.raf.catalist.cats.list.breedsListScreen
import com.raf.catalist.users.auth.user

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "login"
    ){
        user(
            route = "login",
            navController = navController
        )

        breedsListScreen(
            route = "home",
            navController = navController
        )

        breedDetails(
            route = "breeds/{id}",
            navController = navController
        )

        catAlbumGrid(
            route = "album/{id}",
            arguments = listOf(
                navArgument(name = "id"){
                    nullable = false
                    type = NavType.StringType
                }
            ),
            onClose = {
                navController.navigateUp()
            },
            onAlbumClick = {
                navController.navigate(route = "albums/${it}")
            }
        )

        albumGallery(
            route = "albums/{id}",
            arguments = listOf(
                navArgument(name = "id"){
                    nullable = false
                    type = NavType.StringType
                }
            ),
            onClose = {
                navController.navigateUp()
            }
        )
    }
}