package com.myfinances

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.myfinances.domain.entity.ColorPalette
import com.myfinances.domain.entity.ThemeSetting
import com.myfinances.domain.usecase.GetColorPaletteUseCase
import com.myfinances.domain.usecase.GetThemeUseCase
import com.myfinances.ui.navigation.RootNavigationGraph
import com.myfinances.ui.theme.MyFinancesTheme
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var getThemeUseCase: GetThemeUseCase

    @Inject
    lateinit var getColorPaletteUseCase: GetColorPaletteUseCase

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as MyFinancesApplication).appComponent.inject(this)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themeState by getThemeUseCase().collectAsStateWithLifecycle(
                initialValue = ThemeSetting.LIGHT
            )
            val paletteState by getColorPaletteUseCase().collectAsStateWithLifecycle(
                initialValue = ColorPalette.default
            )

            val useDarkTheme = themeState == ThemeSetting.DARK

            MyFinancesTheme(
                darkTheme = useDarkTheme,
                palette = paletteState
            ) {
                RootNavigationGraph()
            }
        }
    }
}