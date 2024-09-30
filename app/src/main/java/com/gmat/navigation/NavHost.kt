package com.gmat.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.gmat.ui.screens.profile.Profile
import com.gmat.ui.viewModel.ScannerViewModel

@Composable
fun AppNavHost(navController: NavHostController = rememberNavController(),scannerViewModel: ScannerViewModel) {

    NavHost(navController, startDestination = NavRoutes.Profile.route) {
        animatedComposable(NavRoutes.Profile.route) {
            Profile(navController)
        }

//        authScreens.forEach { (route, screen) ->
//            if (route == NavRoutes.Login.route) {
//                slideInComposable(route) {
//                    screen(navController)
//                }
//            } else {
//                animatedComposable(route) {
//                    screen(navController)
//                }
//            }
//        }

        settingScreens.forEach { (route, screen) ->
            if (route == NavRoutes.Profile.route) {
                slideInComposable(route) {
                    screen(navController)
                }
            } else {
                animatedComposable(route) {
                    screen(navController)
                }
            }
        }
    }
}
