package com.raf.catalist.core.compose

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.raf.catalist.R
import com.raf.catalist.cats.list.BreedListUiEvent
import java.util.Locale

@Composable
fun NoDataMessage() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "No data available", modifier = Modifier.padding(16.dp))
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBarM3(
    eventPublisher: (BreedListUiEvent) -> Unit,
    initQuery: String = ""
){

    var query by remember { mutableStateOf(initQuery) }
    var active by remember { mutableStateOf(false) }
    var searchHistory = remember { mutableStateListOf<String>() }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val result = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            val firstResult = result?.getOrNull(0)
            query = firstResult ?: "No speech input"
            eventPublisher(BreedListUiEvent.RequestDataFilter(query))
            active = false
            if(query != "" && !searchHistory.contains(query)) searchHistory.add(0, query)
        }
    }

    var context = LocalContext.current
//    val searchHistory = listOf("Abyssinian", "Aegean", "American Bobtail","Abyssinian", "Aegean", "American Bobtail","Abyssinian", "Aegean", "American Bobtail","Abyssinian", "Aegean", "American Bobtail")

    val transition = updateTransition(targetState = active, label = "")
    val width by transition.animateDp(
        transitionSpec = {
            if (false isTransitioningTo true) {
                tween(durationMillis = 300, easing = LinearEasing)
            } else {
                tween(durationMillis = 300, easing = LinearEasing)
            }
        }, label = ""
    ) { state ->
        if (state) 0.dp else 16.dp
    }

    SearchBar(
        modifier = if (active) Modifier.fillMaxWidth() else Modifier.fillMaxWidth().padding(horizontal = width),
        query = query,
        onQueryChange = {query = it},
        onSearch = {newQuery ->
            eventPublisher(BreedListUiEvent.RequestDataFilter(newQuery))
            active = false
            if(newQuery != "" && !searchHistory.contains(newQuery)) searchHistory.add(0, newQuery)
        },
        active = active,
        onActiveChange = {
            active = it
        },
        placeholder = { Text(text = "Search cats")},
        leadingIcon = {
            Icon(imageVector = Icons.Filled.Search, contentDescription = "search")
        },
        trailingIcon = {
            Row {
                IconButton(onClick = {
                    if (!SpeechRecognizer.isRecognitionAvailable(context)) {
                        Toast.makeText(
                            context,
                            "Speech recognition is not available",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        val i = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
                        i.putExtra(
                            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                        )
                        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.ENGLISH.toString())
                        i.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say cat name")
                        launcher.launch(i)
                    }

                }) {
                    Icon(painter = painterResource(id = R.drawable.baseline_mic_24), contentDescription = "Microphone")
                }
                if (active){
                    IconButton(onClick = { if (query.isNotEmpty()) {query = ""; eventPublisher(BreedListUiEvent.RequestDataFilter(""))} else active = false}) {
                        Icon(imageVector = Icons.Filled.Close, contentDescription = "Close")
                    }
                }
            }
        },
    ) {
        searchHistory.takeLast(3).forEach { item ->
            ListItem(
                modifier = Modifier
                    .clickable {
                        eventPublisher(BreedListUiEvent.RequestDataFilter(item))
                        active = false
                        query = item }
                ,
                headlineContent = {Text(text = item)},
                leadingContent = {
                    Icon(
                        painter = painterResource(R.drawable.baseline_history_24),
                        contentDescription = "History"
                    )
                }
            )
        }
    }
}

