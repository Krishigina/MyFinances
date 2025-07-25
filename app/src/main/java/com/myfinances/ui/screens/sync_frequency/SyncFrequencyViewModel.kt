package com.myfinances.ui.screens.sync_frequency

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myfinances.domain.entity.SyncFrequency
import com.myfinances.domain.usecase.GetSyncFrequencyUseCase
import com.myfinances.domain.usecase.SaveSyncFrequencyUseCase
import com.myfinances.ui.mappers.SyncFrequencyDomainToUiMapper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class SyncFrequencyViewModel @Inject constructor(
    private val getSyncFrequencyUseCase: GetSyncFrequencyUseCase,
    private val saveSyncFrequencyUseCase: SaveSyncFrequencyUseCase,
    private val mapper: SyncFrequencyDomainToUiMapper
) : ViewModel() {

    private val _uiState = MutableStateFlow(SyncFrequencyUiState())
    val uiState = _uiState.asStateFlow()

    init {
        getSyncFrequencyUseCase().onEach { frequency ->
            _uiState.update {
                it.copy(
                    currentFrequency = frequency,
                    selectedFrequencyLabel = mapper.map(frequency)
                )
            }
        }.launchIn(viewModelScope)
    }

    fun onEvent(event: SyncFrequencyEvent) {
        when (event) {
            is SyncFrequencyEvent.OnFrequencySelected -> {
                viewModelScope.launch {
                    saveSyncFrequencyUseCase(event.frequency)
                }
            }
        }
    }
}