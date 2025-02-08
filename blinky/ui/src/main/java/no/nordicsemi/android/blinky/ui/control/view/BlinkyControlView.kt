package no.nordicsemi.android.blinky.ui.control.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import no.nordicsemi.android.blinky.spec.GamepadInput
import no.nordicsemi.android.blinky.ui.control.viewmodel.GamepadUIOutput

@Composable
internal fun BlinkyControlView(
    t: Double,
    setGamepadState: (GamepadUIOutput) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        GamepadView(setGamepadState = setGamepadState)
//        GamepadOutputStateView(
//            t = t
//        )
    }
}

@Preview(widthDp = 700, heightDp = 400)
@Composable
private fun BlinkyControlViewPreview() {
    BlinkyControlView(
        t = 1.23,
        setGamepadState = ::stubSetGamepadState,
        modifier = Modifier.padding(16.dp),
    )
}