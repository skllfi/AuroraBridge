package com.aurorabridge.optimizer.ui.vm

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aurorabridge.optimizer.utils.LocaleManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class Language(val name: String, val code: String)

class LanguageViewModel(private val localeManager: LocaleManager) : ViewModel() {

    private val _availableLanguages = MutableStateFlow<List<Language>>(emptyList())
    val availableLanguages: StateFlow<List<Language>> = _availableLanguages.asStateFlow()

    private val _selectedLanguage = MutableStateFlow(Language("English", "en"))
    val selectedLanguage: StateFlow<Language> = _selectedLanguage.asStateFlow()

    init {
        _availableLanguages.value = listOf(
            Language("English", "en"),
            Language("Русский", "ru")
            // Add other languages here
        )
    }

    fun loadInitialLanguage(context: Context) {
        viewModelScope.launch {
            val languageCode = localeManager.getLanguage(context)
            val language = _availableLanguages.value.find { it.code == languageCode }
            if (language != null) {
                _selectedLanguage.value = language
            }
        }
    }

    fun setLanguage(context: Context, language: Language) {
        localeManager.setLocale(context, language.code)
        _selectedLanguage.value = language
    }
}