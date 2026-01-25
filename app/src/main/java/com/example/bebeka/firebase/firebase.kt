package com.example.bebeka.firebase

import android.content.Context
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore


object FirebaseManager {
    private var isInitialized = false
    private lateinit var firestore: FirebaseFirestore

    fun initialize(context: Context): Boolean {
        return try {
            if (!isInitialized) {
                // Проверяем, нужно ли инициализировать
                if (FirebaseApp.getApps(context).isEmpty()) {
                    FirebaseApp.initializeApp(context)
                }

                // Настраиваем Firestore
                firestore = Firebase.firestore

                // Настраиваем настройки для оффлайн работы
                val settings = com.google.firebase.firestore.FirebaseFirestoreSettings.Builder()
                    .setPersistenceEnabled(true)
                    .setCacheSizeBytes(com.google.firebase.firestore.FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)
                    .build()
                firestore.firestoreSettings = settings

                isInitialized = true
                Log.d("FirebaseManager", "✅ Firebase initialized successfully")
                true
            } else {
                Log.d("FirebaseManager", "✅ Firebase already initialized")
                true
            }
        } catch (e: Exception) {
            Log.e("FirebaseManager", "❌ Firebase initialization failed: ${e.message}")
            false
        }
    }

    fun getFirestore(): FirebaseFirestore {
        if (!isInitialized) {
            throw IllegalStateException("Firebase not initialized. Call initialize() first.")
        }
        return firestore
    }

    fun checkConnection(onComplete: (Boolean) -> Unit) {
        if (!isInitialized) {
            onComplete(false)
            return
        }

        firestore.collection("connection_test")
            .document("ping")
            .set(hashMapOf("timestamp" to System.currentTimeMillis()))
            .addOnSuccessListener {
                onComplete(true)
            }
            .addOnFailureListener {
                onComplete(false)
            }
    }
}