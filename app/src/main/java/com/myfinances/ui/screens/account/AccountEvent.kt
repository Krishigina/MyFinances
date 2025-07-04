package com.myfinances.ui.screens.account

/**
 * Определяет все возможные события (действия пользователя), которые могут произойти
 * на экране "Счет".
 * ViewModel обрабатывает эти события для изменения своего состояния [AccountUiState].
 * Использование sealed-интерфейса для событий - это часть паттерна MVI (Model-View-Intent).
 */
sealed interface AccountEvent {
    /**
     * Событие переключения между режимом просмотра и режимом редактирования.
     */
    data object EditModeToggled : AccountEvent

    /**
     * Событие для сохранения внесенных изменений.
     */
    data object SaveChanges : AccountEvent

    /**
     * Событие изменения названия счета в поле ввода.
     * @param name Новое значение названия.
     */
    data class NameChanged(val name: String) : AccountEvent

    /**
     * Событие изменения баланса в поле ввода.
     * @param balance Новое значение баланса (в виде строки).
     */
    data class BalanceChanged(val balance: String) : AccountEvent

    /**
     * Событие открытия/закрытия модального окна для выбора валюты.
     */
    data object CurrencyPickerToggled : AccountEvent

    /**
     * Событие выбора новой валюты из списка.
     * @param currency Выбранный код валюты.
     */
    data class CurrencySelected(val currency: String) : AccountEvent
}