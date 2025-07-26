package com.myfinances.util

/**
 * Проверяет, запущен ли код в данный момент в рамках инструментального теста.
 * Это делается путем проверки наличия класса из тестовой библиотеки (например, Espresso)
 * в classpath. Это безопасный способ, который не повлияет на релизную сборку.
 */
fun isRunningInTestEnvironment(): Boolean {
    return try {
        Class.forName("androidx.test.espresso.Espresso")
        true
    } catch (e: ClassNotFoundException) {
        false
    }
}