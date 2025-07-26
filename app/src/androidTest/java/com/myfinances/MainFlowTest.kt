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
 * Проверяют полный сценарий: настройку ПИН-кода и смену темы.
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

        // Очистка состояния перед тестом
        context.filesDir.resolve("datastore").listFiles()?.forEach { it.delete() }
        context.cacheDir.parent?.let { parentDir ->
            File(parentDir, "shared_prefs").listFiles()?.forEach { it.delete() }
        }
    }

    @Test
    fun fullScenario_pinSetupAndThemeToggle() {
        // Даем Compose-окружению "успокоиться" после всех стартовых инициализаций.
        composeTestRule.waitForIdle()

        // ШАГ 0: Дождаться полной загрузки приложения после splash screen.
        // Мы ждем появления текста "Расходы", т.к. это стартовый экран.
        // Таймаут увеличен для медленных эмуляторов.
        composeTestRule.waitUntil(timeoutMillis = 15_000) {
            composeTestRule.onAllNodesWithText(expensesText).fetchSemanticsNodes().isNotEmpty()
        }

        // --- ЧАСТЬ 1: НАСТРОЙКА PIN-КОДА ---

        // 1. Переходим на экран Настроек, кликая по текстовой метке
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

        // 7. Убеждаемся, что мы вернулись на экран Настроек и статус пин-кода "Включен"
        composeTestRule.onNodeWithText(passcodeText).assertExists()
        composeTestRule.onNodeWithText(pinStatusOnText).assertExists()

        // --- ЧАСТЬ 2: ПЕРЕКЛЮЧЕНИЕ ТЕМЫ ---

        // 8. Находим переключатель темы и проверяем, что он выключен
        val themeSwitch = composeTestRule.onNodeWithTag(themeSwitchTag)
        themeSwitch.assertIsOff()

        // 9. Включаем темную тему
        themeSwitch.performClick()
        themeSwitch.assertIsOn()

        // 10. Выключаем темную тему
        themeSwitch.performClick()
        themeSwitch.assertIsOff()
    }

    private fun enterPin(pin: String) {
        pin.forEach { digit ->
            composeTestRule.onNodeWithText(digit.toString()).performClick()
        }
    }
}