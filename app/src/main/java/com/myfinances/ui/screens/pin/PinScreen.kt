package com.myfinances.ui.screens.pin

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.myfinances.ui.components.PinDotsIndicator
import com.myfinances.ui.components.PinNumpad

@Composable
fun PinScreen(
    navController: NavController,
    viewModel: PinScreenViewModel,
    onAuthSuccess: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        when (val state = uiState) {
            is PinScreenUiState.Loading -> {
                CircularProgressIndicator()
            }

            is PinScreenUiState.Success -> {
                PinScreenContent(
                    state = state,
                    viewModel = viewModel,
                    onAuthSuccess = onAuthSuccess,
                    navController = navController
                )
            }
        }
    }
}

@Composable
private fun PinScreenContent(
    state: PinScreenUiState.Success,
    viewModel: PinScreenViewModel,
    navController: NavController,
    onAuthSuccess: () -> Unit
) {
    if (state.navigateToMain) {
        LaunchedEffect(Unit) {
            onAuthSuccess()
        }
    }

    if (state.navigateBack) {
        LaunchedEffect(Unit) {
            navController.popBackStack()
        }
    }

    val shake = remember { Animatable(0f) }
    LaunchedEffect(state.error) {
        if (state.error != null) {
            for (i in 0..7) {
                shake.animateTo(
                    targetValue = if (i % 2 == 0) 15f else -15f,
                    animationSpec = tween(50)
                )
            }
            shake.animateTo(0f, animationSpec = tween(50))
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(64.dp))
        Text(
            text = stringResource(id = state.titleRes),
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(modifier = Modifier.height(48.dp))
        Box(modifier = Modifier.padding(start = shake.value.dp)) {
            PinDotsIndicator(pinLength = state.enteredPin.length)
        }
        Spacer(modifier = Modifier.weight(1f))
        PinNumpad(
            onNumberClick = { viewModel.onEvent(PinScreenEvent.OnNumberClick(it)) },
            onBackspaceClick = { viewModel.onEvent(PinScreenEvent.OnBackspaceClick) }
        )
        Spacer(modifier = Modifier.height(48.dp))
    }
}