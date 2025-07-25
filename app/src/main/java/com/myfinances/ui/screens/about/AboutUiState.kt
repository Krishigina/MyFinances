package com.myfinances.ui.screens.about

import com.myfinances.domain.entity.AppInfo

data class AboutUiState(
    val appInfo: AppInfo = AppInfo("", "")
)