package com.gmat.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.gmat.ui.screen.login.OTP
import com.gmat.ui.screens.home.HomeScreen
import com.gmat.ui.screens.merchant.UpgradeQR
import com.gmat.ui.screens.merchant.UpgradedQR
import com.gmat.ui.screens.profile.Profile
import com.gmat.ui.screens.rewards.Rewards
import com.gmat.ui.screens.transaction.AddTransactionDetails
import com.gmat.ui.screens.transaction.TransactionChat
import com.gmat.ui.screens.transaction.TransactionHistory
import com.gmat.ui.screens.transaction.TransactionReceipt
import com.gmat.ui.viewModel.ScannerViewModel

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
    scannerViewModel: ScannerViewModel
) {

    NavHost(navController, startDestination = NavRoutes.Home.route) {
        animatedComposable(NavRoutes.Profile.route) {
            Profile(navController)
        }

        animatedComposable(NavRoutes.Rewards.route) {
            Rewards(navController)
        }

        animatedComposable(NavRoutes.UpgradeQR.route) {
            val state by scannerViewModel.state.collectAsState()
            UpgradeQR(
                navController = navController,
                scannerState = state,
                onScannerEvent = scannerViewModel::onEvent
            )
        }

        animatedComposable(NavRoutes.UpgradedQR.route) {
            UpgradedQR(navController = navController)
        }

        animatedComposable(NavRoutes.Home.route) {
            val state by scannerViewModel.state.collectAsState()
            HomeScreen(
                navController = navController,
                scannerState = state,
                onScannerEvent = scannerViewModel::onEvent
            )
        }

        animatedComposable(NavRoutes.TransactionChat.route) {
            TransactionChat(navController = navController)
        }

        animatedComposable(NavRoutes.TransactionHistory.route) {
            TransactionHistory(navController = navController)
        }

        animatedComposable(NavRoutes.AddTransactionDetails.route) {
            val state by scannerViewModel.state.collectAsState()
            AddTransactionDetails(
                navController = navController,
                scannerState = state,
                onScannerEvent = scannerViewModel::onEvent
            )
        }

        animatedComposable(NavRoutes.TransactionReceipt.route) {
            TransactionReceipt(navController = navController)
        }

        animatedComposable(
            route = NavRoutes.OTP.route + "/{verificationId}",
            arguments = listOf(
                navArgument("verificationId") {
                    type = NavType.StringType
                    defaultValue = ""
                    nullable = false
                }
            )) { entry ->
            OTP(navController = navController, verificationId = entry.arguments?.getString("verificationId") ?: "")
        }

        authScreens.forEach { (route, screen) ->
            if (route == NavRoutes.Login.route) {
                slideInComposable(route) {
                    screen(navController)
                }
            } else {
                animatedComposable(route) {
                    screen(navController)
                }
            }
        }

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

