package com.afishapet.moviescreen.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.MarqueeAnimationMode
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.compose.rememberNavController
import com.afishapet.moviescreen.ui.navigation.NavRoot
import com.afishapet.moviescreen.ui.theme.MovieScreenTheme
import com.afishapet.moviescreen.ui.utils.LocalTopAppBarState
import dagger.hilt.android.AndroidEntryPoint

@OptIn(ExperimentalMaterial3Api::class)
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val topAppBarState = LocalTopAppBarState.current
            androidx.compose.runtime.CompositionLocalProvider(
                LocalTopAppBarState provides topAppBarState,
            ) {
                val navController = rememberNavController()
                MovieScreenTheme {
                    androidx.compose.material3.Scaffold(
                        topBar = {
                            with(LocalTopAppBarState.current) {
                                val text = rememberSaveable(
                                    inputs = arrayOf(titleText.value)
                                ) {
                                    titleText.value
                                }
                                TopAppBar(
                                    title = {
                                        Text(
                                            modifier = Modifier.basicMarquee(
                                                animationMode = MarqueeAnimationMode.Immediately
                                            ),
                                            text = text,
                                            style = MaterialTheme.typography.headlineSmall,
                                            maxLines = 1,
                                            textAlign = TextAlign.Start
                                        )
                                    },
                                    actions = actions.value
                                )
                            }
                        }
                    ) { paddingValues ->
                        NavRoot(
                            modifier = Modifier.padding(paddingValues),
                            navController = navController
                        )
                    }
                }
            }
        }
    }
}
