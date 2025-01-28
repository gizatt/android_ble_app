package no.nordicsemi.android.blinky.spec

import kotlinx.coroutines.flow.StateFlow

interface Gamepad {

    enum class State {
        LOADING,
        READY,
        NOT_AVAILABLE
    }

    /**
     * Connects to the device.
     */
    suspend fun connect()

    /**
     * Disconnects from the device.
     */
    fun release()

    /**
     * The current state of the connection.
     */
    val state: StateFlow<State>

    /**
     * Current time on device.
     */
    val t: StateFlow<Double>

    /**
     * Set gamepad state.
     */
    suspend fun setGamepadState(enable: Boolean, leftJoystickX: Byte, leftJoystickY: Byte, rightJoystickX: Byte, rightJoystickY: Byte)
}