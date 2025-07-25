package com.myfinances.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.myfinances.R

@Composable
fun PinNumpad(
    onNumberClick: (String) -> Unit,
    onBackspaceClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val buttons = listOf(
        "1", "2", "3",
        "4", "5", "6",
        "7", "8", "9",
        "", "0", "backspace"
    )

    Column(
        modifier = modifier.padding(horizontal = 48.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        buttons.chunked(3).forEach { row ->
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                row.forEach { button ->
                    NumpadButton(
                        value = button,
                        onClick = {
                            if (it == "backspace") {
                                onBackspaceClick()
                            } else {
                                onNumberClick(it)
                            }
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun NumpadButton(
    value: String,
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (value.isEmpty()) {
        Box(modifier = modifier.aspectRatio(1f))
        return
    }

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(CircleShape)
            .clickable { onClick(value) },
        contentAlignment = Alignment.Center
    ) {
        if (value == "backspace") {
            Icon(
                painter = painterResource(id = R.drawable.ic_pin_backspace),
                contentDescription = "Backspace",
                tint = MaterialTheme.colorScheme.onBackground
            )
        } else {
            Text(
                text = value,
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}