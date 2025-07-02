import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
fun CustomCircularProgressIndicator(
    modifier: Modifier = Modifier,
    outerColor: Color = Color(0xFFEF1E93),  // Pink color
    innerColor: Color = Color(0xFFEF1E93),  // Same pink for inner arc
    outerStrokeWidth: Float = 6f,
    innerStrokeWidth: Float = 4f
) {
    // Infinite animation transition
    val infiniteTransition = rememberInfiniteTransition(label = "progress_rotation")

    // Animate outer arc (clockwise)
    val outerRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "outer_rotation"
    )

    // Animate inner arc (counter-clockwise)
    val innerRotation by infiniteTransition.animateFloat(
        initialValue = 360f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "inner_rotation"
    )

    // Drawing box and canvas
    Box(modifier = modifier.size(48.dp)) {
        Canvas(modifier = Modifier.size(48.dp)) {
            val center = size.width / 2

            // Outer arc
            drawArc(
                color = outerColor,
                startAngle = outerRotation,
                sweepAngle = 270f, // 270° sweep with 90° gap
                useCenter = false,
                style = Stroke(width = outerStrokeWidth, cap = StrokeCap.Round)
            )

            // Inner arc (smaller and counter-rotating)
            val arcSize = Size(size.width * 0.7f, size.height * 0.7f)
            val topLeft = Offset(
                (size.width - arcSize.width) / 2f,
                (size.height - arcSize.height) / 2f
            )

            drawArc(
                color = innerColor,
                startAngle = innerRotation,
                sweepAngle = 240f, // 240° sweep with 120° gap
                useCenter = false,
                style = Stroke(width = innerStrokeWidth, cap = StrokeCap.Round),
                size = arcSize,
                topLeft = topLeft
            )
        }
    }
}
