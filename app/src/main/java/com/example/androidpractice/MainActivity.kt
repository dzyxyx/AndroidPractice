package com.example.androidpractice

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.compose.rememberNavController
import com.example.androidpractice.di.FilterBadgeCache
import com.example.androidpractice.navigation.AppNavGraph
import com.example.androidpractice.navigation.ProfileNavGraph
import com.example.androidpractice.ui.favorites.FavoritesScreen
import com.example.androidpractice.ui.filter.FilterScreen
import com.example.androidpractice.ui.theme.AndroidPracticeTheme
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {
    private val badgeCache: FilterBadgeCache by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AndroidPracticeTheme {
                val hasActiveFilters by badgeCache.hasActiveFilters.collectAsState()
                AppContent(hasActiveFilters = hasActiveFilters)
            }
        }
    }
}

enum class AppDestinations(
    val label: String,
    val icon: ImageVector,
) {
    HOME("Главная", Icons.Default.Home),
    FAVORITES("Избранное", Icons.Default.Favorite),
    FILTER("Фильтры", Icons.Default.FilterList),
    PROFILE("Профиль", Icons.Default.Person),
}

@Composable
fun AppContent(hasActiveFilters: Boolean = false) {
    val currentDestination = rememberSaveable { mutableStateOf(AppDestinations.HOME) }
    val homeNavController = rememberNavController()
    val profileNavController = rememberNavController()

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            AppDestinations.entries.forEach { destination ->
                item(
                    icon = {
                        if (destination == AppDestinations.FILTER && hasActiveFilters) {
                            BadgedBox(badge = { Badge() }) {
                                Icon(destination.icon, contentDescription = destination.label)
                            }
                        } else {
                            Icon(destination.icon, contentDescription = destination.label)
                        }
                    },
                    label = { Text(destination.label) },
                    selected = destination == currentDestination.value,
                    onClick = { currentDestination.value = destination }
                )
            }
        }
    ) {
        when (currentDestination.value) {
            AppDestinations.HOME -> AppNavGraph(navController = homeNavController)
            AppDestinations.FAVORITES -> FavoritesScreen(
                onMovieClick = { movieId ->
                    currentDestination.value = AppDestinations.HOME
                    homeNavController.navigate("movie_detail/$movieId")
                }
            )
            AppDestinations.FILTER -> FilterScreen(
                onApply = { currentDestination.value = AppDestinations.HOME }
            )
            AppDestinations.PROFILE -> ProfileNavGraph(navController = profileNavController)
        }
    }
}
