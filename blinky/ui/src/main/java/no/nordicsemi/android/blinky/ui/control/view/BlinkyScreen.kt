package no.nordicsemi.android.blinky.ui.control.view

import android.content.pm.ActivityInfo
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import no.nordicsemi.android.blinky.spec.Gamepad
import no.nordicsemi.android.blinky.ui.R
import no.nordicsemi.android.blinky.ui.control.viewmodel.BlinkyViewModel
import no.nordicsemi.android.common.logger.view.LoggerAppBarIcon
import no.nordicsemi.android.common.permissions.ble.RequireBluetooth
import no.nordicsemi.android.common.ui.view.NordicAppBar
import no.nordicsemi.android.scanner.view.DeviceConnectingView
import no.nordicsemi.android.scanner.view.DeviceDisconnectedView
import no.nordicsemi.android.scanner.view.Reason

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun BlinkyScreen(
    onNavigateUp: () -> Unit,
) {
    var isGamepadVisible by remember { mutableStateOf(false) }
    val viewModel: BlinkyViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        NordicAppBar(
            title = { Text(text = viewModel.deviceName) },
            onNavigationButtonClick = onNavigateUp,
            actions = {
                LoggerAppBarIcon(onClick = { viewModel.openLogger() })
            }
        )
        RequireBluetooth {
            when (state) {
                Gamepad.State.LOADING -> {
                    (LocalContext.current as? ComponentActivity)?.requestedOrientation =
                        ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED

                    DeviceConnectingView(
                        modifier = Modifier.padding(16.dp),
                    ) { padding ->
                        Button(
                            onClick = onNavigateUp,
                            modifier = Modifier.padding(padding),
                        ) {
                            Text(text = stringResource(id = R.string.action_cancel))
                        }
                    }
                }
                Gamepad.State.READY -> {
                    // Force landscape when the gamepad is visible
                    (LocalContext.current as? ComponentActivity)?.requestedOrientation =
                        ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

                    val t by viewModel.t.collectAsStateWithLifecycle()
                    BlinkyControlView(
                        t = t,
                        setGamepadState = viewModel::setGamepadState,
                        modifier = Modifier
                            .widthIn(max = 460.dp)
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp)
                    )
                }
                Gamepad.State.NOT_AVAILABLE -> {
                    (LocalContext.current as? ComponentActivity)?.requestedOrientation =
                        ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED

                    DeviceDisconnectedView(
                        reason = Reason.LINK_LOSS,
                        modifier = Modifier.padding(16.dp),
                    ) { padding ->
                        Button(
                            onClick = { viewModel.connect() },
                            modifier = Modifier.padding(padding),
                        ) {
                            Text(text = stringResource(id = R.string.action_retry))
                        }
                    }
                }
            }
        }
    }
}