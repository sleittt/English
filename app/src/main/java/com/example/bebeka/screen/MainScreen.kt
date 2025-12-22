package com.example.bebeka.screen

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.bebeka.R
import com.example.bebeka.Routes
import com.example.bebeka.logik.AppDatabase
import com.example.bebeka.logik.SessionManager
import com.example.bebeka.logik.User
import com.example.bebeka.ui.theme.enabledButton
import com.example.bebeka.ui.theme.fredokaFonts
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// Обновите MainScreen для загрузки пользователя из базы
@Composable
fun MainScreen(navController: NavController, context: Context) {
    var topUsers by remember { mutableStateOf<List<User>>(emptyList()) }
    var currentUser by remember { mutableStateOf<User?>(null) }

    val db = remember { AppDatabase.getDatabase(context) }

    // Загружаем данные при запуске
    LaunchedEffect(Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Загружаем данные пользователя
                val currentUserId = SessionManager.getCurrentUserId(context)
                if (currentUserId != -1L) {
                    currentUser = db.userDao().getUserById(currentUserId)
                }

                // Загружаем топ пользователей для лидерборда
                topUsers = db.userDao().getTopUsers()
            } catch (e: Exception) {
                Log.e("MainScreen", "Error loading user data: ${e.message}")
            }
        }
    }

    Scaffold(topBar = {
        topMainBar(
            nickname = currentUser?.username,
            navController = navController,
            currentUser = currentUser // Передаем currentUser в topMainBar
        )
    },
        modifier = Modifier.fillMaxSize()
    ){ innerPadding ->
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(horizontal = 24.dp), horizontalAlignment = Alignment.Start) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text=stringResource(id = R.string.tops),
                fontSize = 20.sp,
                fontFamily = fredokaFonts,
                fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(8.dp))

            currentUser?.let { Text(stringResource(id = R.string.you_points, it.points)) }
                Spacer(modifier = Modifier.height(20.dp))
            }

            Text(text=stringResource(id = R.string.exc),
                fontSize = 20.sp,
                fontFamily = fredokaFonts,
                fontWeight = FontWeight.Medium)
            Spacer(Modifier.height(20.dp))
            Column(modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()) {
                Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
                    Button(onClick = {navController.navigate(Routes.Animal.route)},modifier = Modifier
                        .weight(1F)
                        , shape = RoundedCornerShape(30.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = enabledButton)) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Image(
                                ImageBitmap.imageResource(R.drawable.animal),
                                contentDescription = null,
                                modifier = Modifier.size(90.dp)
                            )
                            Text(text=stringResource(id = R.string.animal),
                                fontSize = 13.sp,
                                fontFamily = fredokaFonts,
                                fontWeight = FontWeight.Light,
                                color = Color.White)
                        }
                    }
                    Spacer(Modifier.width(15.dp))
                    Button(onClick = {navController.navigate(Routes.Words.route)},modifier = Modifier
                        .weight(1F)
                        , shape = RoundedCornerShape(30.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD6185D))) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally){
                            Image(
                                ImageBitmap.imageResource(R.drawable.words),
                                contentDescription = null,
                                modifier = Modifier.size(90.dp)
                            )
                            Text(text=stringResource(id = R.string.words),
                                fontSize = 13.sp,
                                fontFamily = fredokaFonts,
                                fontWeight = FontWeight.Light,
                                color = Color.White)
                        }
                    }
                }
                Spacer(Modifier.height(15.dp))
                Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
                    Spacer(Modifier.width(15.dp))
                    Button(onClick = {navController.navigate(Routes.Sentence.route)}, modifier = Modifier
                        .weight(1F)
                        , shape = RoundedCornerShape(30.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5BA890))) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Image(
                                ImageBitmap.imageResource(R.drawable.game),
                                contentDescription = null,
                                modifier = Modifier.size(90.dp)
                            )
                            Text(text=stringResource(id = R.string.game),
                                fontSize = 13.sp,
                                fontFamily = fredokaFonts,
                                fontWeight = FontWeight.Light,
                                color = Color.White)
                        }
                    }
                }
            }
        }
    }
