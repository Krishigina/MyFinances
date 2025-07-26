package com.myfinances.domain.entity

enum class Language(val code: String) {
    RUSSIAN("ru"),
    ENGLISH("en");

    companion object {
        val default = RUSSIAN

        fun fromCode(code: String?): Language {
            if (code.isNullOrBlank()) {
                return default
            }
            val primaryLanguage = code.split("-").first()
            return entries.find { it.code == primaryLanguage } ?: default
        }
    }
}