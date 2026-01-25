package com.example.bebeka.firebase

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject

import kotlinx.coroutines.tasks.await
import java.util.UUID

class FirestoreService {
    // Обновление аватарки пользователя
    suspend fun updateUserProfileImage(firebaseId: String, profileImage: String?): Boolean {
        return try {
            val updates = hashMapOf<String, Any?>(
                "profileImage" to profileImage
            )

            db.collection(USERS_COLLECTION)
                .document(firebaseId)
                .update(updates)
                .await()

            Log.d(TAG, "✅ Profile image updated for user: $firebaseId")
            true
        } catch (e: Exception) {
            Log.e(TAG, "❌ Update profile image failed: ${e.message}")
            false
        }
    }

    // Обновление профиля (имя и аватарка)
    suspend fun updateUserProfile(firebaseId: String, username: String, profileImage: String?): Boolean {
        return try {
            val updates = hashMapOf<String, Any?>(
                "username" to username,
                "profileImage" to profileImage
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

    // Получение пользователя с аватаркой
    suspend fun getUserWithImage(firebaseId: String): FirestoreUser? {
        return try {
            val document = db.collection(USERS_COLLECTION)
                .document(firebaseId)
                .get()
                .await()

            if (document.exists()) {
                document.toObject<FirestoreUser>()?.copy(id = document.id)
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Get user with image failed: ${e.message}")
            null
        }
    }
    // Используем lazy для отложенной инициализации
    private val db: FirebaseFirestore by lazy {
        Firebase.firestore.apply {
            // Настраиваем только один раз при первом использовании
            firestoreSettings = com.google.firebase.firestore.FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build()
            Log.d("FirestoreService", "Firestore initialized")
        }
    }

    companion object {
        private const val USERS_COLLECTION = "users"
        private const val TAG = "FirestoreService"
    }

    // Проверка доступности Firestore
    suspend fun isAvailable(): Boolean {
        return try {
            // Простая проверка доступности
            db.collection(USERS_COLLECTION)
                .limit(1)
                .get()
                .await()
            true
        } catch (e: Exception) {
            Log.w(TAG, "Firestore not available: ${e.message}")
            false
        }
    }

    // Регистрация пользователя в Firestore
    suspend fun registerUser(
        email: String,
        password: String,
        username: String
    ): FirestoreUser {
        return try {
            Log.d(TAG, "Starting registration for: $email")

            // Генерируем уникальный ID
            val firebaseId = UUID.randomUUID().toString()

            // Создаем объект пользователя
            val firestoreUser = FirestoreUser(
                id = firebaseId,
                email = email,
                password = password,
                username = username,
                points = 0,
                createdAt = System.currentTimeMillis(),
                localId = 0L
            )

            // Сохраняем в Firestore
            db.collection(USERS_COLLECTION)
                .document(firebaseId)
                .set(firestoreUser)
                .await()

            Log.d(TAG, "✅ User registered in Firestore: $firebaseId")
            firestoreUser
        } catch (e: Exception) {
            Log.e(TAG, "❌ Registration failed: ${e.message}", e)
            throw Exception("Failed to register in Firestore: ${e.message}")
        }
    }

    // Проверка существования email
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
                .update("localId", localId)
                .await()
            true
        } catch (e: Exception) {
            Log.e(TAG, "❌ Update localId failed: ${e.message}")
            false
        }
    }

    // Получение пользователя по ID
    suspend fun getUserById(firebaseId: String): FirestoreUser? {
        return try {
            val document = db.collection(USERS_COLLECTION)
                .document(firebaseId)
                .get()
                .await()

            if (document.exists()) {
                document.toObject<FirestoreUser>()?.copy(id = document.id)
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Get user failed: ${e.message}")
            null
        }
    }

    // Обновление очков
    suspend fun updateUserPoints(firebaseId: String, points: Int): Boolean {
        return try {
            db.collection(USERS_COLLECTION)
                .document(firebaseId)
                .update("points", points)
                .await()
            true
        } catch (e: Exception) {
            Log.e(TAG, "❌ Update points failed: ${e.message}")
            false
        }
    }
}

data class FirestoreUser(
    val id: String = "",
    val email: String = "",
    val password: String = "",
    val username: String = "",
    val points: Int = 0,
    val createdAt: Long = 0L,
    val localId: Long = 0L,
    val profileImage: String? = null // Base64 строка или null
)