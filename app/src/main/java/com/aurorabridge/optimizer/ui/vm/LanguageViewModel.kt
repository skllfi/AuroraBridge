/**
 * Manages the language settings of the application, allowing the user to select and persist their preferred language.
 */
package com.aurorabridge.optimizer.ui.vm

import android.content.Context
import androidx.lifecycle.ViewModel
import com.aurorabridge.optimizer.utils.LocaleManager

class LanguageViewModel : ViewModel() {

    fun setLanguage(context: Context, language: String) {
        LocaleManager.setLocale(context, language)
        LocaleManager.saveLanguage(context, language)
    }

    fun getLanguage(context: Context): String {
        return LocaleManager.getLanguage(context)
    }
}
