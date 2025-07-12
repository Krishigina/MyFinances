package com.myfinances.ui.mappers

import com.myfinances.domain.entity.Account
import com.myfinances.ui.model.AccountUiModel
import com.myfinances.ui.util.formatCurrency
import com.myfinances.ui.util.getCurrencySymbol

class AccountDomainToUiMapper {
    fun map(account: Account): AccountUiModel {
        return AccountUiModel(
            id = account.id,
            name = account.name,
            balance = account.balance,
            currency = account.currency,
            emoji = account.emoji,
            balanceFormatted = formatCurrency(account.balance, account.currency),
            currencySymbol = getCurrencySymbol(account.currency)
        )
    }
}