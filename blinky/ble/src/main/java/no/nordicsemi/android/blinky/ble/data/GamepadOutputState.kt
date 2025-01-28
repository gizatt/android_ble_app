package no.nordicsemi.android.blinky.ble.data

import android.bluetooth.BluetoothDevice

class GamepadOutputState: GamepadOutputCallback() {
    var t: Double = 0.0

    override fun onGamepadOutputStateChanged(device: BluetoothDevice, t: Double) {
        this.t = t
    }
}