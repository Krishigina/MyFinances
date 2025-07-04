package com.myfinances.ui.screens.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myfinances.data.manager.AccountUpdateManager
import com.myfinances.domain.usecase.GetAccountUseCase
import com.myfinances.domain.usecase.UpdateAccountUseCase
import com.myfinances.domain.util.Result
import com.myfinances.ui.components.CurrencyModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel для экрана "Счет".
 *
 * Отвечает за:
 * - Загрузку данных о счете пользователя с помощью [GetAccountUseCase].
 * - Управление состоянием UI экрана через [AccountUiState].
 * - Обработку пользовательских действий ([AccountEvent]), таких как переключение в режим
 *   редактирования, изменение данных в полях ввода и сохранение.
 * - Взаимодействие с [UpdateAccountUseCase] для отправки обновленных данных на сервер.
 * - Управление жизненным циклом асинхронных операций в [viewModelScope], что гарантирует
 *   их автоматическую отмену при уничтожении ViewModel.
 */
@HiltViewModel
class AccountViewModel @Inject constructor(
    private val getAccountUseCase: GetAccountUseCase,
    private val updateAccountUseCase: UpdateAccountUseCase,
    private val accountUpdateManager: AccountUpdateManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<AccountUiState>(AccountUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private var activeJob: Job? = null

    private val availableCurrencies = listOf(
        CurrencyModel("RUB", "Российский рубль", "₽"),
        CurrencyModel("USD", "Американский доллар", "$"),
        CurrencyModel("EUR", "Евро", "€")
    )

    init {
        loadAccount()
    }

    fun onEvent(event: AccountEvent) {
        val currentState = _uiState.value
        if (currentState !is AccountUiState.Success) return

        when (event) {
            is AccountEvent.EditModeToggled -> {
                _uiState.update {
                    currentState.copy(
                        isEditMode = !currentState.isEditMode,
                        draftName = if (currentState.isEditMode) currentState.account.name else currentState.draftName,
                        draftBalance = if (currentState.isEditMode) currentState.account.balance.toBigDecimal()
                            .toPlainString() else currentState.draftBalance,
                        draftCurrency = if (currentState.isEditMode) currentState.account.currency else currentState.draftCurrency,
                        saveError = null
                    )
                }
            }

            is AccountEvent.NameChanged -> _uiState.update { currentState.copy(draftName = event.name) }
            is AccountEvent.BalanceChanged -> _uiState.update { currentState.copy(draftBalance = event.balance) }
            is AccountEvent.CurrencyPickerToggled -> _uiState.update {
                currentState.copy(
                    showCurrencyPicker = !currentState.showCurrencyPicker
                )
            }

            is AccountEvent.CurrencySelected -> _uiState.update {
                currentState.copy(
                    draftCurrency = event.currency,
                    showCurrencyPicker = false
                )
            }

            is AccountEvent.SaveChanges -> saveChanges(currentState)
        }
    }

    private fun loadAccount(forceReload: Boolean = false) {
        activeJob?.cancel()
        activeJob = viewModelScope.launch {
            if (forceReload || _uiState.value is AccountUiState.Loading) {
                _uiState.value = AccountUiState.Loading
            }
            when (val result = getAccountUseCase()) {
                is Result.Success -> {
                    _uiState.value = AccountUiState.Success(
                        account = result.data,
                        draftName = result.data.name,
                        draftBalance = result.data.balance.toBigDecimal().toPlainString(),
                        draftCurrency = result.data.currency,
                        availableCurrencies = availableCurrencies
                    )
                }

                is Result.Error -> _uiState.value =
                    AccountUiState.Error(result.exception.message ?: "Unknown error")

                is Result.NetworkError -> _uiState.value = AccountUiState.NoInternet
            }
        }
    }

    private fun saveChanges(state: AccountUiState.Success) {
        activeJob?.cancel()
        activeJob = viewModelScope.launch {
            _uiState.update { state.copy(isSaving = true, saveError = null) }

            val result = updateAccountUseCase(
                accountId = state.account.id,
                name = state.draftName,
                balance = state.draftBalance,
                currency = state.draftCurrency
            )

            when (result) {
                is Result.Success -> {
                    accountUpdateManager.notifyAccountUpdated()
                    loadAccount(forceReload = true)
                }
                is Result.Error -> _uiState.update {
                    state.copy(
                        isSaving = false,
                        saveError = result.exception.message
                    )
                }

                is Result.NetworkError -> _uiState.update {
                    state.copy(
                        isSaving = false,
                        saveError = "Ошибка сети. Проверьте подключение."
                    )
                }
            }
        }
    }
}