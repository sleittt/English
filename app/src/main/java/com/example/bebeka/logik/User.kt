package com.example.bebeka.logik

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Update
import androidx.core.content.edit
import androidx.room.ColumnInfo
import com.example.bebeka.firebase.FirestoreService

// User.kt
@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val email: String,
    val password: String,
    val username: String = "",
    val points: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    val profileImage: ByteArray? = null,
    // Добавляем поле для хранения Firebase ID
    @ColumnInfo(name = "firebase_id")
    val firebaseId: String? = null
) {
    // Для сравнения ByteArray
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as User

        if (id != other.id) return false
        if (email != other.email) return false
        if (password != other.password) return false
        if (username != other.username) return false
        if (points != other.points) return false
        if (createdAt != other.createdAt) return false
        if (profileImage != null) {
            if (other.profileImage == null) return false
            if (!profileImage.contentEquals(other.profileImage)) return false
        } else if (other.profileImage != null) return false
        if (firebaseId != other.firebaseId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + email.hashCode()
        result = 31 * result + password.hashCode()
        result = 31 * result + username.hashCode()
        result = 31 * result + points
        result = 31 * result + createdAt.hashCode()
        result = 31 * result + (profileImage?.contentHashCode() ?: 0)
        result = 31 * result + (firebaseId?.hashCode() ?: 0)
        return result
    }
}

// UserDao.kt
@Dao
interface UserDao {
    @Insert
    suspend fun insertUser(user: User): Long

    @Query("SELECT * FROM users WHERE email = :email AND password = :password")
    suspend fun getUser(email: String, password: String): User?

    @Query("SELECT * FROM users WHERE email = :email")
    suspend fun getUserByEmail(email: String): User?

    @Query("SELECT * FROM users ORDER BY points DESC LIMIT 3")
    suspend fun getTopUsers(): List<User>

    @Update
    suspend fun updateUser(user: User)

    @Query("SELECT * FROM users WHERE id = :id")
    suspend fun getUserById(id: Long): User?

    // Добавлен метод для обновления только изображения
    @Query("UPDATE users SET profileImage = :imageData WHERE id = :userId")
    suspend fun updateUserImage(userId: Long, imageData: ByteArray?)
}

// AppDatabase.kt
@Database(entities = [User::class], version = 3, exportSchema = false) // Версия увеличена до 2
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                ).fallbackToDestructiveMigration() // Добавлено для упрощения миграции
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

