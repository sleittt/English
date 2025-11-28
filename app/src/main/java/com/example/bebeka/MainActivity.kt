package com.example.bebeka

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.rememberNavController
import com.example.bebeka.data.Prefs
import com.example.bebeka.data.updateLocale
import com.example.bebeka.ui.theme.BebekaTheme
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val prefs = Prefs(this)
        updateLocale(this, prefs.language)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            val context = LocalContext.current
            val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            BebekaTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val context = LocalContext.current
                    Navigation(navController, Modifier.padding(innerPadding), context)
                }
            }
        }

    }
}


