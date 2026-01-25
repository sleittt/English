package com.example.bebeka

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.rememberNavController
import com.example.bebeka.data.Prefs
import com.example.bebeka.data.updateLocale
import com.example.bebeka.ui.theme.BebekaTheme
import com.google.firebase.FirebaseApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Инициализируем Firebase один раз
        try {
            FirebaseApp.initializeApp(this)
            println("✅ Firebase initialized")
        } catch (e: Exception) {
            println("⚠️ Firebase already initialized or error: ${e.message}")
        }

        val prefs = Prefs(this)
        updateLocale(this, prefs.language)
        enableEdgeToEdge()

        setContent {
            val navController = rememberNavController()
            val context = LocalContext.current
            BebekaTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Navigation(
                        navController = navController,
                        innerPadding = Modifier.padding(innerPadding),
                        context = context
                    )
                }
            }
        }
    }
}