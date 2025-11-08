package com.aurorabridge.optimizer.utils

import android.content.Context
import android.content.res.Configuration
import java.util.Locale

class LocaleManager {

    fun setLocale(context: Context, languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        context.resources.updateConfiguration(config, context.resources.displayMetrics)
        
        saveLanguage(context, languageCode)
    }

    private fun saveLanguage(context: Context, language: String) {
        val prefs = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
        prefs.edit().putString("language", language).apply()
    }

    fun getLanguage(context: Context): String {
        val prefs = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
        return prefs.getString("language", "en") ?: "en"
    }
}