class UserRepository(
    private val userDao: UserDao,
    private val firestoreService: FirestoreService
) {
    private val TAG = "UserRepository"


    // Регистрация через Firestore
    suspend fun registerUserWithFirestore(
        email: String,
        password: String,
        username: String
    ): User? {
        return try {
            // Сначала проверяем локально
            val existingLocalUser = userDao.getUserByEmail(email)
            if (existingLocalUser != null) {
                throw Exception("User already exists locally")
            }

            // Проверяем доступность Firestore
            val isFirestoreAvailable = firestoreService.isAvailable()

            var firebaseId: String? = null

            if (isFirestoreAvailable) {
                try {
                    // Проверяем email в Firestore
                    val emailExists = firestoreService.checkEmailExists(email)
                    if (emailExists) {
                        throw Exception("User with this email already exists in Firestore")
                    }

                    // Создаем в Firestore
                    val firestoreUser = firestoreService.registerUser(
                        email = email,
                        password = password,
                        username = username
                    )
                    firebaseId = firestoreUser.id
                    Log.d(TAG, "✅ User created in Firestore: $firebaseId")
                } catch (e: Exception) {
                    Log.w(TAG, "⚠️ Firestore registration failed, continuing locally: ${e.message}")
                }
            }

            // Создаем локально
            val localUser = User(
                email = email,
                password = password,
                username = username,
                firebaseId = firebaseId
            )

            val localId = userDao.insertUser(localUser)
            Log.d(TAG, "✅ User created locally: $localId")

            // Если был создан в Firestore, обновляем localId
            if (firebaseId != null) {
                try {
                    firestoreService.updateLocalId(firebaseId, localId)
                    Log.d(TAG, "✅ LocalId updated in Firestore")
                } catch (e: Exception) {
                    Log.w(TAG, "⚠️ Failed to update localId in Firestore: ${e.message}")
                }
            }

            // Возвращаем пользователя с обновленным ID
            val resultUser = localUser.copy(id = localId)

            // Обновляем запись
            userDao.updateUser(resultUser)

            resultUser
        } catch (e: Exception) {
            Log.e(TAG, "❌ Registration failed: ${e.message}")
            throw e
        }
    }
    suspend fun updateUserProfileImage(userId: Long, imageBytes: ByteArray?) {
        try {
            // Сохраняем в локальную базу
            userDao.updateUserImage(userId, imageBytes)

            // Получаем пользователя
            val user = userDao.getUserById(userId)

            // Если у пользователя есть firebaseId, синхронизируем с Firebase
            user?.firebaseId?.let { firebaseId ->
                // Кодируем в Base64 строку для Firebase
                val base64Image = imageBytes?.let {
                    android.util.Base64.encodeToString(it, android.util.Base64.DEFAULT)
                }
                // Вызываем правильный метод
                firestoreService.updateUserProfileImage(firebaseId, base64Image)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error updating profile image: ${e.message}")
        }
    }

    // Вход
    suspend fun loginWithFirestore(email: String, password: String): User? {
        return try {
            var firestoreUser: com.example.bebeka.firebase.FirestoreUser? = null

            // Сначала пробуем Firestore
            try {
                firestoreUser = firestoreService.loginUser(email, password)
            } catch (e: Exception) {
                Log.w(TAG, "Firestore login failed, trying local: ${e.message}")
            }

            // Ищем локально
            val localUser = userDao.getUser(email, password)

            when {
                firestoreUser != null && localUser != null -> {
                    // Обновляем firebaseId если нужно
                    if (localUser.firebaseId == null) {
                        val updatedUser = localUser.copy(firebaseId = firestoreUser.id)
                        userDao.updateUser(updatedUser)
                        updatedUser
                    } else {
                        localUser
                    }
                }
                firestoreUser != null -> {
                    // Создаем локально из Firestore
                    val newLocalUser = User(
                        email = firestoreUser.email,
                        password = password,
                        username = firestoreUser.username,
                        points = firestoreUser.points,
                        firebaseId = firestoreUser.id
                    )
                    val localId = userDao.insertUser(newLocalUser)
                    newLocalUser.copy(id = localId)
                }
                localUser != null -> {
                    // Только локальный
                    localUser
                }
                else -> null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Login error: ${e.message}")
            null
        }
    }

    // Остальные методы остаются
    suspend fun getUserById(userId: Long): User? = userDao.getUserById(userId)
    suspend fun getUserByEmail(email: String): User? = userDao.getUserByEmail(email)
    suspend fun getUser(email: String, password: String): User? = userDao.getUser(email, password)

    suspend fun updateUserPoints(userId: Long, additionalPoints: Double) {
        val user = userDao.getUserById(userId)
        user?.let {
            val totalPoints = it.points + additionalPoints
            val updatedUser = it.copy(points = totalPoints.toInt())

            // Обновляем локально
            userDao.updateUser(updatedUser)

            // Пробуем обновить в Firestore если есть ID
            it.firebaseId?.let { firebaseId ->
                try {
                    firestoreService.updateUserPoints(firebaseId, totalPoints.toInt())
                } catch (e: Exception) {
                    Log.w(TAG, "Failed to sync points to Firestore: ${e.message}")
                }
            }
        }
    }


    suspend fun syncProfileImageFromFirebase(userId: Long) {
        try {
            val user = userDao.getUserById(userId)
            user?.firebaseId?.let { firebaseId ->
                // Получаем аватарку из Firebase
                val bitmap = firestoreService.getUserProfileImage(firebaseId)

                // Сохраняем в локальную базу
                if (bitmap != null) {
                    val stream = java.io.ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream)
                    val imageBytes = stream.toByteArray()
                    userDao.updateUserImage(userId, imageBytes)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error syncing profile image from Firebase: ${e.message}")
        }
    }

    suspend fun getTopUsers(): List<User> = userDao.getTopUsers()
    suspend fun updateUserImage(userId: Long, imageData: ByteArray?) =
        userDao.updateUserImage(userId, imageData)
}

// SessionManager.kt
object SessionManager {
    private const val PREFS_NAME = "session_prefs"
    private const val KEY_USER_ID = "current_user_id"
    private const val KEY_FIREBASE_ID = "current_firebase_id"
    private const val KEY_EMAIL = "current_email"

    fun saveUserSession(
        context: Context,
        userId: Long,
        firebaseId: String? = null,
        email: String? = null
    ) = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        .edit {
            putLong(KEY_USER_ID, userId)
            firebaseId?.let { putString(KEY_FIREBASE_ID, it) }
            email?.let { putString(KEY_EMAIL, it) }
        }

    fun getCurrentUserId(context: Context): Long {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getLong(KEY_USER_ID, -1L)
    }

    fun getCurrentFirebaseId(context: Context): String? {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_FIREBASE_ID, null)
    }

    fun getCurrentEmail(context: Context): String? {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_EMAIL, null)
    }

    fun clearSession(context: Context) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit {
                clear()
            }
    }
}