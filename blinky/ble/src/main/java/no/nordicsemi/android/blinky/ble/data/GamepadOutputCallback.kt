package no.nordicsemi.android.blinky.ble.data

import android.bluetooth.BluetoothDevice
import no.nordicsemi.android.ble.callback.profile.ProfileReadResponse
import no.nordicsemi.android.ble.data.Data

abstract class GamepadOutputCallback: ProfileReadResponse() {

    override fun onDataReceived(device: BluetoothDevice, data: Data) {
        if (data.size() == 4) {
            // Milliseconds come in; convert to float for state.
            val t = data.getIntValue(Data.FORMAT_UINT32_LE, 0)
            onGamepadOutputStateChanged(device, t!!.toDouble() / 1000.0)
        } else {
            onInvalidDataReceived(device, data)
        }
    }

    abstract fun onGamepadOutputStateChanged(device: BluetoothDevice, t: Double)
}