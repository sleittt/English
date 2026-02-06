package com.example.bebeka.firebase

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import java.util.UUID
import kotlin.getValue

class FirestoreService {
    private val db: FirebaseFirestore by lazy {
        Firebase.firestore.apply {
            firestoreSettings = com.google.firebase.firestore.FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build()
        }
    }

    suspend fun getUserProfileImage(firebaseId: String): Bitmap? {
        return try {
            val document = db.collection(USERS_COLLECTION)
                .document(firebaseId)
                .get()
                .await()

            if (document.exists()) {
                val base64Image = document.getString("profileImage")
                base64ToBitmap(base64Image)
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Get user profile image failed: ${e.message}")
            null
        }
    }
    suspend fun isAvailable(): Boolean {
        return try {
            db.collection("test")
                .document("test")
                .get()
                .await()
            true
        } catch (e: Exception) {
            Log.w(TAG, "Firestore not available: ${e.message}")
            false
        }
    }

    companion object {
        private const val USERS_COLLECTION = "users"
        private const val TAG = "FirestoreService"
    }

    // Метод для String? (что тебе и нужно)
    suspend fun updateUserProfileImage(firebaseId: String, base64Image: String?): Boolean {
        return try {
            val updates = hashMapOf<String, Any?>(
                "profileImage" to base64Image,
                "updatedAt" to System.currentTimeMillis()
            )

            db.collection(USERS_COLLECTION)
                .document(firebaseId)
                .update(updates)
                .await()

            Log.d(TAG, "✅ Profile image updated in Firebase for user: $firebaseId")
            true
        } catch (e: Exception) {
            Log.e(TAG, "❌ Update profile image in Firebase failed: ${e.message}")
            false
        }
    }

    // Метод для Bitmap (если вдруг нужен)
    suspend fun updateUserProfileImageBitmap(firebaseId: String, bitmap: Bitmap?): Boolean {
        return try {
            val base64Image = if (bitmap != null) {
                val byteArrayOutputStream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream)
                val byteArray = byteArrayOutputStream.toByteArray()
                Base64.encodeToString(byteArray, Base64.DEFAULT)
            } else {
                null
            }

            updateUserProfileImage(firebaseId, base64Image)
        } catch (e: Exception) {
            Log.e(TAG, "❌ Update profile image (bitmap) failed: ${e.message}")
            false
        }
    }

    // Конвертация Base64 строки в Bitmap
    private fun base64ToBitmap(base64String: String?): Bitmap? {
        return if (!base64String.isNullOrEmpty()) {
            try {
                val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
                BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
            } catch (e: Exception) {
                Log.e(TAG, "Error converting Base64 to bitmap: ${e.message}")
                null
            }
        } else {
            null
        }
    }

    // Получение пользователя с аватаркой
    suspend fun getUserById(firebaseId: String): FirestoreUser? {
        return try {
            val document = db.collection(USERS_COLLECTION)
                .document(firebaseId)
                .get()
                .await()

            if (document.exists()) {
                val user = document.toObject<FirestoreUser>()?.copy(id = document.id)
                // Преобразуем base64 в bitmap если нужно
                user?.profileImage?.let { base64 ->
                    user.profileImageBitmap = base64ToBitmap(base64)
                }
                user
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Get user failed: ${e.message}")
            null
        }
    }

    // Обновление всего профиля
    suspend fun updateUserProfile(firebaseId: String, username: String, profileImage: String?): Boolean {
        return try {
            val updates = hashMapOf<String, Any?>(
                "username" to username,
                "profileImage" to profileImage,
                "updatedAt" to System.currentTimeMillis()
            )

            db.collection(USERS_COLLECTION)
                .document(firebaseId)
                .update(updates)
                .await()

            Log.d(TAG, "✅ Profile updated for user: $firebaseId")
            true
        } catch (e: Exception) {
            Log.e(TAG, "❌ Update profile failed: ${e.message}")
            false
        }
    }

    // Регистрация пользователя
    suspend fun registerUser(
        email: String,
        password: String,
        username: String
    ): FirestoreUser {
        try {
            val firebaseId = UUID.randomUUID().toString()

            val firestoreUser = FirestoreUser(
                id = firebaseId,
                email = email,
                password = password,
                username = username,
                points = 0,
                createdAt = System.currentTimeMillis()
            )

            db.collection(USERS_COLLECTION)
                .document(firebaseId)
                .set(firestoreUser)
                .await()

            Log.d(TAG, "✅ User registered in Firestore: $firebaseId")
            return firestoreUser
        } catch (e: Exception) {
            Log.e(TAG, "❌ Registration failed: ${e.message}", e)
            throw Exception("Failed to register in Firestore: ${e.message}")
        }
    }

    // Вход пользователя
    suspend fun loginUser(email: String, password: String): FirestoreUser? {
        return try {
            val query = db.collection(USERS_COLLECTION)
                .whereEqualTo("email", email)
                .limit(1)
                .get()
                .await()

            if (!query.isEmpty) {
                val document = query.documents[0]
                val firestoreUser = document.toObject<FirestoreUser>()?.copy(id = document.id)

                if (firestoreUser?.password == password) {
                    firestoreUser
                } else {
                    null
                }
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Login failed: ${e.message}")
            null
        } as FirestoreUser?
    }

    // Обновление локального ID
    suspend fun updateLocalId(firebaseId: String, localId: Long): Boolean {
        return try {
            db.collection(USERS_COLLECTION)
                .document(firebaseId)
                .update("localId", localId, "updatedAt", System.currentTimeMillis())
                .await()
            true
        } catch (e: Exception) {
            Log.e(TAG, "❌ Update localId failed: ${e.message}")
            false
        }
    }

    // Обновление очков
    suspend fun updateUserPoints(firebaseId: String, points: Int): Boolean {
        return try {
            db.collection(USERS_COLLECTION)
                .document(firebaseId)
                .update("points", points, "updatedAt", System.currentTimeMillis())
                .await()
            true
        } catch (e: Exception) {
            Log.e(TAG, "❌ Update points failed: ${e.message}")
            false
        }
    }

    // Проверка email
    suspend fun checkEmailExists(email: String): Boolean {
        return try {
            val query = db.collection(USERS_COLLECTION)
                .whereEqualTo("email", email)
                .limit(1)
                .get()
                .await()

            !query.isEmpty
        } catch (e: Exception) {
            Log.e(TAG, "❌ Check email failed: ${e.message}")
            false
        }
    }
}

data class FirestoreUser(
    val id: String = "",
    val email: String = "",
    val password: String = "", // Храни пароль, как ты хочешь
    val username: String = "",
    val points: Int = 0,
    val profileImage: String? = null, // Base64 строка
    val createdAt: Long = System.currentTimeMillis(),
    val localId: Long = 0L,
    var profileImageBitmap: Bitmap? = null // Для кэширования
)