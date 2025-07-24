package com.myfinances.ui.model

import java.util.UUID

data class SnackbarMessage(
    val message: String,
    val id: Long = UUID.randomUUID().mostSignificantBits
)