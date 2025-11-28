package com.example.bebeka.data

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import androidx.core.content.edit
import java.util.Locale

class Prefs(context: Context) {
    private val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    var language: String
        get() = sharedPreferences.getString("language", "en") ?: "en"
        set(value) = sharedPreferences.edit().putString("language", value).apply()

}
fun updateLocale(context: Context, languageCode: String) {
    val locale = Locale(languageCode)
    Locale.setDefault(locale)

    val resources = context.resources
    val configuration = Configuration(resources.configuration)
    configuration.setLocale(locale)

    context.createConfigurationContext(configuration)
    resources.updateConfiguration(configuration, resources.displayMetrics)
}