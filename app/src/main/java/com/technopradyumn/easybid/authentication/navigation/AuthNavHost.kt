package com.technopradyumn.easybid.authentication.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.NavHostController
import com.technopradyumn.easybid.authentication.presentation.auth.AuthScreen
import com.technopradyumn.easybid.authentication.presentation.auth.NameOrUserNameScreen
import kotlinx.serialization.Serializable

@Serializable
sealed class AuthRoute {
    @Serializable
    data object Auth : AuthRoute()

    @Serializable
    data class NameOrUserName(val emailOrPhone: String = "", val password: String = "") : AuthRoute()

    @Serializable
    data object LogInSignUpWithGoogle : AuthRoute()
}

fun NavGraphBuilder.authNavGraph(navController: NavHostController) {
    composable<AuthRoute.Auth> {
        AuthScreen(
            navController = navController,
            onLoginSignUpGoogleClick = {
                navController.navigate(
                    AuthRoute.NameOrUserName()
                )
            },
        )
    }

    composable<AuthRoute.NameOrUserName> { backStackEntry ->
        val token = backStackEntry.arguments?.getString("token")
        if (token != null) {
            NameOrUserNameScreen(
                navController = navController,
                token = token
            )
        }
    }

}


