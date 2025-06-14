package com.myfinances.data

import com.myfinances.domain.entity.Account
import com.myfinances.domain.entity.Category
import com.myfinances.domain.entity.Transaction

object MockData {

    val categories = listOf(
        Category(id = 1, name = "Аренда квартиры", emoji = "🏠", isIncome = false),
        Category(id = 2, name = "Одежда", emoji = "👗", isIncome = false),
        Category(id = 3, name = "На собачку", emoji = "🐶", isIncome = false),
        Category(id = 4, name = "Ремонт квартиры", emoji = "🛠️", isIncome = false),
        Category(id = 5, name = "Продукты", emoji = "🛒", isIncome = false),
        Category(id = 6, name = "Спортзал", emoji = "🏋️", isIncome = false),
        Category(id = 7, name = "Медицина", emoji = "💊", isIncome = false),

        Category(id = 10, name = "Зарплата", emoji = "💰", isIncome = true),
        Category(id = 11, name = "Подработка", emoji = "🎁", isIncome = true)
    )

    val transactions = listOf(
        Transaction(id = 1, categoryId = 1, amount = "100000"),
        Transaction(id = 2, categoryId = 2, amount = "100000"),
        Transaction(id = 3, categoryId = 3, amount = "100000", comment = "Джек"),
        Transaction(id = 4, categoryId = 3, amount = "100000", comment = "Энни"),
        Transaction(id = 5, categoryId = 4, amount = "100000"),
        Transaction(id = 6, categoryId = 5, amount = "100000"),
        Transaction(id = 7, categoryId = 6, amount = "100000"),
        Transaction(id = 8, categoryId = 7, amount = "100000"),

        Transaction(id = 10, categoryId = 10, amount = "500000"),
        Transaction(id = 11, categoryId = 11, amount = "100000"),
    )

    fun findCategoryById(id: Int): Category? {
        return categories.find { it.id == id }
    }

    val account = Account(
        id = 1,
        name = "Мой счет",
        balance = -670000.0,
        currency = "₽",
        emoji = "💰"
    )
}