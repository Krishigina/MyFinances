package com.myfinances.ui.util

import android.content.Context
import androidx.annotation.StringRes
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ResourceProvider @Inject constructor(private val context: Context) {
    fun getString(@StringRes resId: Int): String {
        return context.getString(resId)
    }
}