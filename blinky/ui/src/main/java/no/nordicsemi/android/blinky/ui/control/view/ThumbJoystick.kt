package no.nordicsemi.android.blinky.ui.control.view

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import no.nordicsemi.android.blinky.ui.R
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun ThumbJoystick(
    onPositionChange: (x: Float, y: Float) -> Unit,
    modifier: Modifier = Modifier
) {
    var center by remember { mutableStateOf(Offset.Zero) }
    var currentOffset by remember { mutableStateOf(Offset.Zero) }
    var isDragging by remember { mutableStateOf(false) }

    val animatedX by animateFloatAsState(
        targetValue = currentOffset.x,
        label="animatedX"
    )
    val animatedY by animateFloatAsState(
        targetValue = currentOffset.y,
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
                        val radius = intArrayOf(size.width, size.height).min() / 2f
                        val deadzone = intArrayOf(size.width, size.height).min() / 20f
                        currentOffset = change.position - center
                        val distance = currentOffset.getDistance()
                        if (distance < deadzone) {
                            currentOffset = Offset.Zero
                        }
                        else if (distance > radius) {
                            currentOffset = currentOffset.times(radius / distance)
                        } else {
                            currentOffset = currentOffset.times( (distance - deadzone) / (radius - deadzone))
                        }
                        onPositionChange(currentOffset.x/radius, currentOffset.y/radius)
                    },
                    onDragEnd = {
                        isDragging = false
                        currentOffset = Offset.Zero
                        onPositionChange(0.0F, 0.0F)
                    },
                    onDragCancel = {
                        isDragging = false
                    }
                )
            }
    ) {
        Canvas(modifier = Modifier.aspectRatio(1f)) {
            center = Offset(size.width / 2, size.height / 2)
            drawCircle(
                color = Color(0xFFFFD700),
                radius = size.width / 2f,
                center = center + Offset(animatedX, animatedY)
            )
            drawCircle(
                color = Color(0xFF000000),
                radius = size.width / 2f - 10,
                center = center + Offset(animatedX, animatedY)
            )
            drawCircle(
                color = Color(0xFFFFFFFF),
                radius = size.width / 8f,
                center = center * 0.5F + Offset(animatedX, animatedY)
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
        modifier = Modifier
            .size(256.dp, 256.dp)
            .padding(16.dp),
    )
}
