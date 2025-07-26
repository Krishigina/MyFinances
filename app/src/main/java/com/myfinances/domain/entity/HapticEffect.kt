package com.myfinances.domain.entity

enum class HapticEffect {
    CLICK,
    DOUBLE_CLICK,
    TICK;

    companion object {
        val default = CLICK
    }
}