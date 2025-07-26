package com.myfinances.data.manager

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import com.myfinances.domain.entity.HapticEffect
import com.myfinances.domain.repository.SessionRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class HapticFeedbackManager @Inject constructor(
    private val context: Context,
    private val sessionRepository: SessionRepository
) {
    private val vibrator: Vibrator? by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager =
                context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
    }

    private val scope = CoroutineScope(Dispatchers.Default)

    fun performHapticFeedback() {
        scope.launch {
            val settingsEnabled = sessionRepository.getHapticsEnabled().first()
            if (settingsEnabled) {
                val effectType = sessionRepository.getHapticEffect().first()
                val vibrationEffect = createVibrationEffect(effectType)
                vibrator?.vibrate(vibrationEffect)
            }
        }
    }

    fun previewHapticEffect(effect: HapticEffect) {
        scope.launch {
            val settingsEnabled = sessionRepository.getHapticsEnabled().first()
            if(settingsEnabled) {
                val vibrationEffect = createVibrationEffect(effect)
                vibrator?.vibrate(vibrationEffect)
            }
        }
    }

    private fun createVibrationEffect(effect: HapticEffect): VibrationEffect {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            when (effect) {
                HapticEffect.CLICK -> VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK)
                HapticEffect.DOUBLE_CLICK -> VibrationEffect.createPredefined(VibrationEffect.EFFECT_DOUBLE_CLICK)
                HapticEffect.TICK -> VibrationEffect.createPredefined(VibrationEffect.EFFECT_TICK)
            }
        } else {
            @Suppress("DEPRECATION")
            when (effect) {
                HapticEffect.CLICK -> VibrationEffect.createOneShot(20, VibrationEffect.DEFAULT_AMPLITUDE)
                HapticEffect.DOUBLE_CLICK -> VibrationEffect.createWaveform(longArrayOf(0, 20, 50, 20), -1)
                HapticEffect.TICK -> VibrationEffect.createOneShot(10, 100) // 100 - более слабая амплитуда
            }
        }
    }
}