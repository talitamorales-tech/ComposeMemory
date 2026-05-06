package com.talitamorales.composememory

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.android.gms.ads.MobileAds
import com.talitamorales.composememory.ads.AppOpenAdController
import com.talitamorales.composememory.ui.theme.ComposeMemoryTheme
import com.talitamorales.composememory.ui.views.CardsCarouselScreen
import com.talitamorales.composememory.ui.views.InitialMenuScreen
import com.talitamorales.composememory.ui.views.MemoryGameScreen
import com.talitamorales.composememory.ui.views.OnboardScreen
import com.talitamorales.composememory.ui.views.ThemeSelectionScreen
import com.talitamorales.composememory.viewmodel.FakeGameViewModel
import com.talitamorales.composememory.viewmodel.GameViewModel
import com.talitamorales.composememory.viewmodel.GameViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    companion object {
        private const val ORIENTATION_TAG = "CM-Orientation"
    }

    private var currentRouteOrientation: Int = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
    private lateinit var appOpenAdController: AppOpenAdController

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        appOpenAdController = AppOpenAdController(
            appContext = applicationContext,
            adUnitId = getString(R.string.admob_app_open)
        )
        lifecycleScope.launch(Dispatchers.IO) {
            MobileAds.initialize(this@MainActivity) {
                runOnUiThread {
                    appOpenAdController.preload()
                }
            }
        }
        setContent {
            ComposeMemoryTheme {


                val navController = rememberNavController()
                DisposableEffect(navController) {
                    val listener = NavController.OnDestinationChangedListener { _, destination, _ ->
                        val route = destination.route
                        Log.d(
                            ORIENTATION_TAG,
                            "Route changed route=$route requested=${orientationName(requestedOrientation)} " +
                                "config=${configOrientationName(resources.configuration.orientation)}"
                        )
                        applyOrientationForRoute(route)
                    }

                    navController.addOnDestinationChangedListener(listener)
                    onDispose {
                        navController.removeOnDestinationChangedListener(listener)
                    }
                }

                Scaffold(modifier = Modifier.fillMaxSize()) {

                    NavHost(
                        navController = navController,
                        startDestination = "splash"
                    ) {

                        composable("splash") {
                            OnboardScreen(navController)
                        }

                        composable("initialMenu") {
                            InitialMenuScreen(navController)
                        }

                        composable("themeSelection") {
                            ThemeSelectionScreen(navController)
                        }

                        composable("cardsCarousel") {
                            CardsCarouselScreen(
                                onClose = {
                                    val backToMenu = navController.popBackStack(
                                        route = "initialMenu",
                                        inclusive = false
                                    )
                                    if (!backToMenu) {
                                        navController.navigate("initialMenu")
                                    }
                                }
                            )
                        }

                        composable(
                            route = "memoryGame/{themeId}/{difficultyId}",
                            arguments = listOf(
                                navArgument("themeId") { type = NavType.IntType },
                                navArgument("difficultyId") { type = NavType.IntType }
                            )
                        ) { backStackEntry ->
                            val themeId = backStackEntry.arguments?.getInt("themeId") ?: 1
                            val difficultyId = backStackEntry.arguments?.getInt("difficultyId") ?: 1

                            val viewModel: GameViewModel = viewModel(
                                factory = GameViewModelFactory(
                                    themeId = themeId,
                                    difficultyId = difficultyId
                                ),
                                viewModelStoreOwner = backStackEntry
                            )

                            MemoryGameScreen(viewModel)
                        }
                    }
                }
            }
        }

    }

    override fun onResume() {
        super.onResume()
        if (::appOpenAdController.isInitialized) {
            appOpenAdController.showOnReturnIfAvailable(this)
        }
    }

    override fun onStop() {
        if (::appOpenAdController.isInitialized) {
            appOpenAdController.markAppBackgrounded()
        }
        super.onStop()
    }

    override fun onDestroy() {
        if (::appOpenAdController.isInitialized) {
            appOpenAdController.clear()
        }
        super.onDestroy()
    }

    private fun applyOrientationForRoute(route: String?) {
        if (route == null) {
            Log.d(
                ORIENTATION_TAG,
                "applyOrientationForRoute route=null ignored; keeping " +
                    orientationName(currentRouteOrientation)
            )
            return
        }

        val desiredOrientation = when {
            route?.startsWith("memoryGame") == true ->
                ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            route == "cardsCarousel" ->
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            route == "initialMenu" || route == "themeSelection" || route == "splash" ->
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            else ->
                if (currentRouteOrientation != ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED) {
                    currentRouteOrientation
                } else {
                    ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                }
        }

        Log.d(
            ORIENTATION_TAG,
            "applyOrientationForRoute route=$route desired=${orientationName(desiredOrientation)} " +
                "currentRequested=${orientationName(requestedOrientation)} " +
                "lastApplied=${orientationName(currentRouteOrientation)} " +
                "config=${configOrientationName(resources.configuration.orientation)}"
        )

        if (currentRouteOrientation != desiredOrientation || requestedOrientation != desiredOrientation) {
            currentRouteOrientation = desiredOrientation
            requestedOrientation = desiredOrientation
            Log.d(
                ORIENTATION_TAG,
                "Orientation applied route=$route -> ${orientationName(desiredOrientation)}"
            )
        }
    }

    private fun orientationName(value: Int): String = when (value) {
        ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED -> "UNSPECIFIED"
        ActivityInfo.SCREEN_ORIENTATION_PORTRAIT -> "PORTRAIT"
        ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE -> "LANDSCAPE"
        ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE -> "SENSOR_LANDSCAPE"
        ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE -> "USER_LANDSCAPE"
        else -> value.toString()
    }

    private fun configOrientationName(value: Int): String = when (value) {
        Configuration.ORIENTATION_LANDSCAPE -> "LANDSCAPE"
        Configuration.ORIENTATION_PORTRAIT -> "PORTRAIT"
        Configuration.ORIENTATION_UNDEFINED -> "UNDEFINED"
        else -> value.toString()
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
