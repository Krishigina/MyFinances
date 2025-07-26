package com.myfinances

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.work.WorkManager
import com.myfinances.ui.components.PIN_DOT_FILLED_TAG
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class PinFlowTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private lateinit var settingsText: String
    private lateinit var expensesText: String
    private lateinit var passcodeText: String
    private lateinit var pinStatusOnText: String
    private lateinit var createPinTitle: String
    private lateinit var confirmPinTitle: String

    @Before
    fun setup() {
        cleanupState()
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        settingsText = appContext.getString(R.string.botton_nav_label_settings)
        expensesText = appContext.getString(R.string.botton_nav_label_expenses)
        passcodeText = appContext.getString(R.string.passcode)
        pinStatusOnText = appContext.getString(R.string.pin_status_on)
        createPinTitle = appContext.getString(R.string.pin_enter_title)
        confirmPinTitle = appContext.getString(R.string.pin_confirm_title)
    }

    @After
    fun tearDown() {
        cleanupState() // Очистка после теста для надежности
    }

    /**
     * Проверяет полный сценарий установки PIN-кода:
     * 1. Переход на экран настроек.
     * 2. Начало установки PIN-кода.
     * 3. Ввод и подтверждение PIN-кода.
     * 4. Проверка, что произошел возврат на экран настроек.
     * 5. Проверка, что статус PIN-кода изменился на "Включен".
     */
    @Test
    fun pinSetup_succeedsAndUpdatesStatusCorrectly() {
        waitForMainScreen()

        // 1. Навигация в настройки
        composeTestRule.onNodeWithText(settingsText).performClick()
        composeTestRule.onNodeWithText(passcodeText).performClick()

        // 2. Экран создания PIN-кода
        composeTestRule.onNodeWithText(createPinTitle).assertExists()
        enterPin("1234")

        // 3. Экран подтверждения PIN-кода
        composeTestRule.onNodeWithText(confirmPinTitle).assertExists()
        composeTestRule.onNodeWithTag(PIN_DOT_FILLED_TAG).assertDoesNotExist()
        enterPin("1234")

        // 4. Ждем возврата на экран Настроек
        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule.onAllNodesWithText(passcodeText).fetchSemanticsNodes().isNotEmpty()
        }

        // 5. Проверяем результат
        composeTestRule.onNodeWithText(passcodeText).assertExists()
        composeTestRule.onNodeWithText(pinStatusOnText).assertExists()
    }

    private fun cleanupState() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val application = appContext.applicationContext as MyTestApplication

        appContext.filesDir.resolve("datastore").deleteRecursively()
        appContext.getDatabasePath("my_finances.db").delete()
        appContext.getDatabasePath("my_finances.db-shm").delete()
        appContext.getDatabasePath("my_finances.db-wal").delete()
        File(appContext.applicationInfo.dataDir, "shared_prefs").deleteRecursively()
        WorkManager.getInstance(appContext).cancelAllWork().result.get()
        application.resetAppComponent()
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