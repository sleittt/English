package com.example.bebeka.screen

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.bebeka.Routes
import com.example.bebeka.logik.AppDatabase
import com.example.bebeka.logik.SessionManager
import com.example.bebeka.logik.User
import com.example.bebeka.ui.theme.DeepBlue
import com.example.bebeka.ui.theme.enabledButton
import com.example.bebeka.ui.theme.fredokaFonts
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController) {
    val context = LocalContext.current
    var currentUser by remember { mutableStateOf<User?>(null) }
    var profileImageBitmap by remember { mutableStateOf<android.graphics.Bitmap?>(null) }
    val db = remember { AppDatabase.getDatabase(context) }

    // Функция для загрузки изображения из URI и преобразования в ByteArray
    fun loadImageBytesFromUri(uri: android.net.Uri): ByteArray? {
        return try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                inputStream.readBytes()
            }
        } catch (e: Exception) {
            null
        }
    }

    // Launcher для выбора изображения из галереи
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: android.net.Uri? ->
        uri?.let { imageUri ->
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val currentUserId = SessionManager.getCurrentUserId(context)
                    if (currentUserId != -1L) {
                        // Загружаем байты изображения
                        val imageBytes = loadImageBytesFromUri(imageUri)

                        // Сохраняем в базу данных
                        db.userDao().updateUserImage(currentUserId, imageBytes)

                        // Обновляем текущего пользователя
                        val updatedUser = db.userDao().getUserById(currentUserId)
                        withContext(Dispatchers.Main) {
                            currentUser = updatedUser
                            // Преобразуем ByteArray в Bitmap для отображения
                            updatedUser?.profileImage?.let { bytes ->
                                profileImageBitmap = android.graphics.BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e("ProfileScreen", "Error updating profile image: ${e.message}")
                }
            }
        }
    }

    // Загружаем данные пользователя и его изображение при инициализации
    LaunchedEffect(Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val currentUserId = SessionManager.getCurrentUserId(context)
                if (currentUserId != -1L) {
                    val user = db.userDao().getUserById(currentUserId)
                    withContext(Dispatchers.Main) {
                        currentUser = user
                        // Преобразуем ByteArray из БД в Bitmap для отображения
                        user?.profileImage?.let { bytes ->
                            profileImageBitmap = android.graphics.BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("ProfileScreen", "Error loading data: ${e.message}")
            }
        }
    }

    Scaffold(topBar = {
        TopAppBar(
            title = {
                Column(
                    modifier = Modifier.padding(24.dp)
                ) {
                    profileImageBitmap?.let { bitmap ->
                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = "Profile image",
                            modifier = Modifier
                                .size(134.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text(text="Your profile, ${currentUser?.username ?: "User"}",
                        fontSize = 22.sp,
                        fontFamily = fredokaFonts,
                        fontWeight = FontWeight.Medium)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = DeepBlue),
            )
    }) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(24.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Bottom
        ) {
            Button(onClick = {},
                modifier = Modifier.height(56.dp).fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = enabledButton)) {
                Text(text="Switch to dark (non-functional)",
                    fontSize = 20.sp,
                    fontFamily = fredokaFonts,
                    fontWeight = FontWeight.Medium)
            }
            Spacer(Modifier.height(10.dp))
            Button(onClick = {
                navController.navigate("${Routes.langSelect.route}?firstChange=false")
            },
                modifier = Modifier.height(56.dp).fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = enabledButton)) {
                Text(text="Switch language",
                    fontSize = 20.sp,
                    fontFamily = fredokaFonts,
                    fontWeight = FontWeight.Medium)
            }
            Spacer(Modifier.height(10.dp))
            Button(onClick = {
                galleryLauncher.launch("image/*")
            },
                modifier = Modifier.height(56.dp).fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = enabledButton)) {
                Text(text="Change your image",
                    fontSize = 20.sp,
                    fontFamily = fredokaFonts,
                    fontWeight = FontWeight.Medium)
            }
            Spacer(Modifier.height(10.dp))
            Button(
                onClick = {
                    SessionManager.clearSession(context)
                    navController.navigate(Routes.Login.route) {
                        popUpTo(Routes.Main.route) { inclusive = true }
                    }
                },
                modifier = Modifier.height(56.dp).fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE5E5E5))) {
                Text(text="Logout",
                    fontSize = 20.sp,
                    fontFamily = fredokaFonts,
                    fontWeight = FontWeight.Medium)
            }
        }
    }
}