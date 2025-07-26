// app/src/androidTest/java/com/myfinances/MainFlowTest.kt
package com.myfinances

import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.myfinances.ui.components.PIN_DOT_FILLED_TAG
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

/**
 * UI-тесты для основных пользовательских сценариев в приложении.
 * Каждый тест проверяет один изолированный сценарий.
 */
@RunWith(AndroidJUnit4::class)
class MainFlowTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private lateinit var settingsText: String
    private lateinit var expensesText: String
    private lateinit var passcodeText: String
    private lateinit var pinStatusOffText: String
    private lateinit var pinStatusOnText: String
    private lateinit var createPinTitle: String
    private lateinit var confirmPinTitle: String
    private lateinit var themeSwitchTag: String

    @Before
    fun setup() {
        val context = composeTestRule.activity
        settingsText = context.getString(R.string.botton_nav_label_settings)
        expensesText = context.getString(R.string.botton_nav_label_expenses)
        passcodeText = context.getString(R.string.passcode)
        pinStatusOffText = context.getString(R.string.pin_status_off)
        pinStatusOnText = context.getString(R.string.pin_status_on)
        createPinTitle = context.getString(R.string.pin_enter_title)
        confirmPinTitle = context.getString(R.string.pin_confirm_title)
        themeSwitchTag = "theme_switch"

        // Очистка состояния перед тестом для обеспечения изоляции
        context.filesDir.resolve("datastore").listFiles()?.forEach { it.delete() }
        context.cacheDir.parent?.let { parentDir ->
            File(parentDir, "shared_prefs").listFiles()?.forEach { it.delete() }
        }
    }

    /**
     * Проверяет полный сценарий установки PIN-кода.
     * Сценарий:
     * 1. Переход на экран настроек.
     * 2. Начало установки PIN-кода.
     * 3. Ввод и подтверждение PIN-кода.
     * 4. Проверка, что статус PIN-кода на экране настроек изменился на "Включен".
     */
    @Test
    fun pinSetup_succeedsAndUpdatesStatus() {
        // Дожидаемся загрузки главного экрана
        waitForMainScreen()

        // 1. Переходим на экран Настроек
        composeTestRule.onNodeWithText(settingsText).performClick()

        // 2. Нажимаем на пункт "Код-пароль"
        composeTestRule.onNodeWithText(passcodeText).performClick()

        // 3. Проверяем, что открылся экран установки пин-кода
        composeTestRule.onNodeWithText(createPinTitle).assertExists()

        // 4. Вводим пин-код "1234"
        enterPin("1234")

        // 5. Проверяем, что перешли на экран подтверждения
        composeTestRule.onNodeWithText(confirmPinTitle).assertExists()
        composeTestRule.onNodeWithTag(PIN_DOT_FILLED_TAG).assertDoesNotExist()

        // 6. Повторно вводим пин-код "1234" для подтверждения
        enterPin("1234")

        // Явно ждем завершения всех асинхронных операций (навигация, рекомпозиция)
        composeTestRule.waitForIdle()

        // 7. Убеждаемся, что мы вернулись на экран Настроек и статус пин-кода "Включен"
        composeTestRule.onNodeWithText(passcodeText).assertExists()
        composeTestRule.onNodeWithText(pinStatusOnText).assertExists()
    }

    /**
     * Проверяет, что переключатель темы корректно изменяет свое состояние.
     * Сценарий:
     * 1. Переход на экран настроек.
     * 2. Проверка начального состояния переключателя (выключен).
     * 3. Клик по переключателю и проверка, что он включился.
     * 4. Повторный клик и проверка, что он снова выключился.
     */
    @Test
    fun themeToggle_changesStateCorrectly() {
        // Дожидаемся загрузки главного экрана
        waitForMainScreen()

        // 1. Переходим на экран Настроек
        composeTestRule.onNodeWithText(settingsText).performClick()

        // 2. Находим переключатель темы и проверяем, что он выключен
        val themeSwitch = composeTestRule.onNodeWithTag(themeSwitchTag)
        themeSwitch.assertExists()
        themeSwitch.assertIsOff()

        // 3. Включаем темную тему
        themeSwitch.performClick()
        themeSwitch.assertIsOn()

        // 4. Выключаем темную тему
        themeSwitch.performClick()
        themeSwitch.assertIsOff()
    }

    private fun waitForMainScreen() {
        composeTestRule.waitForIdle()
        composeTestRule.waitUntil(timeoutMillis = 15_000) {
            composeTestRule.onAllNodesWithText(expensesText).fetchSemanticsNodes().isNotEmpty()
        }
    }

    private fun enterPin(pin: String) {
        pin.forEach { digit ->
            composeTestRule.onNodeWithText(digit.toString()).performClick()
        }
    }
}