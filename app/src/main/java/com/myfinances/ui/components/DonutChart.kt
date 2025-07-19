package com.myfinances.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.myfinances.ui.model.CategorySpendingUiModel

@Composable
fun DonutChart(
    modifier: Modifier = Modifier,
    items: List<CategorySpendingUiModel>
) {
    val totalSum = items.sumOf { it.percentage }.toFloat()
    if (totalSum == 0f) return

    val animatedProgress = remember { Animatable(0f) }

    LaunchedEffect(items) {
        animatedProgress.snapTo(0f)
        animatedProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1000)
        )
    }

    val sweepAngles = items.map {
        360 * (it.percentage / totalSum)
    }

    BoxWithConstraints(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        val strokeWidth = 8.dp
        val strokeWidthPx = with(LocalDensity.current) { strokeWidth.toPx() }
        val chartSize = if (this.maxWidth < this.maxHeight) this.maxWidth else this.maxHeight

        Canvas(
            modifier = Modifier.size(chartSize)
        ) {
            var startAngle = -90f
            for (i in items.indices) {
                drawArc(
                    color = items[i].color,
                    startAngle = startAngle,
                    sweepAngle = sweepAngles[i] * animatedProgress.value,
                    useCenter = false,
                    style = Stroke(width = strokeWidthPx, cap = StrokeCap.Butt)
                )
                startAngle += sweepAngles[i] * animatedProgress.value
            }
        }

        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            items.forEach { item ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        modifier = Modifier.size(5.dp),
                        shape = CircleShape,
                        color = item.color
                    ) {}
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = "${item.percentage}% ${item.title}",
                        fontSize = 9.sp
                    )
                }
            }
        }
    }
}