package no.nordicsemi.android.blinky.ui.control.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.height
import no.nordicsemi.android.blinky.spec.Gamepad
import no.nordicsemi.android.blinky.spec.GamepadInput
import no.nordicsemi.android.blinky.ui.control.viewmodel.GamepadUIOutput


@Composable
internal fun GamepadView(
    setGamepadState: (GamepadUIOutput) -> Unit,
    modifier: Modifier = Modifier,
) {
    var isEnabled by remember { mutableStateOf(false) }
    var speed by remember { mutableFloatStateOf(0.25f) }
    var joystick1X by remember { mutableFloatStateOf(0f) }
    var joystick1Y by remember { mutableFloatStateOf(0f) }
    var joystick2X by remember { mutableFloatStateOf(0f) }
    var joystick2Y by remember { mutableFloatStateOf(0f) }
    var height by remember { mutableFloatStateOf(-1.0f) }
    var motionEnable by remember { mutableStateOf(false) }

    // Centralized function to send the gamepad state
    fun sendGamepadState() {
        setGamepadState(
            GamepadUIOutput(
            isEnabled,speed, joystick1X, joystick1Y, joystick2X, joystick2Y,
                height, motionEnable,
            )
        )
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // Toggle Button (Switch)
        Row(
            modifier = Modifier.fillMaxWidth().height(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text = "Enable:")
            Spacer(modifier = Modifier.width(16.dp))
            Switch(
                checked = isEnabled,
                onCheckedChange = {
                    isEnabled = it
                    sendGamepadState()
                },
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = "Speed: %.02f".format(speed))
            Spacer(modifier = Modifier.width(4.dp))
            Slider(
                value = speed,
                onValueChange = { speed = it },
                valueRange = 0f..1f,
                steps=100,
                onValueChangeFinished = {
                    sendGamepadState()
                })
        }
        Row(
            modifier = Modifier.fillMaxWidth().height(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text = "Motion control:")
            Spacer(modifier = Modifier.width(16.dp))
            Switch(
                checked = motionEnable,
                onCheckedChange = {
                    motionEnable = it
                    sendGamepadState()
                }
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = "Height: %.02f".format(height))
            Spacer(modifier = Modifier.width(4.dp))
            Slider(
                value = height,
                onValueChange = {
                    height = it
                    sendGamepadState()
                                },
                valueRange = -1f..1f,
                steps=100,
                onValueChangeFinished = {
                    sendGamepadState()
                })
        }

        // Joysticks
        Row(
            modifier = Modifier.fillMaxWidth().fillMaxHeight(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.Bottom
        ) {
            ThumbJoystick(
                onPositionChange = { x, y ->
                    joystick1X = x
                    joystick1Y = y
                    sendGamepadState()
                },
                modifier = Modifier.fillMaxHeight()
            )
            ThumbJoystick(
                onPositionChange = { x, y ->
                    joystick2X = x
                    joystick2Y = y
                    sendGamepadState()
                },
                modifier = Modifier.fillMaxHeight()
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}


// Stub implementation as a named function
fun stubSetGamepadState(input: GamepadUIOutput) {
    println(
        "Gamepad state changed: $input"
    )
}

@Preview(widthDp = 700, heightDp = 400)
@Composable
private fun GamepadViewPreview() {
    GamepadView(
        setGamepadState = ::stubSetGamepadState,
        modifier = Modifier.padding(16.dp),
    )
}