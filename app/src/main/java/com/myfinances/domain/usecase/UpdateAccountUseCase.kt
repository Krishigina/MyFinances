package com.myfinances.domain.usecase

import com.myfinances.domain.entity.Account
import com.myfinances.domain.repository.AccountsRepository
import com.myfinances.domain.util.Result
import javax.inject.Inject

/**
 * Use-case для обновления данных счета.
 *
 * Инкапсулирует бизнес-логику, связанную с обновлением счета пользователя.
 * Перед отправкой данных в репозиторий, выполняет базовую валидацию:
 * - Проверяет, что название счета не пустое.
 * - Проверяет, что строка баланса может быть корректно преобразована в число.
 * - Проверяет, что баланс не является отрицательным.
 *
 * Это позволяет отделить правила валидации от ViewModel и слоя данных.
 */
class UpdateAccountUseCase @Inject constructor(
    private val repository: AccountsRepository
) {
    /**
     * Выполняет операцию обновления счета.
     * @param accountId ID счета для обновления.
     * @param name Новое название счета.
     * @param balance Новый баланс в виде строки (для удобства ввода в UI).
     * @param currency Новая валюта счета.
     * @return [Result] с обновленной сущностью [Account] в случае успеха,
     * или [Result.Error] с [IllegalArgumentException] в случае ошибки валидации.
     */
    suspend operator fun invoke(
        accountId: Int,
        name: String,
        balance: String,
        currency: String
    ): Result<Account> {
        if (name.isBlank()) {
            return Result.Error(IllegalArgumentException("Название счета не может быть пустым"))
        }
        // Заменяем запятую на точку для поддержки разных локалей ввода
        val balanceAsDouble = balance.replace(',', '.').toDoubleOrNull()
            ?: return Result.Error(IllegalArgumentException("Некорректный формат баланса"))

        if (balanceAsDouble < 0) {
            return Result.Error(IllegalArgumentException("Баланс не может быть отрицательным"))
        }

        return repository.updateAccount(accountId, name, balanceAsDouble, currency)
    }
}