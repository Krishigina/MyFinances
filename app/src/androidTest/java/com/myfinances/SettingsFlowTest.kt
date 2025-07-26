package com.myfinances

import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.work.WorkManager
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class SettingsFlowTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private lateinit var settingsText: String
    private lateinit var expensesText: String
    private lateinit var themeSwitchTag: String

    @Before
    fun setup() {
        cleanupState()
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        settingsText = appContext.getString(R.string.botton_nav_label_settings)
        expensesText = appContext.getString(R.string.botton_nav_label_expenses)
        themeSwitchTag = "theme_switch"
    }

    @After
    fun tearDown() {
        cleanupState()
    }

    @Test
    fun themeToggle_changesStateCorrectly() {
        waitForMainScreen()

        composeTestRule.onNodeWithText(settingsText).performClick()

        val themeSwitch = composeTestRule.onNodeWithTag(themeSwitchTag)
        themeSwitch.assertExists()
        themeSwitch.assertIsOff()

        themeSwitch.performClick()
        themeSwitch.assertIsOn()

        themeSwitch.performClick()
        themeSwitch.assertIsOff()
    }

    private fun cleanupState() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        appContext.filesDir.resolve("datastore").deleteRecursively()
        appContext.getDatabasePath("my_finances.db").delete()
        appContext.getDatabasePath("my_finances.db-shm").delete()
        appContext.getDatabasePath("my_finances.db-wal").delete()
        File(appContext.applicationInfo.dataDir, "shared_prefs").deleteRecursively()
        WorkManager.getInstance(appContext).cancelAllWork().result.get()
        (appContext.applicationContext as MyTestApplication).resetAppComponent()
    }

    private fun waitForMainScreen() {
        composeTestRule.waitForIdle()
        composeTestRule.waitUntil(timeoutMillis = 15_000) {
            composeTestRule.onAllNodesWithText(expensesText).fetchSemanticsNodes().isNotEmpty()
        }
    }
}