package com.example.bebeka.utils

import android.util.Log

object DebugLogger {
    // Set to false to disable all debug logs in production
    var isEnabled = true

    private const val DEFAULT_TAG = "BebekaDebug"

    fun d(tag: String = DEFAULT_TAG, message: String) {
        if (isEnabled) Log.d(tag, message)
    }

    fun e(tag: String = DEFAULT_TAG, message: String, throwable: Throwable? = null) {
        if (isEnabled) {
            if (throwable != null) Log.e(tag, message, throwable)
            else Log.e(tag, message)
        }
    }

    fun i(tag: String = DEFAULT_TAG, message: String) {
        if (isEnabled) Log.i(tag, message)
    }

    fun w(tag: String = DEFAULT_TAG, message: String, throwable: Throwable? = null) {
        if (isEnabled) {
            if (throwable != null) Log.w(tag, message, throwable)
            else Log.w(tag, message)
        }
    }
}