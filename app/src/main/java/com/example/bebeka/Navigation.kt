package com.example.bebeka

import android.content.Context
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.bebeka.screen.games.WordsScreen
import com.example.profik.screen.games.animalScreen
import com.example.bebeka.screen.LanguageSelectScreen
import com.example.bebeka.screen.LogInScreen
import com.example.bebeka.screen.MainScreen
import com.example.bebeka.screen.ProfileScreen
import com.example.bebeka.screen.SignUpPage1Screen
import com.example.bebeka.screen.SignUpPage2Screen
import com.example.bebeka.screen.SplashScreen
import com.example.bebeka.screen.games.Sentence
import com.example.profik.screen.games.animalGuess
import com.example.bebeka.screen.imageOnBoard1
import com.example.bebeka.screen.imageOnBoard2
import com.example.bebeka.screen.imageOnBoard3
import com.example.bebeka.screen.onBoarding

public sealed class Routes(val route: String) {
    object onBoard1: Routes("first")
    object onBoard2: Routes("second")
    object onBoard3: Routes("third")
    object langSelect: Routes("langSelect")
    object Signup1:Routes ("signup1")
    object Signup2:Routes ("signup2/{firstName}/{lastName}/{email}")
    object Login : Routes("Login")
    object Main : Routes("Main")
    object Animal : Routes("GuessAnimal")
    object AnimalGuess : Routes("GuessAnimalAnswer/{answer}") {
        fun createRoute(answer: String) = "GuessAnimalAnswer/$answer"
    }
    object Words : Routes("Words")
    object Audition : Routes("Audio")
    object Splash : Routes("Splash")
    object Profile : Routes("Profile")
    object Sentence : Routes("Sentence")

}

// Функция для создания маршрута Signup2 с параметрами
fun createSignup2Route(firstName: String, lastName: String, email: String): String {
    return "signup2/${Uri.encode(firstName)}/${Uri.encode(lastName)}/${Uri.encode(email)}"
}
@Composable
fun Navigation(navController: NavHostController, innerPadding: Modifier, context: Context){
    NavHost(
        navController = navController,
        startDestination = Routes.Splash.route,
    ){
        composable(Routes.Splash.route) { SplashScreen(navController = navController) }
        composable(Routes.onBoard1.route){
            onBoarding(
                modifier = innerPadding, navController=navController,
                image = R.drawable.onboarding1,
                header = "Confidence in your words",
                text = "With conversation-based learning, you'll be talking from lesson one",
                buttonText="Next",
                route = Routes.onBoard2.route,
                progress = { imageOnBoard1() }
            )
        }
        composable(Routes.onBoard2.route) {
            onBoarding(
                modifier = innerPadding, navController=navController,
                image = R.drawable.onboarding2,
                header = "Take your time to learn",
                text = "Develop a habit of learning and make it a part of your daily routine",
                buttonText="More",
                route = Routes.onBoard3.route,
                progress = { imageOnBoard2() }
            )
        }
        composable(Routes.onBoard3.route) {
            onBoarding(
                modifier = innerPadding, navController=navController,
                image = R.drawable.onboarding3,
                header = "The lessons you need to learn",
                text = "Using a variety of learning styles to learn and retain",
                route = Routes.langSelect.route,
                buttonText="Choose a language",
                progress = { imageOnBoard3() }
            )
        }
        composable(
            "${Routes.langSelect.route}?firstChange={firstChange}",
            arguments = listOf(
                navArgument("firstChange") {
                    type = NavType.BoolType
                    defaultValue = true
                }
            )
        ){ backStackEntry ->
            val firstChange = backStackEntry.arguments?.getBoolean("firstChange") ?: true
            LanguageSelectScreen(navController = navController, firstChange = firstChange)
        }
        composable(Routes.Signup1.route) {
            SignUpPage1Screen(
                navController = navController,
                context = context,
                onContinue = { firstName, lastName, email ->
                    navController.navigate(createSignup2Route(firstName, lastName, email))
                }
            )
        }

        composable(
            route = Routes.Signup2.route,
            arguments = listOf(
                navArgument("firstName") { type = NavType.StringType },
                navArgument("lastName") { type = NavType.StringType },
                navArgument("email") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val firstName = Uri.decode(backStackEntry.arguments?.getString("firstName") ?: "")
            val lastName = Uri.decode(backStackEntry.arguments?.getString("lastName") ?: "")
            val email = Uri.decode(backStackEntry.arguments?.getString("email") ?: "")

            SignUpPage2Screen(
                navController = navController,
                context = context,
                firstName = firstName,
                lastName = lastName,
                email = email
            )
        }
        composable(Routes.Login.route) { LogInScreen(navController = navController, context = context) }
        composable(Routes.Main.route) { MainScreen(navController = navController,  context = context) }
        composable(Routes.Animal.route) { animalScreen(navController = navController) }
        composable(
            Routes.AnimalGuess.route,
            arguments = listOf(navArgument("answer") { type = NavType.StringType })
        ) { backStackEntry ->
            val answer = backStackEntry.arguments?.getString("answer") ?: ""
            animalGuess(navController = navController, answer = answer)
        }
        composable(Routes.Words.route) { WordsScreen(navController = navController) }
        composable(Routes.Profile.route) { ProfileScreen(navController = navController) }
        composable(Routes.Sentence.route) { Sentence(navController = navController) }

    }
}
@Composable
fun BackNavigation(
    navController: NavController,
    targetRoute: String,
    enabled: Boolean = true
) {
    BackHandler(enabled = enabled) {
        navController.navigate(targetRoute) {
            popUpTo(targetRoute) { inclusive = true }
            launchSingleTop = true
        }
    }
}


