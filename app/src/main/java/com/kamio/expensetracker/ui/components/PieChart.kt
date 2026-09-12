package com.kamio.expensetracker.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.background
import androidx.compose.ui.unit.dp
import com.kamio.expensetracker.ui.theme.ChartPalette
import kotlin.math.max

data class PieSlice(val label: String, val value: Float, val color: Color)

/**
 * Simple donut chart with a legend below it. No external chart library required.
 */
@Composable
fun PieChart(
    slices: List<PieSlice>,
    modifier: Modifier = Modifier
) {
    val total = max(slices.sumOf { it.value.toDouble() }.toFloat(), 0.0001f)

    Column(modifier = modifier) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .padding(16.dp)
        ) {
            var startAngle = -90f
            val strokeWidth = size.minDimension * 0.28f
            slices.forEach { slice ->
                val sweep = 360f * (slice.value / total)
                drawArc(
                    color = slice.color,
                    startAngle = startAngle,
                    sweepAngle = sweep,
                    useCenter = false,
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = strokeWidth)
                )
                startAngle += sweep
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            slices.forEach { slice ->
                val pct = if (total > 0) (slice.value / total * 100) else 0f
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Spacer(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(slice.color)
                    )
                    Text("${slice.label}  ·  ${"%.0f".format(pct)}%")
                }
            }
        }
    }
}

fun defaultSliceColors(count: Int): List<Color> =
    List(count) { ChartPalette[it % ChartPalette.size] }
