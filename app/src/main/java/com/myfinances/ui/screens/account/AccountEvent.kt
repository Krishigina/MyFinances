package com.myfinances.ui.screens.account

/**
 * Определяет все возможные события (действия пользователя), которые могут произойти
 * на экране "Счет".
 * ViewModel обрабатывает эти события для изменения своего состояния [AccountUiState].
 * Использование sealed-интерфейса для событий - это часть паттерна MVI (Model-View-Intent).
 */
sealed interface AccountEvent {
    data object EditModeToggled : AccountEvent
    data object SaveChanges : AccountEvent
    data class NameChanged(val name: String) : AccountEvent
    data class BalanceChanged(val balance: String) : AccountEvent
    data object CurrencyPickerToggled : AccountEvent
    data class CurrencySelected(val currency: String) : AccountEvent
    data object RetryLoad : AccountEvent
}