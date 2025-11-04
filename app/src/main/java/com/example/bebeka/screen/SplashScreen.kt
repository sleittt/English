package com.example.bebeka.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.bebeka.Routes
import com.example.bebeka.logik.SessionManager

@Composable
fun SplashScreen(navController: NavController) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        // Проверяем, есть ли активная сессия
        val userId = SessionManager.getCurrentUserId(context)
        if (userId != -1L) {
            // Пользователь уже вошел - переходим на главный экран
            navController.navigate(Routes.Main.route) {
                popUpTo(Routes.Splash.route) { inclusive = true }
            }
        } else {
            // Нет активной сессии - переходим на выбор языка или онбординг
            navController.navigate(Routes.onBoard1.route) {
                popUpTo(Routes.Splash.route) { inclusive = true }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator()
        Spacer(modifier = Modifier.height(16.dp))
        Text("Проверка сессии...")
    }
}