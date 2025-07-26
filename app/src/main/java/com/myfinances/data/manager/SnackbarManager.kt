package com.myfinances.data.manager

import com.myfinances.ui.model.SnackbarMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SnackbarManager @Inject constructor() {
    private val _messages = MutableStateFlow<List<SnackbarMessage>>(emptyList())
    val messages: StateFlow<List<SnackbarMessage>> = _messages.asStateFlow()

    fun showMessage(message: String) {
        _messages.update { currentMessages ->
            currentMessages + SnackbarMessage(message = message)
        }
    }

    fun setMessageShown(messageId: Long) {
        _messages.update { currentMessages ->
            currentMessages.filterNot { it.id == messageId }
        }
    }
}