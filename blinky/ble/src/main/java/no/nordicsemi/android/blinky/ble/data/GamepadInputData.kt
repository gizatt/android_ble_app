package no.nordicsemi.android.blinky.ble.data

import no.nordicsemi.android.ble.data.Data

class GamepadInputData private constructor() {

    companion object {
        fun from(enable: Boolean, leftJoystickX: Byte, leftJoystickY: Byte, rightJoystickX: Byte, rightJoystickY: Byte): Data {
            val buffer = ByteArray(5)
            buffer[0] = if (enable) 0x01 else 0x00
            buffer[1] = leftJoystickX
            buffer[2] = leftJoystickY
            buffer[3] = rightJoystickX
            buffer[4] = rightJoystickY
            return Data(buffer)
        }
    }

}