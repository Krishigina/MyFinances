package com.myfinances.data.manager

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.myfinances.domain.entity.Language
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocaleManager @Inject constructor() {
    fun updateAppLocale(languageCode: String) {
        val localeList = LocaleListCompat.forLanguageTags(languageCode)
        AppCompatDelegate.setApplicationLocales(localeList)
    }
}