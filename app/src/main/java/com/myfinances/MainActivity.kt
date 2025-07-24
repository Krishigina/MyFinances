package com.myfinances

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.myfinances.domain.entity.ThemeSetting
import com.myfinances.domain.usecase.GetThemeUseCase
import com.myfinances.ui.navigation.RootNavigationGraph
import com.myfinances.ui.theme.MyFinancesTheme
import javax.inject.Inject

class MainActivity : ComponentActivity() {

    @Inject
    lateinit var getThemeUseCase: GetThemeUseCase

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as MyFinancesApplication).appComponent.inject(this)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themeState by getThemeUseCase().collectAsStateWithLifecycle(
                initialValue = ThemeSetting.LIGHT
            )
            val useDarkTheme = themeState == ThemeSetting.DARK
            MyFinancesTheme(darkTheme = useDarkTheme) {
                RootNavigationGraph()
            }
        }
    }
}