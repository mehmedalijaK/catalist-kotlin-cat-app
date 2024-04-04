package com.raf.catalist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.raf.catalist.navigation.AppNavigation
import com.raf.catalist.core.theme.CatalistTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CatalistTheme {
                // A surface container using the 'background' color from the theme
                AppNavigation()
            }
        }
    }
}
