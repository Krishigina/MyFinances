package com.myfinances.ui.screens.about

import androidx.lifecycle.ViewModel
import com.myfinances.domain.usecase.GetAppInfoUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

class AboutViewModel @Inject constructor(
    private val getAppInfoUseCase: GetAppInfoUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AboutUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadAppInfo()
    }

    private fun loadAppInfo() {
        val appInfo = getAppInfoUseCase()
        _uiState.update { it.copy(appInfo = appInfo) }
    }
}