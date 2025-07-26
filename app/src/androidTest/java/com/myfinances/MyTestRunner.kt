package com.myfinances

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner

/**
 * Кастомный TestRunner, который подменяет стандартный Application класс
 * на наш тестовый [MyTestApplication] во время выполнения инструментальных тестов.
 */
class MyTestRunner : AndroidJUnitRunner() {
    override fun newApplication(
        cl: ClassLoader?,
        className: String?,
        context: Context?
    ): Application {
        return super.newApplication(cl, MyTestApplication::class.java.name, context)
    }
}