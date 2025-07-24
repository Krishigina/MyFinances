package com.myfinances.ui.screens.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myfinances.data.manager.SnackbarManager
import com.myfinances.data.manager.SyncUpdateManager
import com.myfinances.domain.usecase.GetAccountUseCase
import com.myfinances.domain.usecase.UpdateAccountUseCase
import com.myfinances.domain.util.Result
import com.myfinances.ui.components.CurrencyModel
import com.myfinances.ui.mappers.AccountDomainToUiMapper
import com.myfinances.ui.util.formatSyncTime
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class AccountViewModel @Inject constructor(
    private val getAccountUseCase: GetAccountUseCase,
    private val updateAccountUseCase: UpdateAccountUseCase,
    private val syncUpdateManager: SyncUpdateManager,
    private val snackbarManager: SnackbarManager,
    private val accountMapper: AccountDomainToUiMapper
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
        viewModelScope.launch {
            syncUpdateManager.syncCompletedFlow.collect { syncTime ->
                snackbarManager.showMessage("Синхронизация завершена: ${formatSyncTime(syncTime)}")
            }
        }
    }

    fun onEvent(event: AccountEvent) {
        when (event) {
            is AccountEvent.RetryLoad -> loadAccount(forceReload = true)
            else -> {
                val currentState = _uiState.value
                if (currentState !is AccountUiState.Success) return

                when (event) {
                    is AccountEvent.EditModeToggled -> {
                        _uiState.update {
                            currentState.copy(
                                isEditMode = !currentState.isEditMode,
                                draftName = currentState.account.name,
                                draftBalance = currentState.account.balance.toBigDecimal()
                                    .toPlainString(),
                                draftCurrency = currentState.account.currency,
                            )
                        }
                    }

                    is AccountEvent.NameChanged -> _uiState.update { currentState.copy(draftName = event.name) }
                    is AccountEvent.BalanceChanged -> _uiState.update {
                        currentState.copy(
                            draftBalance = event.balance
                        )
                    }

                    is AccountEvent.CurrencyPickerToggled -> _uiState.update {
                        currentState.copy(showCurrencyPicker = !currentState.showCurrencyPicker)
                    }

                    is AccountEvent.CurrencySelected -> _uiState.update {
                        currentState.copy(
                            draftCurrency = event.currency,
                            showCurrencyPicker = false
                        )
                    }

                    is AccountEvent.SaveChanges -> saveChanges(currentState)
                    AccountEvent.RetryLoad -> { /* Handled above */ }
                }
            }
        }
    }

    private fun loadAccount(forceReload: Boolean = false) {
        activeJob?.cancel()
        if (forceReload) {
            viewModelScope.launch {
                _uiState.value = AccountUiState.Loading
                getAccountUseCase.refresh()
            }
        }

        getAccountUseCase().onEach { result ->
            when (result) {
                is Result.Success -> {
                    val accountUiModel = accountMapper.map(result.data)
                    _uiState.value = AccountUiState.Success(
                        account = accountUiModel,
                        draftName = accountUiModel.name,
                        draftBalance = accountUiModel.balance.toBigDecimal().toPlainString(),
                        draftCurrency = accountUiModel.currency,
                        availableCurrencies = availableCurrencies
                    )
                }

                is Result.Failure -> {
                    when(result) {
                        is Result.Failure.ApiError -> snackbarManager.showMessage("Ошибка API: ${result.code}")
                        is Result.Failure.GenericError -> snackbarManager.showMessage(result.exception.message ?: "Не удалось загрузить счет")
                        is Result.Failure.NetworkError -> snackbarManager.showMessage("Ошибка сети. Проверьте подключение.")
                    }
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun saveChanges(state: AccountUiState.Success) {
        activeJob?.cancel()
        activeJob = viewModelScope.launch {
            _uiState.update { state.copy(isSaving = true) }

            val result = updateAccountUseCase(
                accountId = state.account.id,
                name = state.draftName,
                balance = state.draftBalance,
                currency = state.draftCurrency
            )

            _uiState.update {
                if (it is AccountUiState.Success) {
                    it.copy(isSaving = false)
                } else {
                    it
                }
            }

            when (result) {
                is Result.Success -> {
                    snackbarManager.showMessage("Счет успешно сохранен")
                    _uiState.update {
                        if (it is AccountUiState.Success) {
                            it.copy(isEditMode = false)
                        } else {
                            it
                        }
                    }
                }
                is Result.Failure -> {
                    when(result) {
                        is Result.Failure.ApiError -> snackbarManager.showMessage("Ошибка сохранения: ${result.message}")
                        is Result.Failure.GenericError -> snackbarManager.showMessage(result.exception.message ?: "Ошибка сохранения")
                        is Result.Failure.NetworkError -> snackbarManager.showMessage("Ошибка сети. Проверьте подключение.")
                    }
                }
            }
        }
    }
}