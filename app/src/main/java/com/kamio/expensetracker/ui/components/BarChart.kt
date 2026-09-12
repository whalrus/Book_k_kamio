package com.kamio.expensetracker.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.max

data class BarEntry(val label: String, val value: Float, val color: Color)

/**
 * Simple vertical bar chart, e.g. for spend-per-month trend.
 */
@Composable
fun BarChart(
    entries: List<BarEntry>,
    modifier: Modifier = Modifier,
    barLabelFormatter: (Float) -> String = { it.toInt().toString() }
) {
    val maxValue = max(entries.maxOfOrNull { it.value } ?: 0f, 0.0001f)

    Column(modifier = modifier) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(horizontal = 8.dp, vertical = 8.dp)
        ) {
            if (entries.isEmpty()) return@Canvas
            val slotWidth = size.width / entries.size
            val barWidth = slotWidth * 0.5f

            entries.forEachIndexed { index, entry ->
                val barHeight = (entry.value / maxValue) * size.height
                val left = index * slotWidth + (slotWidth - barWidth) / 2f
                val top = size.height - barHeight
                drawRoundRect(
                    color = entry.color,
                    topLeft = Offset(left, top),
                    size = Size(barWidth, barHeight),
                    cornerRadius = CornerRadius(8f, 8f)
                )
            }
        }

        androidx.compose.foundation.layout.Row(modifier = Modifier.fillMaxWidth()) {
            entries.forEach { entry ->
                Text(
                    text = entry.label,
                    modifier = Modifier
                        .padding(2.dp)
                        .weight(1f),
                    fontSize = 11.sp,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}
