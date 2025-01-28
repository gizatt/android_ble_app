package no.nordicsemi.android.blinky.spec

import java.util.UUID

class GamepadSpec {

    companion object {
        val GAMEPAD_SERVICE_UUID: UUID = UUID.fromString("198a8000-2ab7-414c-9459-47e3d418a7fd")
        val GAMEPAD_INPUT_CHARACTERISTIC_UUID: UUID = UUID.fromString("198a8001-2ab7-414c-9459-47e3d418a7fd")
        val GAMEPAD_OUTPUT_CHARACTERISTIC_UUID: UUID = UUID.fromString("198a8002-2ab7-414c-9459-47e3d418a7fd")
    }

}