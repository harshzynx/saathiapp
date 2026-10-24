package harshzynx.saathi.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

/**
 * A custom drawn plant that "grows" based on the growthLevel (0.0 to 1.0)
 */
@Composable
fun PlantVisual(growthLevel: Float, modifier: Modifier = Modifier) {
    val animatedGrowth by animateFloatAsState(
        targetValue = growthLevel.coerceIn(0.1f, 1.0f),
        animationSpec = tween(durationMillis = 2000),
        label = "plantGrowth"
    )

    Box(modifier = modifier, contentAlignment = Alignment.BottomCenter) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val centerX = width / 2
            val bottomY = height

            // Draw Pot
            drawRect(
                color = Color(0xFF8D6E63), // Brown pot
                topLeft = Offset(centerX - 20.dp.toPx(), bottomY - 20.dp.toPx()),
                size = Size(40.dp.toPx(), 20.dp.toPx())
            )

            // Draw Stem
            val stemHeight = (height * 0.6f) * animatedGrowth
            drawLine(
                color = Color(0xFF4CAF50),
                start = Offset(centerX, bottomY - 20.dp.toPx()),
                end = Offset(centerX, bottomY - 20.dp.toPx() - stemHeight),
                strokeWidth = 4.dp.toPx()
            )

            // Draw Leaves based on growth
            if (animatedGrowth > 0.3f) {
                drawLeaf(centerX, bottomY - 20.dp.toPx() - (stemHeight * 0.4f), isLeft = true)
            }
            if (animatedGrowth > 0.6f) {
                drawLeaf(centerX, bottomY - 20.dp.toPx() - (stemHeight * 0.7f), isLeft = false)
            }
            if (animatedGrowth > 0.9f) {
                // Top Flower/Bud
                drawCircle(
                    color = Color(0xFFFF4081),
                    radius = 8.dp.toPx(),
                    center = Offset(centerX, bottomY - 20.dp.toPx() - stemHeight)
                )
            }
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawLeaf(x: Float, y: Float, isLeft: Boolean) {
    val leafPath = Path().apply {
        moveTo(x, y)
        if (isLeft) {
            quadraticBezierTo(x - 30.dp.toPx(), y - 10.dp.toPx(), x - 10.dp.toPx(), y - 30.dp.toPx())
            quadraticBezierTo(x - 5.dp.toPx(), y - 15.dp.toPx(), x, y)
        } else {
            quadraticBezierTo(x + 30.dp.toPx(), y - 10.dp.toPx(), x + 10.dp.toPx(), y - 30.dp.toPx())
            quadraticBezierTo(x + 5.dp.toPx(), y - 15.dp.toPx(), x, y)
        }
    }
    drawPath(path = leafPath, color = Color(0xFF81C784))
}
