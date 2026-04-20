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
import com.example.bebeka.firebase.FirestoreService
import com.example.bebeka.logik.AppDatabase
import com.example.bebeka.logik.SessionManager
import com.example.bebeka.logik.User
import com.example.bebeka.ui.theme.enabledButton
import com.example.bebeka.ui.theme.fredokaFonts
import com.example.bebeka.utils.DebugLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun MainScreen(navController: NavController, context: Context, firestoreService: FirestoreService) {
    var topUsers by remember { mutableStateOf<List<User>>(emptyList()) }
    var currentUser by remember { mutableStateOf<User?>(null) }

    val db = remember { AppDatabase.getDatabase(context) }

    LaunchedEffect(Unit) {
        DebugLogger.d("MainScreen", "MainScreen loaded, loading user data")
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val currentUserId = SessionManager.getCurrentUserId(context)
                DebugLogger.d("MainScreen", "Current user ID from SessionManager: $currentUserId")
                if (currentUserId != -1L) {
                    currentUser = db.userDao().getUserById(currentUserId)
                    DebugLogger.d("MainScreen", "Loaded current user: ${currentUser?.username} (points=${currentUser?.points})")
                } else {
                    DebugLogger.d("MainScreen", "No valid user ID found")
                }

                topUsers = db.userDao().getTopUsers()
                DebugLogger.d("MainScreen", "Loaded ${topUsers.size} top users for leaderboard")
            } catch (e: Exception) {
                DebugLogger.e("MainScreen", "Error loading user data: ${e.message}", e)
            }
        }
    }

    Scaffold(
        topBar = {
            topMainBar(
                nickname = currentUser?.username,
                navController = navController,
                currentUser = currentUser
            )
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp), horizontalAlignment = Alignment.Start
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            Spacer(modifier = Modifier.height(8.dp))

            currentUser?.let { Text(stringResource(id = R.string.you_points, it.points)) }
            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = stringResource(id = R.string.exc),
                fontSize = 20.sp,
                fontFamily = fredokaFonts,
                fontWeight = FontWeight.Medium
            )
            Spacer(Modifier.height(20.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
            )
            {
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = {
                            DebugLogger.d("MainScreen", "Navigate to Animal screen")
                            navController.navigate(Routes.Animal.route)
                        },
                        modifier = Modifier
                            .weight(1F),
                        shape = RoundedCornerShape(30.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = enabledButton)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Image(
                                ImageBitmap.imageResource(R.drawable.animal),
                                contentDescription = null,
                                modifier = Modifier.size(90.dp)
                            )
                            Text(
                                text = stringResource(id = R.string.animal),
                                fontSize = 13.sp,
                                fontFamily = fredokaFonts,
                                fontWeight = FontWeight.Light,
                                color = Color.White
                            )
                        }
                    }
                    Spacer(Modifier.width(15.dp))
                    Button(
                        onClick = {
                            DebugLogger.d("MainScreen", "Navigate to Words screen")
                            navController.navigate(Routes.Words.route)
                        },
                        modifier = Modifier
                            .weight(1F),
                        shape = RoundedCornerShape(30.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD6185D))
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Image(
                                ImageBitmap.imageResource(R.drawable.words),
                                contentDescription = null,
                                modifier = Modifier.size(90.dp)
                            )
                            Text(
                                text = stringResource(id = R.string.words),
                                fontSize = 13.sp,
                                fontFamily = fredokaFonts,
                                fontWeight = FontWeight.Light,
                                color = Color.White
                            )
                        }
                    }
                }
                Spacer(Modifier.height(15.dp))
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = {
                            DebugLogger.d("MainScreen", "Navigate to Sentence screen")
                            navController.navigate(Routes.Sentence.route)
                        },
                        modifier = Modifier
                            .weight(1F),
                        shape = RoundedCornerShape(30.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5BA890))
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Image(
                                ImageBitmap.imageResource(R.drawable.game),
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(90.dp)
                            )
                            Text(
                                text = stringResource(id = R.string.game),
                                fontSize = 13.sp,
                                fontFamily = fredokaFonts,
                                fontWeight = FontWeight.Light,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}