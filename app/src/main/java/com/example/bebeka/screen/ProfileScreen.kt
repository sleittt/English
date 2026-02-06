package com.example.bebeka.screen

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.CircularProgressIndicator
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
import com.example.bebeka.ui.theme.DeepBlue
import com.example.bebeka.ui.theme.enabledButton
import com.example.bebeka.ui.theme.fredokaFonts
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController, firestoreService: FirestoreService) {
    val context = LocalContext.current
    var currentUser by remember { mutableStateOf<User?>(null) }
    var profileImageBitmap by remember { mutableStateOf<android.graphics.Bitmap?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    val db = remember { AppDatabase.getDatabase(context) }

    // Функция для загрузки изображения из URI и преобразования в ByteArray
    fun loadImageBytesFromUri(uri: android.net.Uri): ByteArray? {
        return try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                inputStream.readBytes()
            }
        } catch (e: Exception) {
            Log.e("ProfileScreen", "Error loading image: ${e.message}")
            null
        }
    }
    fun compressImage(context: Context, uri: android.net.Uri, maxSizeKB: Int = 100): ByteArray? {
        return try {
            // Открываем изображение
            val inputStream = context.contentResolver.openInputStream(uri)
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            BitmapFactory.decodeStream(inputStream, null, options)
            inputStream?.close()

            // Рассчитываем коэффициент сжатия
            var scale = 1
            val imageSizeKB = options.outHeight * options.outWidth * 4 / 1024

            while (imageSizeKB / (scale * scale) > maxSizeKB) {
                scale *= 2
            }

            // Загружаем сжатое изображение
            val scaledOptions = BitmapFactory.Options().apply {
                inSampleSize = scale
            }
            val newInputStream = context.contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(newInputStream, null, scaledOptions)
            newInputStream?.close()

            // Дополнительно сжимаем в JPEG
            val outputStream = ByteArrayOutputStream()
            bitmap?.compress(Bitmap.CompressFormat.JPEG, 70, outputStream)

            outputStream.toByteArray()
        } catch (e: Exception) {
            Log.e("ProfileScreen", "Error compressing image: ${e.message}")
            null
        }
    }

    // Функция для обновления аватарки в Firebase
    suspend fun updateProfileImageInFirebase(userId: Long, imageBytes: ByteArray?) {
        try {
            val user = db.userDao().getUserById(userId)
            user?.firebaseId?.let { firebaseId ->
                val base64Image = imageBytes?.let {
                    android.util.Base64.encodeToString(it, android.util.Base64.DEFAULT)
                }

                firestoreService.updateUserProfileImage(firebaseId, base64Image)
                Log.d("ProfileScreen", "Profile image updated in Firebase")
            }
        } catch (e: Exception) {
            Log.e("ProfileScreen", "Error updating profile in Firebase: ${e.message}")
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
                        // СЖИМАЕМ изображение перед сохранением
                        val imageBytes = compressImage(context, imageUri, 100) // 100KB максимум

                        if (imageBytes == null) {
                            Log.e("ProfileScreen", "Failed to compress image")
                            return@launch
                        }

                        Log.d("ProfileScreen", "Compressed image size: ${imageBytes.size / 1024}KB")

                        // Сохраняем в базу данных
                        db.userDao().updateUserImage(currentUserId, imageBytes)

                        // Обновляем в Firebase
                        val user = db.userDao().getUserById(currentUserId)
                        user?.firebaseId?.let { firebaseId ->
                            val base64Image = android.util.Base64.encodeToString(imageBytes, android.util.Base64.DEFAULT)
                            firestoreService.updateUserProfileImage(firebaseId, base64Image)
                            Log.d("ProfileScreen", "✅ Profile image updated in Firebase")
                        }

                        // Обновляем текущего пользователя
                        val updatedUser = db.userDao().getUserById(currentUserId)
                        withContext(Dispatchers.Main) {
                            currentUser = updatedUser
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
                    // 1. Загружаем пользователя из локальной базы
                    val user = db.userDao().getUserById(currentUserId)

                    withContext(Dispatchers.Main) {
                        currentUser = user
                        user?.profileImage?.let { bytes ->
                            profileImageBitmap = android.graphics.BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                        }
                        isLoading = false
                    }

                    // 2. Если есть firebaseId, синхронизируем с Firebase
                    user?.firebaseId?.let { firebaseId ->
                        try {
                            val firestoreUser = firestoreService.getUserById(firebaseId)
                            firestoreUser?.let { fbUser ->
                                // Обновляем локальные данные из Firebase если они новее
                                val updatedUser = user.copy(
                                    username = fbUser.username,
                                    points = fbUser.points
                                )
                                db.userDao().updateUser(updatedUser)

                                // Обновляем аватарку если она есть в Firebase
                                fbUser.profileImage?.let { base64Image ->
                                    val imageBytes = android.util.Base64.decode(base64Image, android.util.Base64.DEFAULT)
                                    db.userDao().updateUserImage(currentUserId, imageBytes)

                                    withContext(Dispatchers.Main) {
                                        currentUser = updatedUser.copy(profileImage = imageBytes)
                                        profileImageBitmap = android.graphics.BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                                    }
                                }

                                Log.d("ProfileScreen", "Data synced from Firebase")
                            }
                        } catch (e: Exception) {
                            Log.e("ProfileScreen", "Error syncing from Firebase: ${e.message}")
                        }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        isLoading = false
                    }
                }
            } catch (e: Exception) {
                Log.e("ProfileScreen", "Error loading data: ${e.message}")
                withContext(Dispatchers.Main) {
                    isLoading = false
                }
            }
        }
    }

    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        Scaffold(topBar = {
            TopAppBar(
                title = {
                    Column(
                        modifier = Modifier.padding(24.dp)
                    ) {
                        if (profileImageBitmap != null) {
                            Image(
                                bitmap = profileImageBitmap!!.asImageBitmap(),
                                contentDescription = "Profile image",
                                modifier = Modifier
                                    .size(134.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        } else {
                            // Показываем placeholder, если нет аватарки
                            Box(
                                modifier = Modifier
                                    .size(134.dp)
                                    .clip(CircleShape)
                                    .background(Color.Gray)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        }

                        Text(
                            text = stringResource(R.string.nickname, currentUser?.username ?: "User"),
                            fontSize = 22.sp,
                            fontFamily = fredokaFonts,
                            fontWeight = FontWeight.Medium
                        )
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
                Spacer(Modifier.height(10.dp))
                Button(
                    onClick = {
                        navController.navigate("${Routes.langSelect.route}?firstChange=false")
                    },
                    modifier = Modifier.height(56.dp).fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = enabledButton
                    )
                ) {
                    Text(
                        text = stringResource(R.string.chnglanguage),
                        fontSize = 20.sp,
                        fontFamily = fredokaFonts,
                        fontWeight = FontWeight.Medium
                    )
                }
                Spacer(Modifier.height(10.dp))
                Button(
                    onClick = {
                        galleryLauncher.launch("image/*")
                    },
                    modifier = Modifier.height(56.dp).fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = enabledButton
                    )
                ) {
                    Text(
                        text = stringResource(R.string.chngimage),
                        fontSize = 20.sp,
                        fontFamily = fredokaFonts,
                        fontWeight = FontWeight.Medium
                    )
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
                        containerColor = Color(0xFFE5E5E5)
                    )
                ) {
                    Text(
                        text = stringResource(R.string.logout),
                        fontSize = 20.sp,
                        fontFamily = fredokaFonts,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}