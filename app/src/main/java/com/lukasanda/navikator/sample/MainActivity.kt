package com.lukasanda.navikator.sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.lukasanda.navikator.sample.ui.detail.Detail
import com.lukasanda.navikator.sample.ui.home.Home
import com.lukasanda.navikator.sample.ui.theme.NaviKatorTheme

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NaviKatorTheme {
                val navController = rememberAnimatedNavController()
                Scaffold(
                    content = { innerPadding ->
                        Surface(
                            modifier = Modifier
                                .padding(innerPadding)

                                .fillMaxSize(),
                            color = MaterialTheme.colorScheme.surface
                        ) {
                            Column {
                                NavigationComponent(navHostController = navController)
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun NavigationComponent(navHostController: NavHostController) {
    NavHost(
        navController = navHostController,
        startDestination = Home.createRoute(),
    ) {
        Home.composable(this, navHostController)
        Detail.composable(this, navHostController)
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    NaviKatorTheme {
        Greeting("Android")
    }
}