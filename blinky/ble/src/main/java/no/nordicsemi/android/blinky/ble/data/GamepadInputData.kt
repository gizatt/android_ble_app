package no.nordicsemi.android.blinky.ble.data

import no.nordicsemi.android.ble.data.Data
import no.nordicsemi.android.blinky.spec.GamepadInput

class GamepadInputData private constructor() {

    companion object {
        fun from(data: GamepadInput): Data {
            val buffer = ByteArray(9)
            buffer[0] = if (data.enable) 0x01 else 0x00
            buffer[1] = data.speed.toByte()
            buffer[2] = data.leftJoystickX
            buffer[3] = data.leftJoystickY
            buffer[4] = data.rightJoystickX
            buffer[5] = data.rightJoystickY
            buffer[6] = data.pitch
            buffer[7] = data.roll
            buffer[8] = data.height
            return Data(buffer)
        }
    }

}