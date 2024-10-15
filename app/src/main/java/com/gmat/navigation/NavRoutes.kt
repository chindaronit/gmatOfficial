package com.gmat.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.gmat.ui.screen.login.Register
import com.gmat.ui.screens.login.Login
import com.gmat.ui.screens.profile.AboutUs
import com.gmat.ui.screens.profile.EditProfileDetails
import com.gmat.ui.screens.profile.FAQ
import com.gmat.ui.screens.profile.Languages
import com.gmat.ui.screens.profile.Profile
import com.gmat.ui.state.UserState
import com.gmat.ui.viewModel.UserViewModel

sealed class NavRoutes(val route: String) {
    data object Profile : NavRoutes("profile")
    data object Language : NavRoutes("profile/language")
    data object AboutUs : NavRoutes("profile/about")
    data object EditDetails : NavRoutes("profile/editDetails")
    data object FAQ : NavRoutes("profile/faq")
    data object TransactionHistory : NavRoutes("history")
    data object Rewards : NavRoutes("reward")
    data object Login : NavRoutes("login")
    data object OTP : NavRoutes("otp")
    data object Register : NavRoutes("register")
    data object AddTransactionDetails : NavRoutes("addTransactionDetails")
    data object TransactionReceipt : NavRoutes("receipt")
    data object UpgradeQR : NavRoutes("upgradeQr")
    data object UpgradedQR : NavRoutes("upgradedQr")
    data object TransactionChat : NavRoutes("transactionChat")
    data object Home : NavRoutes("home")

    fun withArgs(vararg args: String):String {
        return buildString {
            append(route)
            args.forEach { args ->
                append("/$args")
            }
        }
    }
}

val authScreens = mapOf<String, @Composable (navController: NavController, userViewModel: UserViewModel, userState: UserState, authToken: String) -> Unit>(
    NavRoutes.Login.route to { navController, userViewModel, userState,_ -> Login(navController = navController, userState = userState ,onUserEvents= userViewModel::onEvent) },
    NavRoutes.Register.route to { navController, userViewModel, userState, authToken -> Register(navController = navController,userState=userState,onUserEvents = userViewModel::onEvent, authToken = authToken) }
)

val settingScreens = mapOf<String, @Composable (navController: NavController, userViewModel: UserViewModel, userState: UserState, authToken: String) -> Unit>(
    NavRoutes.Language.route to { navController,_,_,_ -> Languages(navController = navController) },
    NavRoutes.AboutUs.route to { navController,_,_,_ -> AboutUs(navController = navController) },
    NavRoutes.EditDetails.route to { navController,userViewModel,userState, authToken -> EditProfileDetails(navController = navController,userState=userState, onUserEvents = userViewModel::onEvent, authToken = authToken) },
    NavRoutes.FAQ.route to { navController,_,_,_ -> FAQ(navController = navController) }
)