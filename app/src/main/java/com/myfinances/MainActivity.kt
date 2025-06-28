package com.myfinances

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.myfinances.ui.navigation.RootNavigationGraph
import com.myfinances.ui.theme.MyFinancesTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Единственная Activity в приложении, служащая точкой входа для всего UI.
 * Аннотация `@AndroidEntryPoint` необходима для внедрения зависимостей
 * в Activity с помощью Hilt.
 *
 * Основная задача этой Activity — настроить тему Jetpack Compose
 * и запустить корневой навигационный граф [RootNavigationGraph].
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyFinancesTheme {
                RootNavigationGraph()
            }
        }
    }
}
