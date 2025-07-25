package com.myfinances.ui.screens.sync_frequency

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.myfinances.R
import com.myfinances.domain.entity.SyncFrequency

@Composable
fun SyncFrequencyScreen(
    viewModel: SyncFrequencyViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = uiState.selectedFrequencyLabel,
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(vertical = 24.dp)
                .fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        val steps = SyncFrequency.entries.size - 2
        val sliderPosition = frequencyToSliderPosition(uiState.currentFrequency)

        Slider(
            value = sliderPosition,
            onValueChange = { newPosition ->
                val newFrequency = sliderPositionToFrequency(newPosition)
                if (newFrequency != uiState.currentFrequency) {
                    viewModel.onEvent(SyncFrequencyEvent.OnFrequencySelected(newFrequency))
                }
            },
            valueRange = 0f..(SyncFrequency.entries.size - 1).toFloat(),
            steps = steps
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = stringResource(id = R.string.sync_frequency_description),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private fun frequencyToSliderPosition(frequency: SyncFrequency): Float {
    return SyncFrequency.entries.indexOf(frequency).toFloat()
}

private fun sliderPositionToFrequency(position: Float): SyncFrequency {
    return SyncFrequency.entries.getOrElse(position.toInt()) { SyncFrequency.default }
}