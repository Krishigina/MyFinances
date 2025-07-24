package com.myfinances.ui.screens.haptics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myfinances.domain.entity.HapticEffect
import com.myfinances.domain.usecase.GetHapticSettingsUseCase
import com.myfinances.domain.usecase.PreviewHapticEffectUseCase
import com.myfinances.domain.usecase.SaveHapticEffectUseCase
import com.myfinances.domain.usecase.SaveHapticsEnabledUseCase
import com.myfinances.ui.mappers.HapticEffectDomainToUiMapper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class HapticsScreenViewModel @Inject constructor(
    getHapticSettingsUseCase: GetHapticSettingsUseCase,
    private val saveHapticsEnabledUseCase: SaveHapticsEnabledUseCase,
    private val saveHapticEffectUseCase: SaveHapticEffectUseCase,
    private val previewHapticEffectUseCase: PreviewHapticEffectUseCase,
    private val mapper: HapticEffectDomainToUiMapper
) : ViewModel() {

    private val _uiState = MutableStateFlow(HapticsScreenUiState())
    val uiState = _uiState.asStateFlow()

    init {
        getHapticSettingsUseCase().onEach { settings ->
            _uiState.update {
                it.copy(
                    isEnabled = settings.isEnabled,
                    effects = HapticEffect.entries.map { effect ->
                        mapper.map(effect, isSelected = effect == settings.effect)
                    }
                )
            }
        }.launchIn(viewModelScope)
    }

    fun onEvent(event: HapticsScreenEvent) {
        when (event) {
            is HapticsScreenEvent.OnHapticsToggled -> {
                viewModelScope.launch {
                    saveHapticsEnabledUseCase(event.enabled)
                }
            }
            is HapticsScreenEvent.OnEffectSelected -> {
                previewHapticEffectUseCase(event.effect)
                viewModelScope.launch {
                    saveHapticEffectUseCase(event.effect)
                }
            }
        }
    }
}