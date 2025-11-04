package com.example.bebeka.logik

import android.content.Context
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
    val profileImage: ByteArray? = null // Храним изображение как BLOB)
)

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

class UserRepository(private val userDao: UserDao) {

    suspend fun updateUserPoints(userId: Long, additionalPoints: Double) {
        val user = userDao.getUserById(userId)
        user?.let {
            val totalPoints = it.points + additionalPoints
            userDao.updateUser(it.copy(points = totalPoints.toInt()))
        }
    }

    suspend fun getTopUsers(): List<User> {
        return userDao.getTopUsers()
    }

    suspend fun getUserById(userId: Long): User? {
        return userDao.getUserById(userId)
    }

    // Добавлен метод для обновления изображения
    suspend fun updateUserImage(userId: Long, imageData: ByteArray?) {
        userDao.updateUserImage(userId, imageData)
    }
}

// SessionManager.kt
object SessionManager {
    private const val PREFS_NAME = "session_prefs"
    private const val KEY_USER_ID = "current_user_id"

    fun saveCurrentUserId(context: Context, userId: Long) =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit {
                putLong(KEY_USER_ID, userId)
            }

    fun getCurrentUserId(context: Context): Long {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getLong(KEY_USER_ID, -1L)
    }

    fun clearSession(context: Context) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit {
                clear()
            }
    }
}