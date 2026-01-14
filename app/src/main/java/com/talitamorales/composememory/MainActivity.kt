package com.talitamorales.composememory

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.talitamorales.composememory.ui.theme.ComposeMemoryTheme
import com.talitamorales.composememory.ui.views.MemoryGameScreen
import com.talitamorales.composememory.ui.views.OnboardScreen
import com.talitamorales.composememory.viewmodel.FakeGameViewModel
import com.talitamorales.composememory.viewmodel.GameViewModel
import com.talitamorales.composememory.viewmodel.GameViewModelFactory

class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        enableEdgeToEdge()
        setContent {
            ComposeMemoryTheme {

                val navController = rememberNavController()

                Scaffold(modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        WindowInsets.navigationBars
                            .only(WindowInsetsSides.Bottom)
                            .asPaddingValues()
                    )) {

                    NavHost(
                        navController = navController,
                        startDestination = "onboard"
                    ) {

                        composable("onboard") {
                            OnboardScreen(navController)
                        }

                        composable(
                            route = "memoryGame/{id}",
                            arguments = listOf(
                                navArgument("id") { type = NavType.IntType }
                            )
                        ) { backStackEntry ->

                            val id = backStackEntry.arguments?.getInt("id")!!

                            val viewModel: GameViewModel = viewModel(
                                factory = GameViewModelFactory(id),
                                viewModelStoreOwner = backStackEntry
                            )

                            MemoryGameScreen(viewModel)
                        }
                    }
                }
            }
        }

    }
}

@Preview(showBackground = true)
@Composable
fun MemoryGameScreenPreview() {
    ComposeMemoryTheme {
        MemoryGameScreen(
            viewModel = FakeGameViewModel()
        )
    }
}

/*@Composable
fun DetailsRoute(id: String) {

    val viewModel: DetailsViewModel = viewModel(
        factory = DetailsViewModelFactory(
            id = id,
            repository = LocalRepository.current
        )
    )

    DetailsScreen(viewModel)
}*/


/*composable("memoryGame/id",
arguments = listOf(navArgument("id") { type = NavType.IntType }) { backStackEntry ->

    val viewModel: GameViewModel= viewModel()
    val id = backStackEntry.arguments?.getInt("id")!!

    viewModel.load(id)
    MemoryGameScreen(viewModel)
}*/

/*
composable("detalhes/{id}") { backStack ->
    val id = backStack.arguments?.getString("id")!!

    DetailsRoute(id = id)
}*/
