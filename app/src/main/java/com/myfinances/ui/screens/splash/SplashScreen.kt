package com.myfinances.ui.screens.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.myfinances.R
import kotlinx.coroutines.delay

/**
 * Экран-заставка (Splash Screen).
 * Отображает Lottie-анимацию при запуске приложения и по ее завершении
 * вызывает коллбэк для перехода на основной экран.
 *
 * @param onSplashFinished Коллбэк, который будет вызван по окончании анимации.
 */
@Composable
fun SplashScreen(onSplashFinished: () -> Unit) {
    val composition by rememberLottieComposition(
        spec = LottieCompositionSpec.RawRes(R.raw.splash_animation)
    )

    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = 1,
        speed = 1f
    )

    LaunchedEffect(progress) {
        if (progress == 1f) {
            delay(200)
            onSplashFinished()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        LottieAnimation(
            composition = composition,
            progress = { progress },
            modifier = Modifier.fillMaxSize(0.75f)
        )
    }
}