package no.nordicsemi.android.blinky.spec

class GamepadInput(
    var enable: Boolean,
    var speed: UByte,
    var leftJoystickX: Byte,
    var leftJoystickY: Byte,
    var rightJoystickX: Byte,
    var rightJoystickY: Byte,
    var pitch: Byte,
    var roll: Byte,
    var height: Byte,
) {
}