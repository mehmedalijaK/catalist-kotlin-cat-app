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
import com.raf.catalist.cats.quiz.quiz
import com.raf.catalist.cats.quiz.quizHome
import com.raf.catalist.users.auth.user

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "quizHome"
    ){
        quizHome(
            route = "quizHome",
            navController = navController
        )

        quiz(
            route = "quiz",
            navController = navController
        )

        breedsListScreen(
            route = "home",
            navController = navController
        )

        breedDetails(
            route = "breeds/{breedId}",
            navController = navController
        )

        catAlbumGrid(
            route = "album/{breedId}",
            arguments = listOf(
                navArgument(name = "breedId"){
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
            route = "albums/{breedId}",
            arguments = listOf(
                navArgument(name = "breedId"){
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