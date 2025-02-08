package no.nordicsemi.android.blinky.ui.control.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import no.nordicsemi.android.blinky.spec.GamepadInput

@Composable
internal fun GamepadView(
    setGamepadState: (GamepadInput) -> Unit,
    modifier: Modifier = Modifier,
) {
    var isEnabled by remember { mutableStateOf(false) }
    var speed by remember { mutableFloatStateOf(0.25f) }
    var joystick1X by remember { mutableFloatStateOf(0f) }
    var joystick1Y by remember { mutableFloatStateOf(0f) }
    var joystick2X by remember { mutableFloatStateOf(0f) }
    var joystick2Y by remember { mutableFloatStateOf(0f) }

    // Centralized function to send the gamepad state
    fun sendGamepadState() {
        setGamepadState(
            GamepadInput(
            isEnabled,
            (speed*255).toInt().toUByte(),
            (joystick1X*127).toInt().toByte(),
            (joystick1Y*127).toInt().toByte(),
            (joystick2X*127).toInt().toByte(),
            (joystick2Y*127).toInt().toByte()
            )
        )
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // Toggle Button (Switch)
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text = "Enable:", fontSize = 18.sp)
            Spacer(modifier = Modifier.width(16.dp))
            Switch(
                checked = isEnabled,
                onCheckedChange = {
                    isEnabled = it
                    sendGamepadState()
                }
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = "Speed: %.02f".format(speed), fontSize= 18.sp)
            Spacer(modifier = Modifier.width(4.dp))
            Slider(
                value = speed,
                onValueChange = { speed = it },
                valueRange = 0f..1f,
                steps=100,
                onValueChangeFinished = {
                    sendGamepadState()
                },)
        }

        // Joysticks
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom
        ) {
            ThumbJoystick(
                onPositionChange = { x, y ->
                    joystick1X = x
                    joystick1Y = y
                    sendGamepadState()
                },
                modifier = Modifier.size(150.dp)
            )
            ThumbJoystick(
                onPositionChange = { x, y ->
                    joystick2X = x
                    joystick2Y = y
                    sendGamepadState()
                },
                modifier = Modifier.size(150.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}


// Stub implementation as a named function
fun stubSetGamepadState(input: GamepadInput) {
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