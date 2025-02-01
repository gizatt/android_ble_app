package no.nordicsemi.android.blinky.ble

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.content.Context
import android.util.Log
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import no.nordicsemi.android.ble.BleManager
import no.nordicsemi.android.ble.ktx.asValidResponseFlow
import no.nordicsemi.android.ble.ktx.getCharacteristic
import no.nordicsemi.android.ble.ktx.state.ConnectionState
import no.nordicsemi.android.ble.ktx.stateAsFlow
import no.nordicsemi.android.ble.ktx.suspend
import no.nordicsemi.android.blinky.ble.data.GamepadInputData
import no.nordicsemi.android.blinky.ble.data.GamepadOutputCallback
import no.nordicsemi.android.blinky.ble.data.GamepadOutputState
import no.nordicsemi.android.blinky.spec.Gamepad
import no.nordicsemi.android.blinky.spec.GamepadSpec
import timber.log.Timber

class GamepadManager(
    context: Context,
    device: BluetoothDevice
): Gamepad by GamepadManagerImpl(context, device)

private class GamepadManagerImpl(
    context: Context,
    private val device: BluetoothDevice,
): BleManager(context), Gamepad {
    private val scope = CoroutineScope(Dispatchers.IO)

    private var gamepadInputCharacteristic: BluetoothGattCharacteristic? = null
    private var gamepadOutputCharacteristic: BluetoothGattCharacteristic? = null

    private val _gamepadOutputState = MutableStateFlow(0.0)
    override val t = _gamepadOutputState.asStateFlow()

    private val _gamepadInputState = MutableStateFlow(GamepadInputData.from(false, 0, 0, 0, 0))

    override val state = stateAsFlow()
        .map {
            when (it) {
                is ConnectionState.Connecting,
                is ConnectionState.Initializing -> Gamepad.State.LOADING
                is ConnectionState.Ready -> Gamepad.State.READY
                is ConnectionState.Disconnecting,
                is ConnectionState.Disconnected -> Gamepad.State.NOT_AVAILABLE
            }
        }
        .stateIn(scope, SharingStarted.Lazily, Gamepad.State.NOT_AVAILABLE)


    private val gamepadOutputCallback by lazy {
        object : GamepadOutputCallback() {
            override fun onGamepadOutputStateChanged(device: BluetoothDevice, t: Double) {
                _gamepadOutputState.tryEmit(t)
            }
        }
    }


    override suspend fun connect() = connect(device)
        .retry(3, 300)
        .useAutoConnect(false)
        .timeout(3000)
        .suspend()

    override fun release() {
        // Cancel all coroutines.
        scope.cancel()

        val wasConnected = isReady
        // If the device wasn't connected, it means that ConnectRequest was still pending.
        // Cancelling queue will initiate disconnecting automatically.
        cancelQueue()

        // If the device was connected, we have to disconnect manually.
        if (wasConnected) {
            disconnect().enqueue()
        }
    }

    override suspend fun setGamepadState(enable: Boolean, leftJoystickX: Byte, leftJoystickY: Byte, rightJoystickX: Byte, rightJoystickY: Byte) {
        // Write the value to the characteristic.
        _gamepadInputState.value = GamepadInputData.from(enable, leftJoystickX, leftJoystickY, rightJoystickX, rightJoystickY)
    }

    override fun log(priority: Int, message: String) {
        Timber.log(priority, message)
    }

    override fun getMinLogPriority(): Int {
        // By default, the library logs only INFO or
        // higher priority messages. You may change it here.
        return Log.VERBOSE
    }

    override fun isRequiredServiceSupported(gatt: BluetoothGatt): Boolean {
        // Get the LBS Service from the gatt object.
        gatt.getService(GamepadSpec.GAMEPAD_SERVICE_UUID)?.apply {
            // Get the LED characteristic.
            gamepadOutputCharacteristic = getCharacteristic(
                GamepadSpec.GAMEPAD_OUTPUT_CHARACTERISTIC_UUID,
                // Mind, that below we pass required properties.
                // If your implementation supports only WRITE_NO_RESPONSE,
                // change the property to BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE.
                BluetoothGattCharacteristic.PROPERTY_NOTIFY
            )
            // Get the Button characteristic.
            gamepadInputCharacteristic = getCharacteristic(
                GamepadSpec.GAMEPAD_INPUT_CHARACTERISTIC_UUID,
                BluetoothGattCharacteristic.PROPERTY_WRITE
            )

            // Return true if all required characteristics are supported.
            return gamepadOutputCharacteristic != null && gamepadInputCharacteristic != null
        }
        return false
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun initialize() {
        // Enable notifications for the gamepad's output characteristic.
        val flow: Flow<GamepadOutputState> = setNotificationCallback(gamepadOutputCharacteristic)
            .asValidResponseFlow()

        // Forward that state to its relevant flow.
        scope.launch {
            flow.map { it.t }.collect { _gamepadOutputState.tryEmit(it) }
        }

        scope.launch {
            while (true) {
                writeCharacteristic(
                    gamepadInputCharacteristic,
                    _gamepadInputState.value,
                    BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
                ).await()
                // Read the initial value of the LED characteristic.
                // Shouldn't be necessary because of notify but this is working for now.
                readCharacteristic(gamepadOutputCharacteristic)
                    .with(gamepadOutputCallback)
                    .await()
                // Alas any faster and things lag
                delay(100)
            }
        }
    }

    override fun onServicesInvalidated() {
        gamepadInputCharacteristic = null
        gamepadOutputCharacteristic = null
    }
}