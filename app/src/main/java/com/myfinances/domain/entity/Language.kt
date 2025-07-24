package com.myfinances.domain.entity

enum class Language(val code: String) {
    RUSSIAN("ru"),
    ENGLISH("en");

    companion object {
        val default = RUSSIAN

        fun fromCode(code: String?): Language {
            return entries.find { it.code == code } ?: default
        }
    }
}