package com.myfinances.data

import com.myfinances.domain.entity.Account
import com.myfinances.domain.entity.Category

object MockData {

    val categories = listOf(
        Category(id = 1, name = "Аренда квартиры", emoji = "🏠", isIncome = false),
        Category(id = 2, name = "Одежда", emoji = "👗", isIncome = false),
        Category(id = 3, name = "На собачку", emoji = "🐶", isIncome = false),
        Category(id = 4, name = "Ремонт квартиры", emoji = "🛠️", isIncome = false),
        Category(id = 5, name = "Продукты", emoji = "🛒", isIncome = false),
        Category(id = 6, name = "Спортзал", emoji = "🏋️", isIncome = false),
        Category(id = 7, name = "Медицина", emoji = "💊", isIncome = false),

        Category(id = 10, name = "Зарплата", isIncome = true),
        Category(id = 11, name = "Подработка", isIncome = true)
    )

    val account = Account(
        id = 1,
        name = "Мой счет",
        balance = -670000.0,
        currency = "₽",
        emoji = "💰"
    )
}