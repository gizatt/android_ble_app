package no.nordicsemi.android.blinky.ui.control.view

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun ThumbJoystick(
    onPositionChange: (x: Float, y: Float) -> Unit,
    modifier: Modifier = Modifier
) {
    var center by remember { mutableStateOf(Offset.Zero) }
    var currentPosition by remember { mutableStateOf(Offset.Zero) }
    var isDragging by remember { mutableStateOf(false) }

    val animatedX by animateFloatAsState(
        targetValue = if (isDragging) currentPosition.x else center.x,
        label="animatedX"
    )
    val animatedY by animateFloatAsState(
        targetValue = if (isDragging) currentPosition.y else center.y,
        label="animatedY"
    )

    Box(
        modifier = modifier
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = {
                        isDragging = true
                    },
                    onDrag = { change, _ ->
                        currentPosition = change.position
                        val radius = size.width / 2f
                        val delta = currentPosition - center
                        val distance = delta.getDistance()
                        if (distance > radius) {
                            val angle = atan2(delta.y, delta.x)
                            currentPosition = Offset(
                                (radius + radius * cos(angle)),
                                (radius + radius * sin(angle))
                            )
                        }
                        val normalizedX = (currentPosition.x / size.width)
                        val normalizedY = (currentPosition.y / size.height)
                        onPositionChange(normalizedX, normalizedY)
                    },
                    onDragEnd = {
                        isDragging = false
                        val normalizedX = (center.x / size.width)
                        val normalizedY = (center.y / size.height)
                        onPositionChange(normalizedX, normalizedY)
                    },
                    onDragCancel = {
                        isDragging = false
                    }
                )
            }
            .background(Color.LightGray)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            center = Offset(size.width / 2, size.height / 2)
            drawCircle(
                color = Color.hsv(24.0F, 0.6F, 0.8F),
                radius = size.width / 2f,
                center = center
            )
            drawCircle(
                color = Color.hsv(240.0F, 0.1F, 0.1F),
                radius = size.width / 4f,
                center = Offset(animatedX, animatedY)
            )
        }
    }
}

@Preview
@Composable
private fun ThumbJoystickPreview() {
    ThumbJoystick(
        onPositionChange = { x, y ->
            println("Position changed: x=$x, y=$y")
        },
        modifier = Modifier.size(256.dp, 256.dp).padding(16.dp),
    )
}
