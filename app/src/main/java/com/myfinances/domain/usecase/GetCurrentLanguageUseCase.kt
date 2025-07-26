package com.myfinances.domain.usecase

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.myfinances.domain.entity.Language
import javax.inject.Inject

class GetCurrentLanguageUseCase @Inject constructor() {
    operator fun invoke(): Language {
        val currentAppLocales = AppCompatDelegate.getApplicationLocales()
        val currentLangCode = if (!currentAppLocales.isEmpty) {
            currentAppLocales.get(0)?.toLanguageTag()
        } else {
            LocaleListCompat.getAdjustedDefault().get(0)?.toLanguageTag()
        }
        return Language.fromCode(currentLangCode)
    }
}